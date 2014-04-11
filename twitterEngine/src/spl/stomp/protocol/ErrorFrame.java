package spl.stomp.protocol;

/**
 * @name Error Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * ERROR stomp frame
 */
class ErrorFrame extends StompFrame {
    public ErrorFrame(String message) {
        super(StompCommand.ERROR);
        addFrameHeader("message", message);
    }
    
    public ErrorFrame(String message, String body) {
        super(StompCommand.ERROR, body);
        addFrameHeader("message", message);
    }
}
