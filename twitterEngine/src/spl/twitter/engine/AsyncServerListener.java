package spl.twitter.engine;

import java.io.IOException;

/**
 * @name Async Server Listener
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Interface for asynchronous server
 */
public interface AsyncServerListener<T> {
    /**
     * Kill the server and close all the connections
     * @throws IOException
     */
    void kill() throws IOException;
    
    /**
     * Remove a given connection from the server
     * @param connection
     */
    void remove(AsyncConnection<T> connection);
}
