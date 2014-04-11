package spl.twitter.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.DisconnectFrame;
import spl.stomp.protocol.StompFrame;
import spl.twitter.engine.AsyncConnection;
import spl.twitter.engine.AsyncProtocol;
import spl.twitter.engine.AsyncServerListener;
import spl.twitter.tokenizer.StompTokenizer;

/**
 * @name Connection Handler
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Handles messages from clients
 */
public class ConnectionHandler<T> implements AsyncConnection<T> {
    
    private final String clientDisconnectReceipt = "-1";

    private static final int BUFFER_SIZE = 1024;

    protected final SocketChannel _sChannel;

    protected final ReactorData<T> _data;

    protected final AsyncProtocol<T> _protocol;
    protected final StompTokenizer<T> _tokenizer;

    protected Vector<ByteBuffer> _outData = new Vector<ByteBuffer>();

    protected final SelectionKey _skey;

    private static final Logger logger = LogManager.getLogger(ConnectionHandler.class.getName());

    private ProtocolTask<T> _task = null;

    private AsyncServerListener<T> _reactor;
    
    private Queue<T> _messagesQueue;

    /**
     * Creates a new ConnectionHandler object
     * 
     * @param sChannel
     *            the SocketChannel of the client
     * @param data
     *            a reference to a ReactorData object
     */
    private ConnectionHandler(SocketChannel sChannel, ReactorData<T> data,
            SelectionKey key, AsyncServerListener<T> reactor) {
        _sChannel = sChannel;
        _data = data;
        _protocol = _data.getProtocolMaker().create(this);
        _tokenizer = _data.getTokenizerMaker().create();
        _skey = key;
        _reactor = reactor;
        _messagesQueue = new LinkedBlockingQueue<T>();
    }

    // make sure 'this' does not escape b4 the object is fully constructed!
    private void initialize() {
        _skey.attach(this);
        _task = new ProtocolTask<T>(_protocol, _tokenizer, this);
    }

    public static <T> ConnectionHandler<T> create(SocketChannel sChannel,
            ReactorData<T> data, SelectionKey key, AsyncServerListener<T> reactor) {
        ConnectionHandler<T> h = new ConnectionHandler<T>(sChannel, data, key, reactor);
        h.initialize();
        return h;
    }

    public synchronized void addOutData(ByteBuffer buf) {
        _outData.add(buf);
        switchToReadWriteMode();
    }

    private void closeConnection() {
        // remove from the selector.
        _skey.cancel();
        try {
            _sChannel.close();
        } catch (IOException ignored) {
            ignored = null;
        }
    }

    /**
     * Reads incoming data from the client:
     * <UL>
     * <LI>Reads some bytes from the SocketChannel
     * <LI>create a protocolTask, to process this data, possibly generating an
     * answer
     * <LI>Inserts the Task to the ThreadPool
     * </UL>
     * 
     * @throws
     * 
     * @throws IOException
     *             in case of an IOException during reading
     */
    public void read() {
        // do not read if protocol has terminated. only write of pending data is
        // allowed
        if (_protocol.shouldClose()) {
            return;
        }

        SocketAddress address = _sChannel.socket().getRemoteSocketAddress();

        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
        int numBytesRead = 0;
        try {
            numBytesRead = _sChannel.read(buf);
        } catch (IOException e) {
            numBytesRead = -1;
        }
        // is the channel closed??
        if (numBytesRead == -1) {
            // No more bytes can be read from the channel
            logger.info("client on {} has disconnected", address);
            closeConnection();
            // tell the protocol that the connection terminated.
            _protocol.processMessage((T)new DisconnectFrame(clientDisconnectReceipt));
            _protocol.connectionTerminated();
            return;
        }

        // add the buffer to the protocol task
        buf.flip();
        _task.addBytes(buf);
        // add the protocol task to the reactor
        _data.getExecutor().execute(_task);
    }

    /**
     * attempts to send data to the client<br/>
     * if all the data has been successfully sent, the ConnectionHandler will
     * automatically switch to read only mode, otherwise it'll stay in it's
     * current mode (which is read / write).
     * 
     * @throws IOException
     *             if the write operation fails
     * @throws ClosedChannelException
     *             if the channel have been closed while registering to the
     *             Selector
     */
    public synchronized void write() {
        if (_outData.size() == 0) {
            // if nothing left in the output string, go back to read mode
            switchToReadOnlyMode();
            return;
        }
        // if there is something to send
        ByteBuffer buf = _outData.remove(0);
        if (buf.remaining() != 0) {
            try {
                _sChannel.write(buf);
            } catch (IOException e) {
                // this should never happen.
                e.printStackTrace();
            }
            // check if the buffer contains more data
            if (buf.remaining() != 0) {
                _outData.add(0, buf);
            }
        }
        // check if the protocol indicated close.
        if (_protocol.shouldClose()) {
            switchToWriteOnlyMode();
            if (buf.remaining() == 0) {
                closeConnection();
                SocketAddress address = _sChannel.socket()
                        .getRemoteSocketAddress();
                logger.info("disconnecting client on {}", address);
            }
        }
    }

    /**
     * switches the handler to read / write TODO Auto-generated catch blockmode
     * 
     * @throws ClosedChannelException
     *             if the channel is closed
     */
    public void switchToReadWriteMode() {
        _skey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        _data.getSelector().wakeup();
    }

    /**
     * switches the handler to read only mode
     * 
     * @throws ClosedChannelException
     *             if the channel is closed
     */
    public void switchToReadOnlyMode() {
        _skey.interestOps(SelectionKey.OP_READ);
        _data.getSelector().wakeup();
    }

    /**
     * switches the handler to write only mode
     * 
     * @throws ClosedChannelException
     *             if the channel is closed
     */
    public void switchToWriteOnlyMode() {
        _skey.interestOps(SelectionKey.OP_WRITE);
        _data.getSelector().wakeup();
    }

    @Override
    public void send(T frame) {
        _messagesQueue.add(frame);
        flushMessages();
    }
    
    protected void flushMessages() {
        if (_protocol.isAuthorized()) {
            while (!_messagesQueue.isEmpty()) {
                T frame = _messagesQueue.poll();
                try {
                    addOutData(_tokenizer.getBytesForMessage(frame));
                    write();
                    logger.debug("The following frame was sent to the client: \n{}", frame);
                } catch (CharacterCodingException e) {
                    logger.catching(e);
                }
            }
        }
    }

    @Override
    public void close() {
        _protocol.connectionTerminated();
        closeConnection();
    }

    @Override
    public boolean isClosed() {
        return !_sChannel.isConnected();
    }
    
    @Override
    public String toString() {
        return String.format("%s:%s", _sChannel.socket().getInetAddress(), _sChannel.socket().getPort());
    }

}
