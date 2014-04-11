package spl.stomp.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @name StompFrame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Abstract stomp frame, base class for protocol's frames
 */
public abstract class StompFrame {
    public static final String DELIMITER = "\u0000";
    private StompCommand command;
    private Map<String, String> headers;
    private String body;

    /**
     * Create new stomp frame, given command, headers map and body
     * @param command
     * @param headers
     * @param body
     */
    protected StompFrame(StompCommand command, Map<String, String> headers,
            String body) {
        this.command = command;
        this.headers = (headers == null) ? new HashMap<String, String>()
                : headers;
        this.body = (body == null) ? "" : body;
    }

    /**
     * Create new stomp frame, given command and body
     * @param command
     * @param body
     */
    protected StompFrame(StompCommand command, String body) {
        this(command, null, body);
    }

    /**
     * Create new stomp frame, given command only
     * @param command
     */
    protected StompFrame(StompCommand command) {
        this(command, null, null);
    }

    /**
     * Add frame header
     * @param key
     * @param value
     */
    public void addFrameHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Get frame body
     * @return frame body
     */
    public String getBody() {
        return body;
    }

    /**
     * Get frame stomp command type
     * @return stomp command type
     */
    public StompCommand getCommand() {
        return command;
    }

    /**
     * Mandatory headers frame validation
     * @throws StompException
     */
    protected void validate() throws StompException {
        String[] headers = getMandatoryHeaders();
        if (headers != null) {
            for (String header : headers) {
                if (!hasHeader(header))
                    throw new StompException("Frame error", String.format("%s is a mandatory header.\nPlease try again.", header));
            }
        }
    }
    
    /**
     * Get mandatory headers list
     * @return mandatory headers list
     */
    protected String[] getMandatoryHeaders() {
        return null;
    }

    /**
     * Extract specific header from message by name
     * 
     * @param header
     *            name of header
     * @return value of header - null if not found.
     */
    public String header(String header) {
        return headers.get(header);
    }

    /**
     * Format a message into a Stomp frame with no delimiter
     * 
     * @return a valid Stomp frame ready to be framed by tokenizer
     *         getBytesForMessage()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(command.toString());
        sb.append("\n");

        if (headers != null) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                sb.append(key).append(":").append(value).append("\n");
            }
        }

        if (!body.isEmpty()) {
            sb.append("\n");
            sb.append(body);
            sb.append("\n");
        }

        sb.append(DELIMITER);

        return sb.toString();
    }

    /**
     * @param str
     *            as returned from tokenizer contains no framing delimiter
     *            General assumption that the frame is well formed.
     * 
     * @return parsed frame
     */
    public static StompFrame parse(String str) {
        String[] lines = str.split("\n");
        
        if (str.trim().isEmpty())
            return null;
        
        int lineNum = 0;
        
        // ignore empty lines
        while(lines[lineNum].trim().isEmpty()) { lineNum++; };

        // Throws Error if unrecognized
        // Keep reading headers until empty line
        StompCommand command = StompCommand.valueOf(lines[lineNum]);

        if (command == null) {
            return new ErrorFrame("No matching STOMP command.");
        }

        StompFrame frame = null;
        if (command == StompCommand.CONNECT) {
            frame = new ConnectFrame();
        } else if (command == StompCommand.DISCONNECT) {
            frame = new DisconnectFrame();
        } else if (command == StompCommand.SEND) {
            frame = new SendFrame();
        } else if (command == StompCommand.SUBSCRIBE) {
            frame = new SubscribeFrame();
        } else if (command == StompCommand.UNSUBSCRIBE) {
            frame = new UnsubscribeFrame();
        } else {
            return new ErrorFrame("No matching STOMP command.");
        }
        
        if (lines.length > 1) {
            lineNum++;
            // parse headers
            String currentLine = "";
            while (lineNum < lines.length
                    && !(currentLine = lines[lineNum].trim()).isEmpty()) {
                int colonIndex = currentLine.indexOf(':');
                if (colonIndex > 0) {
                    frame.addFrameHeader(currentLine.substring(0, colonIndex),
                            currentLine.substring(colonIndex + 1));
                }
                lineNum++;
            }

            lineNum++;
            StringBuilder body = new StringBuilder();
            while (lineNum < lines.length) {
                body.append(lines[lineNum].trim()).append("\t");
                lineNum++;
            }
            frame.setBody(body.toString());
        }
        
        try {
            frame.validate();
        } catch (StompException e) {
            return e.generateErrorFrame();
        }

        return frame;
    }

    /**
     * Set frame's body
     * @param body
     */
    private void setBody(String body) {
        this.body = body;
    }
    
    /**
     * Determine if frame has a given header or not
     * @param header
     * @return true if frame's header contains the given header name, false otherwise
     */
    protected boolean hasHeader(String header) {
        return headers.containsKey(header);
    }
}
