package spl.twitter.engine;

import java.util.Date;

/**
 * @name Message
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * User posted message
 */
public class Message {
    private String destination;
    private String subscriptionId;
    private String message;
    private long timestamp;

    /**
     * Create new message
     * @param destination
     * @param subscriptionId
     * @param message
     */
    public Message(String destination, String subscriptionId, String message) {
        this.destination = destination;
        this.subscriptionId = subscriptionId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @return the subscriptionId
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return new Date(this.timestamp).toString();
    }
}
