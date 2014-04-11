package spl.twitter.engine;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @name Server Topic
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Represents a special topic for server commands (clients, stats, etc.)
 */
class ServerTopic extends Topic {
    public static final String SERVER_TOPIC_NAME = "server";
    private static final Logger logger = LogManager.getLogger(ServerTopic.class.getName());
    
    public ServerTopic() {
        super(SERVER_TOPIC_NAME);
    }
    
    @Override
    /**
     * log the message in the server's log before sending it to server's subscribers
     */
    public synchronized List<String> send(String message) {
        logger.info(message);
        return super.send(message);
    }
}
