package spl.stomp.protocol;

/**
 * @name Unsubscribe Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * UNSUBSCRIBE stomp frame
 */
class UnsubscribeFrame extends StompFrame {
    public UnsubscribeFrame() {
        super(StompCommand.UNSUBSCRIBE);
    }
    
    public UnsubscribeFrame(String id) {
        super(StompCommand.UNSUBSCRIBE);
        addFrameHeader("id", id);
    }
    
    @Override
    protected String[] getMandatoryHeaders() {
        return new String[] {"id"};
    }
}
