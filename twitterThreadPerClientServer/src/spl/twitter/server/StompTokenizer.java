package spl.twitter.server;
import java.io.BufferedReader;
import java.io.IOException;

import spl.stomp.protocol.StompFrame;
 
/**
 * @name Stomp Tokenizer
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * This interface care of tokenizing an input stream into protocol specific
 * messages.
 * 
 */
public interface StompTokenizer {
    /**
     * @return the next token, or null if no token is available. Pay attention
     *         that a null return value does not indicate the stream is closed,
     *         just that there is no message pending.
     * @throws IOException to indicate that the connection is closed.
     */
    StompFrame getFrame(BufferedReader br);
}