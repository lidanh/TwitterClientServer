package spl.twitter.engine;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.StompException;


/**
 * @name Twitter Engine
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Twitter Engine- manage the backend algorithms, data, etc. (Singleton object)
 */
public class TwitterEngine {
    /** Path for topics, only for compliance with ActiveMQ **/
    public static final String TOPICS_LOCATION = "/topic/";
    /** Sample rate for statistics **/
    private static final long STATS_SAMPLE_RATE = 5000; // 5 seconds
    private static final Logger logger = LogManager.getLogger(TwitterEngine.class.getName());
    /** Regex for finding tweet mention (@) **/
    private static final String MENTION_PATTERN = "(?<=^|(?<=[^a-zA-Z0-9-_\\.]))@([A-Za-z]+[A-Za-z0-9]+)";
    /** TwitterEngine instance (Singleton) **/
    private static TwitterEngine instance;
    
    /** ENGINE DATA **/
    private Map<String, Topic> topics = new ConcurrentHashMap<String, Topic>();
    private Map<String, User> users = new ConcurrentHashMap<String, User>();
    private ServerTopic serverTopic;
    private AsyncServerListener<?> listener;
    
    /** STATS VALUES **/
    private volatile int totalMessagesPosted = 0;
    private double avgPostTime = 0;
    private volatile int maxMessagesPerPeriod = 0;
    private final long serverStartTime;
    Timer statsTimer = new Timer();

    /**
     * Create new Twitter Engine Object
     */
    protected TwitterEngine() {
        // create new server topic and add it to the topics container
        this.serverTopic = new ServerTopic();
        topics.put(this.serverTopic.getTopicName(), this.serverTopic);
        
        // schedule a timer task for statistics
        statsTimer.schedule(new TimerTask() {
            private int lastTotal = 0;

            @Override
            public void run() {
                int totalMessagePostedInPeriod = totalMessagesPosted - lastTotal;
                lastTotal = totalMessagesPosted;
                if (totalMessagePostedInPeriod > maxMessagesPerPeriod)
                    maxMessagesPerPeriod = totalMessagePostedInPeriod;
            }
        }, 0, TwitterEngine.STATS_SAMPLE_RATE);
       
        // Save the server start time for calculating statistics 
        this.serverStartTime = System.currentTimeMillis();
    }

    /**
     * Get TwitterEngine Instance
     * @return TwitterEngine Instance
     */
    public static TwitterEngine getInstance() {
        if (instance == null) {
            instance = new TwitterEngine();
        }

        return instance;
    }

    /**
     * Set Server Connections Listener (The object that accepts new connections)
     * @param listener
     * @see spl.twitter.engine.AsyncServerListener
     */
    public void setConnectionsListener(AsyncServerListener<?> listener) {
        this.listener = listener;
    }

    /**
     * User login / create
     * @param login
     * @param password
     * @param protocol
     * @return AuthorizationResult object that represents the result of the authorization- succeed or not, user object, etc.
     */
    public AuthorizationResult connect(String login, String password,
            AsyncProtocol<?> protocol) {
        logger.trace("@{} is trying to login...", login);
        
        // check if user exists
        if (users.containsKey(login)) {
            // user exists
            if (users.get(login).isConnected()) {
                // already logged in
                logger.error("@{} is already logged in.", login);
                users.get(login).setProtocol(protocol);
                return new AuthorizationResult("Already logged in", null);
            } else if (users.get(login).login(password, protocol)) {
                // login successfully!
                User user = users.get(login);
                logger.info("@{} is now connected!", login);
                return new AuthorizationResult(user);
            } else {
                // wrong password
                logger.error("@{} entered wrong password", login);
                return new AuthorizationResult("Login Error", "Password is wrong");
            }
        } else {
            // New user
            // create user, create topic and register the user to the topic
            // create a new user and add him to the users repository
            User user = new User(login, password, protocol);
            users.put(login, user);
            // create a new topic for this user
            Topic userTopic = new Topic(login);
            topics.put(login, userTopic);
            // register the user to his own topic
            userTopic.setTopicUser(user);

            logger.info("@{} was successfuly created and now logged in!", login);
            return new AuthorizationResult(user);
        }
    }

    /**
     * Close the engine, stop the stats timer
     */
    public void close() {
        this.statsTimer.cancel();
    }

    /**
     * Subscribe to a topic
     * @param subscriptionId
     * @param name
     * @param user
     * @throws StompException
     */
    public void subscribe(String subscriptionId, String name, User user) throws StompException {
        logger.trace("@{} is trying to follow @{} with id #{}", user, name, subscriptionId);

        if (name.equals(user.getUsername())) {
            // Follow self
            throw new StompException("Subscribe", "You cannot following on yourself.");
        }

        // if topic exists
        if (topics.containsKey(name)) {
            if (!user.hasSubscription(subscriptionId)) {
                // if user has not already subscribe to the given topic
                user.subscribeTo(topics.get(name), subscriptionId);
                logger.info("@{} is now following @{}", user, name);
            } else {
                // Already following
                logger.error("@{} is already has subscription #{}", user, subscriptionId);
                throw new StompException("Subscribe", "Already Following");
            }
        } else {
            // topic not found in the server
            logger.error("topic {} was not found", name);
            throw new StompException("Subscribe", "Topic was not found.");
        }
    }

    /**
     * Unsubscribe from a topic (by the given subscriptionId)
     * @param subscriptionId
     * @param user
     * @return the name of the topic the the user was unsubscribed from
     * @throws StompException
     */
    public String unsubscribe(String subscriptionId, User user) throws StompException {
        logger.trace("@{} would like to unsubscribe #{}", user, subscriptionId);

        // check if user has subscription of the given subscription id
        if (user.hasSubscription(subscriptionId)) {
            String unsubscribedTopic = user.getSubscribedTopic(subscriptionId).getTopicName();
            // unsubscribe from topic
            user.unsubscribe(subscriptionId);
            logger.info("@{} doesnt follow @{} anymore", user, unsubscribedTopic);
            
            return unsubscribedTopic;
        } else {
            // Not following
            throw new StompException("Unsubscription error", "User does not have subscription id #" + subscriptionId);
        }
    }

    /**
     * Post a message in the server
     * @param postedUser
     * @param destinationTopic
     * @param message
     * @throws StompException
     */
    public synchronized void send(User postedUser, String destinationTopic, String message) throws StompException {
        // check if the given topic is exists
        if (!topics.containsKey(destinationTopic)) {
            // topic not found
            logger.error("Topic for @{} was not found", destinationTopic);
            throw new StompException("No topic found", "FATAL Error! Please contact server administator ASAP");
        }

        // check if the destination topic is the server topic,
        // so it handles the given server command
        if (destinationTopic.equals(ServerTopic.SERVER_TOPIC_NAME)) {
            processServerMessage(message);
            return;
        } else if (!postedUser.getUsername().equals(destinationTopic)) {
            // deny the message if the user tries to post on behalf other user (Security issues)
            logger.fatal("@{} tried to posted on behalf @{}. due to security issues this action is prohibited.", postedUser, destinationTopic);
            throw new StompException("Oops.", "You cannot post on behalf other user.\nThis action was reported to the server administator.");
        }

        logger.trace("@{} posted: {}", postedUser, message);

        long startTime = System.currentTimeMillis();
        // send to topic users
        Topic destination = topics.get(destinationTopic);
        List<String> delivered = destination.send(message);

        // send to users that follows on mentioned users
        // ignore the users that the message already delivered to
        final Pattern p = Pattern.compile(TwitterEngine.MENTION_PATTERN);
        final Matcher m = p.matcher(message);
        while (m.find()) {
            // mentioned users
            String mentionedUser = m.group(1).trim();
            destination.incrementMentions();

            // if the server contains the mentioned user, 
            // post the message to its topic's subscribers
            if (topics.containsKey(mentionedUser)) {
                Topic t = topics.get(mentionedUser);
                t.send(message, delivered);
                t.incrementMentioned();
            }
        }

        totalMessagesPosted++;
        avgPostTime = (avgPostTime + (System.currentTimeMillis() - startTime)) / totalMessagesPosted;
    }

    /**
     * Process server commands
     * Currently supported commands:
     * - stop - Stop the server and disconnect all the clients
     * - clients - View server registered users
     * - clients online - view server online users
     * - stats - view server statistics
     * @param message
     */
    private void processServerMessage(String message) {
        StringBuilder result = new StringBuilder();

        // remove trailing chars
        message = message.trim().replace("\r", "");

        if (message.startsWith("clients")) {
            // clients command
            
            logger.info("{}: ", message);
            for (User user : getUsers()) {
                if (message.equals("clients online")) {
                    // clients online command
                    if (user.isConnected()) result.append(user.getUsername()).append(' ');
                } else {
                    result.append(user.getUsername()).append(' ');
                }
            }
        } else if (message.equals("stats")) {
            // stats command
            logger.info("Server statistics:");
            result.append(getStats());
        } else if (message.equals("stop")) {
            // stop command
            
            logger.info("stop command was triggered.  disconnect all clients...");
            // stop stats timers
            statsTimer.cancel();
            // logout all the online users
            for (User user : getUsers()) {
                user.logout();
            }

            // kill server listener
            if (listener != null) {
                try {
                    listener.kill();
                } catch (IOException e) {
                    logger.catching(e);
                }
            }

            return;
        }

        this.serverTopic.send(result.toString());
    }

    /**
     * Get server users iterator
     * @return server users iterator
     */
    private Iterable<User> getUsers() {
        return users.values();
    }

    /**
     * Server statistics
     * @return formatted string contains server statistics
     */
    private String getStats() {
        StringBuilder sb = new StringBuilder();

        sb.append("Twitter Statistics\n");
        sb.append("====================\n");
        sb.append("@ Max number of tweets per 5 seconds: ").append(getMaxMessagesPerPeriod()).append('\n');
        sb.append("@ Avg. number of tweets per 5 seconds: ").append(getAvgMessagesPerPeriod()).append('\n');
        sb.append("@ Avg. time to pass a tweet to all users following an account: ").append(getAvgTimeToPostMessage()).append(" ms.\n");
        Topic maxFollowersTopic = getUserWithMaxNumberOfSubscribers();
        if (maxFollowersTopic != null)
            sb.append("@ user with the maximum number of followers: ").append(maxFollowersTopic.getTopicName()).append(" (").append(maxFollowersTopic.getTotalSubscribers()).append(" followers)").append('\n');

        Topic maxTweetsUser = getUserWithMaxNumberOfTweets();
        if (maxTweetsUser != null)
            sb.append("@ user with the maximum number of tweets: ").append(maxTweetsUser).append(" (").append(maxTweetsUser.getTotalMessagesPosted()).append(" tweets)").append('\n');

        Topic maxMentionedUser = getUserThatMentionedMost();
        if (maxMentionedUser != null)
            sb.append("@ user with the maximum mentions in other followers tweets: ").append(maxMentionedUser).append(" (").append(maxMentionedUser.getTotalMentioned()).append(" mentioneds)").append('\n');

        Topic maxMentionsUser = getUserWithMaxNumberOfMentions();
            if (maxMentionsUser != null)
        sb.append("@ user with the maximum number of mentions in her own tweets: ").append(maxMentionsUser).append(" (").append(maxMentionedUser.getTotalMentioned()).append(" mentions)").append('\n');

        return sb.toString();
    }

    /** STATS **/
    /**
     * Get max messages posted per period (default: 5 seconds)
     * @return max count
     */
    private synchronized int getMaxMessagesPerPeriod() {
        return this.maxMessagesPerPeriod;
    }

    /**
     * Get average messages posted per period (default: 5 seconds)
     * @return average
     */
    private synchronized double getAvgMessagesPerPeriod() {
        long samples = (int)((System.currentTimeMillis() - this.serverStartTime) / TwitterEngine.STATS_SAMPLE_RATE);
        return this.totalMessagesPosted / (samples == 0 ? 1 : samples);
    }

    /**
     * Get average time to post message
     * @return average time to post message (in ms)
     */
    private synchronized double getAvgTimeToPostMessage() {
        return this.avgPostTime;
    }

    /**
     * Get the user with the max number of subscribers
     * @return topic object that represents the user with the max number of subscribers
     */
    private synchronized Topic getUserWithMaxNumberOfSubscribers() {
        return Collections.max(topics.values(), new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                return o1.getTotalSubscribers() - o2.getTotalSubscribers();
            }
        });
    }

    /**
     * Get the user that posted maximum number of tweets
     * @return topic object that represents the user that posted maximum number of tweets
     */
    private synchronized Topic getUserWithMaxNumberOfTweets() {
        return Collections.max(topics.values(), new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                return o1.getTotalMessagesPosted() - o2.getTotalMessagesPosted();
            }
        });
    }

    /**
     * Get the user that mentioned at most in other tweets
     * @return topic object that represents the user that mentioned at most in other tweets
     */
    private synchronized Topic getUserThatMentionedMost() {
        return Collections.max(topics.values(), new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                return o1.getTotalMentioned() - o2.getTotalMentioned();
            }
        });
    }

    /**
     * Get the user that mentioned other users in his own tweets at most
     * @return topic object that represents the user that mentioned other users in his own tweets at most
     */
    private synchronized Topic getUserWithMaxNumberOfMentions() {
        return Collections.max(topics.values(), new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                return o1.getTotalMentions() - o2.getTotalMentions();
            }
        });
    }
}
