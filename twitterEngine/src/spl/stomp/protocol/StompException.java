package spl.stomp.protocol;

/**
 * @name Stomp Exception
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * STOMP Protocol exception, thrown in case of any protocol error
 */
public class StompException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 5608449997581790306L;
    private String errorType;

    public StompException(String errorType, String errorMessage) {
        super(errorMessage);
        this.errorType = errorType;
    }
    
    /**
     * Generate ERROR stomp frame for this exception
     * @return ErrorFrame for the given exception
     */
    public ErrorFrame generateErrorFrame() {
        return new ErrorFrame(errorType, getMessage());
    }
}
