package spl.twitter.tokenizer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.StompFrame;

/**
 * @name Stomp Frame Tokenizer
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Tokenizing messages from the buffer into valid STOMP frames
 */
public class StompFrameTokenizer implements StompTokenizer<StompFrame> {

    private final String _messageSeparator = StompFrame.DELIMITER;

    private final StringBuffer _stringBuf = new StringBuffer();
    /**
     * the fifo queue, which holds data coming from the socket. Access to the
     * queue is serialized, to ensure correct processing order.
     */
    private final Vector<ByteBuffer> _buffers = new Vector<ByteBuffer>();
    
    private static final Logger logger = LogManager.getLogger(StompFrameTokenizer.class.getName());

    private final CharsetDecoder _decoder;
    private final CharsetEncoder _encoder;

    public StompFrameTokenizer(Charset charset) {
        this._decoder = charset.newDecoder();
        this._encoder = charset.newEncoder();
    }

    /**
     * Add some bytes to the message. Bytes are converted to chars, and appended
     * to the internal StringBuffer. Complete messages can be retrieved using
     * the nextMessage() method.
     * 
     * @param bytes
     *            an array of bytes to be appended to the message.
     */
    public synchronized void addBytes(ByteBuffer bytes) {
        _buffers.add(bytes);

    }

    /**
     * Is there a complete message ready?.
     * 
     * @return true the next call to nextMessage() will not return null, false
     *         otherwise.
     */
    public synchronized boolean hasMessage() {
        while (_buffers.size() > 0) {
            ByteBuffer bytes = _buffers.remove(0);
            CharBuffer chars = CharBuffer.allocate(bytes.remaining());
            this._decoder.decode(bytes, chars, false); // false: more bytes may
                                                       // follow. Any unused
                                                       // bytes are kept in the
                                                       // decoder.
            chars.flip();
            this._stringBuf.append(chars);
        }
        return this._stringBuf.indexOf(this._messageSeparator) > -1;
    }

    /**
     * Get the next complete message if it exists, advancing the tokenizer to
     * the next message.
     * 
     * @return the next complete message, and null if no complete message exist.
     */
    public synchronized StompFrame nextMessage() {
        String message = null;
        int messageEnd = this._stringBuf.indexOf(this._messageSeparator);
        if (messageEnd > -1) {
            message = this._stringBuf.substring(0, messageEnd);
            this._stringBuf.delete(0,
                    messageEnd + this._messageSeparator.length());
        }
        
        logger.trace("Received data from client: \n{}", message);
        
        return StompFrame.parse(message);
    }

    /**
     * Convert the String message into bytes representation, taking care of
     * encoding and framing.
     * 
     * @return a ByteBuffer with the message content converted to bytes, after
     *         framing information has been added.
     */
    public ByteBuffer getBytesForMessage(StompFrame frame)
            throws CharacterCodingException {
        StringBuilder sb = new StringBuilder(frame.toString());
        sb.append(this._messageSeparator);
        ByteBuffer bb = this._encoder.encode(CharBuffer.wrap(sb));
        return bb;
    }

}
