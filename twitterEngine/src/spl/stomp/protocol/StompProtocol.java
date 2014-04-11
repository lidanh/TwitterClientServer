package spl.stomp.protocol;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.twitter.engine.AsyncConnection;
import spl.twitter.engine.AsyncProtocol;
import spl.twitter.engine.AuthorizationResult;
import spl.twitter.engine.TwitterEngine;
import spl.twitter.engine.User;

/**
 * An implementation of the STOMP protocol
 */
public class StompProtocol implements AsyncProtocol<StompFrame> {
    private static final Logger logger = LogManager.getLogger(StompProtocol.class.getName());
    private boolean shouldClose = false;
    private TwitterEngine engine;
    private User currentUser;
    private AsyncConnection<StompFrame> connection;

    public StompProtocol(AsyncConnection<StompFrame> connection) {
        this.connection = connection;
        this.engine = TwitterEngine.getInstance();
    }

    /**
     * processes a message received as a frame encoded in UTF-16 Incoming
     * messages: CONNECT, DISCONNECT, SUBSCRIBE, UNSUBSCRIBE, SEND
     * 
     * @param msg
     *            the message to process (a frame without end delimiter)
     * @return always NULL except for the case of CONNECT (all messages are
     *         one-way)
     */
    @Override
    public StompFrame processMessage(StompFrame frame) {
        // check for parsing errors
        if (frame == null) {
            return new ErrorFrame("Frame error.");
        } else if (frame.getCommand() == StompCommand.ERROR) {
            return frame;
        }

        if (this.isEnd(frame)) {
            // disconnect user
            logger.info("{} has disconnected.", (isAuthorized()) ? currentUser : connection);
            if (currentUser != null) {
                this.currentUser.logout();
                this.currentUser = null;
            }
            
            connectionTerminated();
            
            return new ReceiptFrame(frame.header("receipt"));
        }
        
        // not connected
        if (currentUser == null && frame.getCommand() != StompCommand.CONNECT) {
            logger.info("Frame was received successfully but client is not authenticated to twitter service.");
            return new ErrorFrame("Action cannot be completed. Please login.");
        }

        // process frame
        StompFrame resultFrame = null;
        try {
            if (frame.getCommand() == StompCommand.CONNECT) {
                // connect
                resultFrame = connect(frame);
            } else if (frame.getCommand() == StompCommand.SUBSCRIBE) {
                // subscribe
                resultFrame = subscribe(frame);
            } else if (frame.getCommand() == StompCommand.UNSUBSCRIBE) {
                // unsubscribe
                resultFrame = unsubscribe(frame);
            } else if (frame.getCommand() == StompCommand.SEND) {
                // send
                send(frame);
            }
        } catch (StompException e) {
            resultFrame = e.generateErrorFrame();
        } catch (Exception e) {
            logger.catching(e);
            resultFrame = new ErrorFrame(e.getMessage(), e.toString());
        }

        return resultFrame;
    }

    /**
     * @param frame
     * @throws StompException
     */
    private void send(StompFrame frame) throws StompException {
        engine.send(
                currentUser,
                frame.header("destination").replace(
                        TwitterEngine.TOPICS_LOCATION, ""),
                frame.getBody());
    }

    /**
     * @param frame
     * @throws StompException
     */
    private StompFrame unsubscribe(StompFrame frame) throws StompException {
        String unfollowedUser = engine.unsubscribe(frame.header("id"), currentUser);
        return new MessageFrame(TwitterEngine.TOPICS_LOCATION + currentUser.getUsername(), frame.header("id"), UUID.randomUUID().toString(), "unfollowing " + unfollowedUser);
    }

    /**
     * @param frame
     * @throws StompException
     */
    private StompFrame subscribe(StompFrame frame) throws StompException {
        engine.subscribe(
                frame.header("id"),
                frame.header("destination").replace(
                        TwitterEngine.TOPICS_LOCATION, ""), currentUser);
        
        return new MessageFrame(TwitterEngine.TOPICS_LOCATION
                + currentUser.getUsername(), frame.header("id"), UUID.randomUUID()
                .toString(), "following " + frame.header("destination").replace(
                        TwitterEngine.TOPICS_LOCATION,""));
    }

    /**
     * @param frame
     * @return
     */
    private StompFrame connect(StompFrame frame) {
        StompFrame resultFrame;
        AuthorizationResult connResult = engine.connect(
                frame.header("login"), frame.header("passcode"), this);
        if (connResult.isSucceed()) {
            // user is connected
            this.currentUser = connResult.getCurrentUser();
            resultFrame = new ConnectedFrame(UUID.randomUUID()
                    .toString());
        } else {
            // error while connecting
            resultFrame = new ErrorFrame(connResult.getErrorTopic(),
                    connResult.getErrorMessage());
        }
        return resultFrame;
    }

    /**
     * determine whether the given message is the termination message
     * 
     * @param msg
     *            the message to examine
     * @return false - this simple protocol doesn't allow termination...
     */
    @Override
    public boolean isEnd(StompFrame frame) {
        return frame.getCommand() == StompCommand.DISCONNECT;
    }

    /**
     * Is the protocol in a closing state?
     * 
     * @return true if the protocol is in closing state.
     */
    @Override
    public boolean shouldClose() {
        return this.shouldClose;
    }

    /**
     * Indicate to the protocol that the client disconnected.
     */
    @Override
    public void connectionTerminated() {
        this.shouldClose = true;
    }

    @Override
    public void sendMessage(String from, String subscriptionId, String msg) {
        this.connection.send(new MessageFrame(TwitterEngine.TOPICS_LOCATION
                + from, subscriptionId, UUID.randomUUID().toString(), msg));
    }
    
    @Override
    public boolean isAuthorized() {
        return this.currentUser != null;
    }
}
