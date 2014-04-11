package spl.twitter.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spl.stomp.protocol.StompException;

/**
 * @name Topic
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Topic represents a "passive" object that someone can follow on.
 * Topic can have topicUser- a user that corresponded to the topic (but that's not mandatory- for example server topic)
 */
public class Topic {
    public static final String SELF_ID = "0";
    /* Topic subscribers */
    private Map<User, String> subscribers;
    private User topicUser;
    /* Topic name, most of the time it will be the topic's user name */
    private String topicName;
    private int totalMessagesPosted = 0, totalMentions = 0, totalMentioned = 0;

    /**
     * Create a new topic
     * @param topicName
     */
    public Topic(String topicName) {
        this.topicName = topicName;
        this.subscribers = new HashMap<User, String>();
    }

    /**
     * Set topic user
     * @param user
     */
    public void setTopicUser(User user) {
        // set topic user
        this.topicUser = user;
        // subscribe to self
        subscribers.put(user, Topic.SELF_ID);
        // update user's subscription
        user.subscribeTo(this, Topic.SELF_ID);
    }

    /**
     * Add subscriber to the topic
     * @param user
     * @param subscriptionId
     */
    public void addSubscriber(User user, String subscriptionId) {
        subscribers.put(user, subscriptionId);
    }

    /**
     * Get topic name
     * @return
     */
    public String getTopicName() {
        return topicName;
    }
    
    /**
     * Post message as the topic (send the message to the topic's subscribers)
     * @param message
     * @return List of subscribers that received the message
     */
    public synchronized List<String> send(String message) {
        return send(message, null);
    }

    /**
     * Post message as the topic (send the message to the topic's subscribers, except the given subscribers list)
     * @param message
     * @oaram except
     * @return List of subscribers that received the message
     */
    public synchronized List<String> send(String message, List<String> except) {
        List<String> deliveredUsers = new ArrayList<String>();

        // send to topic's subscribers (except the given subscribers list)
        for (User user : subscribers.keySet()) {
            if (except == null || (except != null && !except.contains(user.getUsername()))) {
                user.send(new Message(this.getTopicName(), subscribers.get(user), message));
                deliveredUsers.add(user.getUsername());
            }
        }
        
        totalMessagesPosted++;

        return deliveredUsers;
    }
    
    /**
     * Get total messages posted in the topic
     * @return
     */
    public int getTotalMessagesPosted() {
        return this.totalMessagesPosted;
    }

    /**
     * Remove subscriber from the topic
     * @param user
     * @throws StompException
     */
    public void removeSubscriber(User user) throws StompException {
        if (isSubscribed(user)) {
            if (this.topicUser != user) {
                subscribers.remove(user);
            } else {
                throw new StompException("Cannot unsubscribe", "You cannot unsubscribe from yourself.");
            }
        } else {
            // error (handled in the client due to assignment definitions failure
        }
    }

    /**
     * Check if the given user is subscribed to this topic
     * @param user
     * @return true if subscribe, false otherwise
     */
    private boolean isSubscribed(User user) {
        return subscribers.containsKey(user);
    }
    
    @Override
    public String toString() {
        return this.topicName;
    }
    
    /**
     * Get topic's subscribers count
     * @return topic's subscribers count
     */
    public int getTotalSubscribers() {
        // minus 1 => self s not counted
        return this.subscribers.size() - 1;
    }
    
    /**
     * Get total times topic's mentioned in others tweets
     * @return total times topic's mentioned in other tweets
     */
    public int getTotalMentioned() {
        return this.totalMentioned;
    }
    
    /**
     * Get total topic's mentions
     * @return total topic's mentions
     */
    public int getTotalMentions() {
        return this.totalMentions;
    }
    
    /**
     * Increment mentions count
     */
    public synchronized void incrementMentions() {
        this.totalMentions++;
    }
    
    /**
     * Increment mentioned count
     */
    public synchronized void incrementMentioned() {
        this.totalMentioned++;
    }
}
