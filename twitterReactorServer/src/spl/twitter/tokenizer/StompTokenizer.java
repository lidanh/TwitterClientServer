package spl.twitter.tokenizer;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

/**
 * @name Stomp Tokenizer
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Tokenizing messages from the buffer into valid STOMP frames
 */
public interface StompTokenizer<T> {
   /**
    * Add some bytes to the message stream.
    * @param bytes an array of bytes to be appended to the message stream.
    */
   void addBytes(ByteBuffer bytes);

   /**
    * Is there a complete message ready?.
    * @return true the next call to nextMessage() will not return null, false otherwise.
    */
   boolean hasMessage();

   /**
    * Get the next complete message if it exists, advancing the tokenizer to the next message.
    * @return the next complete message, and null if no complete message exist.
    */
   T nextMessage();

   /**
    * Convert the String message into bytes representation, taking care of encoding and framing.
    * @return a ByteBuffer with the message content converted to bytes, after framing information has been added.
    */
   ByteBuffer getBytesForMessage(T msg) throws CharacterCodingException;
}
