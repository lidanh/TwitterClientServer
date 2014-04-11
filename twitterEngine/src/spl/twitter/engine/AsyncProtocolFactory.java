package spl.twitter.engine;

/**
 * @name Async Protocol Factory
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Interface for creating new AsyncProtocol objects in runtime
 * @see spl.twitter.engine.AsyncProtocol
 */
public interface AsyncProtocolFactory<T> {
    /**
     * Create new AsyncProtocol
     * @param connection
     * @return AsyncProtocol Object
     * @see spl.twitter.engine.AsyncProtocol
     */
    AsyncProtocol<T> create(AsyncConnection<T> connection);
}
