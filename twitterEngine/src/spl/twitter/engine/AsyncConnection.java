package spl.twitter.engine;

/**
 * @name Async Connection
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Interface for connection handlers' asynchronous capabilities
 */
public interface AsyncConnection<T> {
    /**
     * Send frame of type T
     * @param frame
     */
    void send(T frame);
    /**
     * Close connection
     */
    void close();
    /**
     * Check if connection handler is closed
     * @return
     */
    boolean isClosed();
}
