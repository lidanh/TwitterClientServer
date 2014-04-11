package spl.stomp.protocol;

/**
 * @name Disconnect Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * DISCONNECT stomp frame
 */
public class DisconnectFrame extends StompFrame {
    public DisconnectFrame() {
        super(StompCommand.DISCONNECT);
    }
    
    public DisconnectFrame(String receipt) {
        this();
        addFrameHeader("receipt", receipt);
    }
    
    @Override
    protected String[] getMandatoryHeaders() {
        return new String[] {"receipt"};
    }
}
