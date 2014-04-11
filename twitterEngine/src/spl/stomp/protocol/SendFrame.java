package spl.stomp.protocol;

/**
 * @name Send Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * SEND stomp frame
 */
class SendFrame extends StompFrame {
    public SendFrame() {
        super(StompCommand.SEND);
    }
    
    public SendFrame(String destination, String body) {
        super(StompCommand.SEND, body);
        addFrameHeader("destination", destination);
    }
    
    @Override
    protected String[] getMandatoryHeaders() {
        return new String[] {"destination"};
    }
}
