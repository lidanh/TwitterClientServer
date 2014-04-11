package spl.twitter.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.DisconnectFrame;
import spl.stomp.protocol.StompFrame;

/**
 * @name Stomp Frame Tokenizer
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Responsible of tokinizing the input stream into protocol frames
 */
public class StompFrameTokenizer implements StompTokenizer {
    private final String clientDisconnectReceipt = "-1";
    private static final Logger logger = LogManager.getLogger(StompFrameTokenizer.class.getName());

    /**
     * Create new stomp tokenizer
     * @param charset
     */
    public StompFrameTokenizer(Charset charset) {
    }
    
    /**
     * Get the next complete frame if it exists, advancing the tokenizer to
     * the next message.
     * 
     * @return the next complete frame, and null if no complete frame exist.
     */
    @Override
    public StompFrame getFrame(BufferedReader buffer) {
        StringBuilder frameStr = new StringBuilder();
        String line = "";

        try {
            // read whole message until delimiter character
            while (!(line = buffer.readLine()).endsWith(StompFrame.DELIMITER)) {
                frameStr.append(line).append("\n");
            }
        } catch (IOException e) {
            return new DisconnectFrame(clientDisconnectReceipt);
        } catch (NullPointerException e) {
            // client closed the connection gracelessly
            // returns Disconnect frame to disconnect the user gracefully
            return new DisconnectFrame(clientDisconnectReceipt);
        }
        
        logger.debug("Received data from client: \n{}", frameStr);
        
        StompFrame result = StompFrame.parse(frameStr.toString());
        
        return result;
    }
}