package spl.twitter.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.StompFrame;
import spl.twitter.engine.AsyncServerListener;

/**
 * @name Conenction Acceptor
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Handles new client connections. An Acceptor is bound on a ServerSocketChannel
 * objects, which can produce new SocketChannels for new clients using its
 * <CODE>accept</CODE> method.
 */
public class ConnectionAcceptor<T> {
    protected ServerSocketChannel _ssChannel;
    private static final Logger logger = LogManager.getLogger(ConnectionAcceptor.class.getName());

    protected ReactorData<T> _data;
    private AsyncServerListener<T> _reactor;

    /**
     * Creates a new ConnectionAcceptor
     * 
     * @param ssChannel
     *            the ServerSocketChannel which can accept new connections
     * @param data
     *            a reference to ReactorData object
     */
    public ConnectionAcceptor(ServerSocketChannel ssChannel, ReactorData<T> data, AsyncServerListener<T> reactor) {
        _ssChannel = ssChannel;
        _data = data;
        _reactor = reactor;
    }

    /**
     * Accepts a connection:
     * <UL>
     * <LI>Creates a SocketChannel for it
     * <LI>Creates a ConnectionHandler for it
     * <LI>Registers the SocketChannel and the ConnectionHandler to the Selector
     * </UL>
     * 
     * @throws IOException
     *             in case of an IOException during the acceptance of a new
     *             connection
     */
    public void accept() throws IOException {
        // Get a new channel for the connection request
        SocketChannel sChannel = _ssChannel.accept();

        // If serverSocketChannel is non-blocking, sChannel may be null
        if (sChannel != null) {
            SocketAddress address = sChannel.socket().getRemoteSocketAddress();

            logger.info("Accepting connection from {}", address);
            sChannel.configureBlocking(false);
            SelectionKey key = sChannel.register(_data.getSelector(), 0);

            ConnectionHandler<T> handler = ConnectionHandler.create(sChannel,
                    _data, key, _reactor);
            handler.switchToReadOnlyMode(); // set the handler to read only mode
        }
    }
}
