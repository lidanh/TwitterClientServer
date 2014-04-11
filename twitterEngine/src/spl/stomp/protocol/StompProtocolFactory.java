package spl.stomp.protocol;

import spl.twitter.engine.AsyncConnection;
import spl.twitter.engine.AsyncProtocol;
import spl.twitter.engine.AsyncProtocolFactory;

/**
 * @name Stomp Protocol Factory
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Factory of StompProtocol Objects
 */
public class StompProtocolFactory implements AsyncProtocolFactory<StompFrame> {
    @Override
    public AsyncProtocol<StompFrame> create(AsyncConnection<StompFrame> connection) {
        return new StompProtocol(connection);
    }
}
