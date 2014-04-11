package spl.stomp.protocol;

/**
 * @name Connected Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * CONNECTED stomp frame
 */
class ConnectedFrame extends StompFrame {
    public ConnectedFrame(String session) {
        super(StompCommand.CONNECTED);
        addFrameHeader("session", session);
        addFrameHeader("version", "1.2");
    }
}
