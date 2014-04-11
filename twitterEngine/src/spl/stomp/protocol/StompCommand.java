package spl.stomp.protocol;

/**
 * @name Stomp Command
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Represents the supported STOMP commands
 */
public class StompCommand {
    public final static String ENCODING = "UTF-8";
    private String _command;
    
    private StompCommand(String msg) {
        _command = msg;
    }
    
    public static StompCommand
        SEND = new StompCommand("SEND"),
        SUBSCRIBE = new StompCommand("SUBSCRIBE"),
        UNSUBSCRIBE = new StompCommand("UNSUBSCRIBE"),
        DISCONNECT = new StompCommand("DISCONNECT"),
        CONNECT = new StompCommand("CONNECT"),
        MESSAGE = new StompCommand("MESSAGE"),
        CONNECTED = new StompCommand("CONNECTED"),
        RECEIPT = new StompCommand("RECEIPT"),
        ERROR = new StompCommand("ERROR");

    public static StompCommand valueOf(String v) {
        v = v.trim();
        if (v.equals("SEND")) return SEND;
        else if (v.equals("SUBSCRIBE")) return SUBSCRIBE;
        else if (v.equals("UNSUBSCRIBE")) return UNSUBSCRIBE;
        else if (v.equals("CONNECT")) return CONNECT;
        else if (v.equals("MESSAGE")) return MESSAGE;
        else if (v.equals("CONNECTED")) return CONNECTED;
        else if (v.equals("DISCONNECT")) return DISCONNECT;
        else if (v.equals("RECEIPT")) return RECEIPT;
        else if (v.equals("ERROR")) return ERROR;
        else return null;
    }

    @Override
    public String toString() {
        return _command;
    }
}
