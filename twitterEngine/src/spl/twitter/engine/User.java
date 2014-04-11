package spl.twitter.engine;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.StompException;

/**
 * @name User
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * User represents an "active" object that actually login and send messages.
 * Each user has also a corresponded topic that represents a "passive" object that someone can follow on, 
 * but topic doesn't have to have a corresponded user (for example- server topic does not have any user)
 */
public class User {
    private static final Logger logger = LogManager.getLogger(User.class.getName());
    private String username;
    private String password;
    /** 
     * User's protocol, used for sending messages to the client.
     * if protocol == null, that's mean that the user is disconnected
     */
    private AsyncProtocol<?> protocol;
    /**
     * User's messages queue
     */
    private Queue<Message> messages;
    /**
     * User's subscribed topics. reset on login (assignment's requirement)
     */
    private Map<String, Topic> subscribedTopics;

    /**
     * Create new user object
     * @param username
     * @param password
     * @param protocol
     */
    public User(String username, String password, AsyncProtocol<?> protocol) {
        this.username = username;
        this.password = password;
        this.protocol = protocol;
        this.messages = new LinkedBlockingQueue<Message>();
        this.subscribedTopics = new ConcurrentHashMap<String, Topic>();
        login(password, protocol);
    }

    /**
     * User login
     * @param password
     * @param protocol
     * @return true if authorized & success, false otherwise
     */
    public synchronized boolean login(String password, AsyncProtocol<?> protocol) {
        // check password
        boolean isApproved = isApproved(password);

        if (isApproved) {
            setProtocol(protocol);
            // flush all the message while the user was disconnected
            flushMessages();
            // reset subscriptions
            resetSubscriptionsInfo();
        }

        return isApproved;
    }

    /**
     * Unsubscribe from all topics that this user follows on
     */
    private synchronized void resetSubscriptionsInfo() {
        try {
            for (Iterator<Map.Entry<String, Topic>> it = subscribedTopics
                    .entrySet().iterator(); it.hasNext();) {
                String subscriptionId = it.next().getKey();
                if (!subscriptionId.equals(Topic.SELF_ID)) {
                    unsubscribe(subscriptionId);
                }
            }
        } catch (StompException e) {

        }
    }

    /**
     * Flush messages to the client (if the client is authorized & connected)
     */
    private synchronized void flushMessages() {
        if (isConnected()) {
            while (!messages.isEmpty()) {
                Message msg = messages.poll();
                this.protocol.sendMessage(msg.getDestination(),
                        msg.getSubscriptionId(), msg.getMessage());
            }
        }
    }

    /**
     * Check if the given password matches the user's password
     * @param password
     * @return true if matches, false otherwise
     */
    protected boolean isApproved(String password) {
        return password.equals(this.password);
    }

    /**
     * Get username
     * @return username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Logout user
     */
    public void logout() {
        setProtocol(null);
    }

    /**
     * Check if user is connected
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return this.protocol != null;
    }
    
    /**
     * Set protocol (means that the user is in connected state)
     * @param protocol
     */
    public void setProtocol(AsyncProtocol<?> protocol) {
        this.protocol = protocol;
    }

    public void send(Message message) {
        messages.add(message);
        flushMessages();
    }

    public void subscribeTo(Topic topic, String subscriptionId) {
        subscribedTopics.put(subscriptionId, topic);
        topic.addSubscriber(this, subscriptionId);
    }

    public void unsubscribe(String id) throws StompException {
        Topic topic = subscribedTopics.get(id);
        if (topic.getTopicName().equals(username)) {
            // user cannot unsubscribe his own topic
            logger.error("@{} tried to unsubscribe from himself", this);
            throw new StompException("Cannot unsubscribe",
                    "You cannot unsubscribe from yourself.");
        }
        subscribedTopics.remove(id);
        topic.removeSubscriber(this);
    }

    public boolean hasSubscription(String subscriptionId) {
        return subscribedTopics.containsKey(subscriptionId);
    }

    @Override
    public boolean equals(Object obj) {
        return this.username == ((User) obj).username;
    }

    public Topic getSubscribedTopic(String subscriptionId) {
        if (subscribedTopics.containsKey(subscriptionId)) {
            return subscribedTopics.get(subscriptionId);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.getUsername();
    }
}
