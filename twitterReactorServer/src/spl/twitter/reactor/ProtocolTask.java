package spl.twitter.reactor;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import spl.stomp.protocol.StompCommand;
import spl.stomp.protocol.StompFrame;
import spl.twitter.engine.AsyncProtocol;
import spl.twitter.tokenizer.StompTokenizer;

/**
 * @name Protocol Task
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask<T> implements Runnable {

    private final AsyncProtocol<T> _protocol;
    private final StompTokenizer<T> _tokenizer;
    private final ConnectionHandler<T> _handler;

    public ProtocolTask(final AsyncProtocol<T> protocol,
            final StompTokenizer<T> tokenizer, final ConnectionHandler<T> h) {
        this._protocol = protocol;
        this._tokenizer = tokenizer;
        this._handler = h;
    }

    // we synchronize on ourselves, in case we are executed by several threads
    // from the thread pool.
    public synchronized void run() {
        // go over all complete messages and process them.
        while (_tokenizer.hasMessage()) {
            T msg = _tokenizer.nextMessage();
            T response = this._protocol.processMessage(msg);
            if (response != null) {
                try {
                    ByteBuffer bytes = _tokenizer.getBytesForMessage(response);
                    this._handler.addOutData(bytes);
                } catch (CharacterCodingException e) {
                    e.printStackTrace();
                }
                
                // if response is CONNECTED frame, flush awaiting messages
                if (response instanceof StompFrame && ((StompFrame) response).getCommand() == StompCommand.CONNECTED) {
                    this._handler.flushMessages();
                }
            }
        }
    }

    public void addBytes(ByteBuffer b) {
        _tokenizer.addBytes(b);
    }
}
