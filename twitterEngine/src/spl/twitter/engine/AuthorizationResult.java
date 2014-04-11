package spl.twitter.engine;

/**
 * @name Authorization Result
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Represents a result of a login- authorization succeed or not, the user object, error messages, etc.
 */
public class AuthorizationResult {
    private boolean isSucceed;
    private String errorMessage;
    private String errorTopic;
    private User currentUser;
    
    protected AuthorizationResult(boolean isSucceed, User currentUser, String errorTopic, String errorMessage) {
        this.isSucceed = isSucceed;
        this.errorTopic = errorTopic;
        this.errorMessage = errorMessage;
        this.currentUser = currentUser;
    }
    
    public AuthorizationResult(User currentUser) {
        this(true, currentUser, null, null);
    }
    
    public AuthorizationResult(String errorTopic, String errorMessage) {
        this(false, null, errorTopic, errorMessage);
    }

    /**
     * @return the isSucceed
     */
    public boolean isSucceed() {
        return isSucceed;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the errorTopic
     */
    public String getErrorTopic() {
        return errorTopic;
    }

    /**
     * @return the currentUser
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
