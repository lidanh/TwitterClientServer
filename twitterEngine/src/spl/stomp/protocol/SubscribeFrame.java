package spl.stomp.protocol;

/**
 * @name Subscribe Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * SUBSCRIBE stomp frame
 */
class SubscribeFrame extends StompFrame {
    public SubscribeFrame() {
        super(StompCommand.SUBSCRIBE);
    }
    
    public SubscribeFrame(String destination, String id) {
        super(StompCommand.SUBSCRIBE);
        addFrameHeader("destination", destination);
        addFrameHeader("id", id);
    }
    
    @Override
    protected String[] getMandatoryHeaders() {
        return new String[] {"destination", "id"};
    }
}
