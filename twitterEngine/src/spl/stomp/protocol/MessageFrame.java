package spl.stomp.protocol;

/**
 * @name Message Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * MESSAGE stomp frame
 */
class MessageFrame extends StompFrame {
    public static final String SERVER_MESSAGE = "-1";
    
    public MessageFrame(String destination, String subscription, String messageId, String body) {
        this(String.valueOf(System.currentTimeMillis() / 1000L), destination, subscription, messageId, body);
    }
    
    public MessageFrame(String timestamp, String destination, String subscription, String messageId, String body) {
        super(StompCommand.MESSAGE, body);
        addFrameHeader("destination", destination);
        addFrameHeader("subscription", subscription);
        addFrameHeader("timestamp", timestamp);
        addFrameHeader("message-id", messageId);
    }
}
