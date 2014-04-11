package spl.stomp.protocol;

/**
 * @name Connect Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * CONNECT stomp frame
 */
class ConnectFrame extends StompFrame {
    public ConnectFrame() {
        super(StompCommand.CONNECT);
    }
    
    public ConnectFrame(String host, String login, String passcode) {
        super(StompCommand.CONNECT);
        addFrameHeader("accept-version", "1.2");
        addFrameHeader("host", host);
        addFrameHeader("login", login);
        addFrameHeader("passcode", passcode);
    }
    
    @Override
    protected String[] getMandatoryHeaders() {
        return new String[] {"accept-version", "host", "login", "passcode"};
    }
}
