package spl.twitter.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.StompFrame;
import spl.stomp.protocol.StompProtocolFactory;
import spl.twitter.engine.AsyncConnection;
import spl.twitter.engine.AsyncProtocolFactory;
import spl.twitter.engine.AsyncServerListener;
import spl.twitter.engine.TwitterEngine;
import spl.twitter.tokenizer.StompFrameTokenizer;
import spl.twitter.tokenizer.StompTokenizer;

/**
 * @name Twitter Reactor
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * An implementation of the Reactor pattern.
 */
public class TwitterReactor<T> implements Runnable, AsyncServerListener<T> {

    private static final Logger logger = LogManager.getLogger(TwitterReactor.class.getName());

    private final int _port;

    private final int _poolSize;

    private final AsyncProtocolFactory<T> _protocolFactory;

    private final TokenizerFactory<T> _tokenizerFactory;

    private volatile boolean _shouldRun = true;

    private ReactorData<T> _data;

    /**
     * Creates a new Reactor
     * 
     * @param poolSize
     *            the number of WorkerThreads to include in the ThreadPool
     * @param port
     *            the port to bind the Reactor to
     * @param protocol
     *            the protocol factory to work with
     * @param tokenizer
     *            the tokenizer factory to work with
     * @throws IOException
     *             if some I/O problems arise during connection
     */
    public TwitterReactor(int port, int poolSize, AsyncProtocolFactory<T> protocol,
            TokenizerFactory<T> tokenizer) {
        _port = port;
        _poolSize = poolSize;
        _protocolFactory = protocol;
        _tokenizerFactory = tokenizer;
    }

    /**
     * Create a non-blocking server socket channel and bind to to the Reactor
     * port
     */
    private ServerSocketChannel createServerSocket(int port) throws IOException {
        try {
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.socket().bind(new InetSocketAddress(port));
            return ssChannel;
        } catch (IOException e) {
            logger.fatal("Port {} is busy", port);
            throw e;
        }
    }

    /**
     * Main operation of the Reactor:
     * <UL>
     * <LI>Uses the <CODE>Selector.select()</CODE> method to find new requests
     * from clients
     * <LI>For each request in the selection set:
     * <UL>
     * If it is <B>acceptable</B>, use the ConnectionAcceptor to accept it,
     * create a new ConnectionHandler for it register it to the Selector
     * <LI>If it is <B>readable</B>, use the ConnectionHandler to read it,
     * extract messages and insert them to the ThreadPool
     * </UL>
     */
    public void run() {
        // Create & start the ThreadPool
        ExecutorService executor = Executors.newFixedThreadPool(_poolSize);
        Selector selector = null;
        ServerSocketChannel ssChannel = null;

        try {
            selector = Selector.open();
            ssChannel = createServerSocket(_port);
        } catch (IOException e) {
            logger.fatal("cannot create the selector -- server socket is busy?");
            TwitterEngine.getInstance().close();
            return;
        }

        _data = new ReactorData<T>(executor, selector, _protocolFactory,
                _tokenizerFactory);
        ConnectionAcceptor<T> connectionAcceptor = new ConnectionAcceptor<T>(
                ssChannel, _data, this);

        // Bind the server socket channel to the selector, with the new
        // acceptor as attachment

        try {
            ssChannel.register(selector, SelectionKey.OP_ACCEPT,
                    connectionAcceptor);
        } catch (ClosedChannelException e) {
            logger.fatal("server channel seems to be closed!");
            return;
        }

        while (_shouldRun && selector.isOpen()) {
            // Wait for an event
            try {
                selector.select();
            } catch (IOException e) {
                logger.fatal("trouble with selector: {}", e.getMessage());
                continue;
            }

            // Get list of selection keys with pending events
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            // Process each key
            while (it.hasNext()) {
                // Get the selection key
                SelectionKey selKey = (SelectionKey) it.next();

                // Remove it from the list to indicate that it is being
                // processed. it.remove removes the last item returned by next.
                it.remove();

                // Check if it's a connection request
                if (selKey.isValid() && selKey.isAcceptable()) {
                    logger.info("Accepting a connection");
                    ConnectionAcceptor<T> acceptor = (ConnectionAcceptor<T>) selKey.attachment();
                    try {
                        acceptor.accept();
                    } catch (IOException e) {
                        logger.error("problem accepting a new connection: {}", e.getMessage());
                    }
                    continue;
                }
                // Check if a message has been sent
                if (selKey.isValid() && selKey.isReadable()) {
                    ConnectionHandler<T> handler = (ConnectionHandler<T>) selKey.attachment();
                    handler.read();
                }
                // Check if there are messages to send
                if (selKey.isValid() && selKey.isWritable()) {
                    ConnectionHandler<T> handler = (ConnectionHandler<T>) selKey.attachment();
                    handler.write();
                }
            }
        }
        stopReactor();
    }

    /**
     * Returns the listening port of the Reactor
     * 
     * @return the listening port of the Reactor
     */
    public int getPort() {
        return _port;
    }

    /**
     * Stops the Reactor activity, including the Reactor thread and the Worker
     * Threads in the Thread Pool.
     */
    public synchronized void stopReactor() {
        if (!_shouldRun)
            return;
        _shouldRun = false;
        _data.getSelector().wakeup(); // Force select() to return
        _data.getExecutor().shutdown();
        try {
            _data.getExecutor().awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Someone didn't have patience to wait for the executor pool to
            // close
            e.printStackTrace();
        }
    }

    /**
     * Main program, used for demonstration purposes. Create and run a
     * Reactor-based server for the Echo protocol. Listening port number and
     * number of threads in the thread pool are read from the command line.
     */
    public static void main(String args[]) {
        // default port
        int port = 1140;
        // default pool size
        int poolSize = 10;

        try {
            port = Integer.parseInt(args[0]);
            poolSize = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Port and pool size was not configured.");
            System.err.println("Usage: java TwitterReactor <port> <pool_size>");
            System.out.println("Use default port (" + port + ") and default pool size (" + poolSize + ")");
        }

        // Reactor server logo
        System.out.println("                                   /                                     ");
        System.out.println("                                  /                                      ");
        System.out.println("                                 /                                       ");
        System.out.println("                                /                                        ");
        System.out.println("                               /                                         ");
        System.out.println("                              /;                                         ");
        System.out.println("                             /;                                          ");
        System.out.println("                            //        TWITTER REACTOR SERVER             ");
        System.out.println("                           //         by Lidan Hifi & Ken Saggy          ");
        System.out.println("                          ;/                                             ");
        System.out.println("                        ,//      /                                       ");
        System.out.println("                    _,-' ;_,,   /                                        ");
        System.out.println("                 _,'-_  ;|,'   /                                         ");
        System.out.println("             _,-'_,..--. |                                               ");
        System.out.println("     ___   .'-'_)'  ) _)\\|      ___                                      ");
        System.out.println("   ,'\"\"\"`'' _  )   ) _)  ''--'''_,-'                                     ");
        System.out.println("-={-o-  /|    )  _)  ) ; '_,--''                                         ");
        System.out.println("   \\ -' ,`.  ) .)  _)_,''|                                               ");
        System.out.println("    `.\"(   `------''     /                                               ");
        System.out.println("      `.\\             _,'                                                ");
        System.out.println("        `-.____....-\\\\                                                   ");
        System.out.println("                  || \\\\                                                  ");
        System.out.println("                  // ||                                                  ");
        System.out.println("                 //  ||                                                  ");
        System.out.println("                //   ||                                                  ");
        System.out.println("            _-.//_, _||_,                                                ");
        System.out.println("              ,'   ,-'/                                                  ");
        
        try{
            // initialize reactor server
            TwitterReactor<StompFrame> reactor = startServer(port, poolSize);
            // initizlize twitter engine
            TwitterEngine engine = TwitterEngine.getInstance();
            engine.setConnectionsListener(reactor);

            Thread thread = new Thread(reactor);
            thread.start();
            thread.setName("Reactor Server");
            logger.info("Reactor is ready on port {} with {} threads available", reactor.getPort(), poolSize);
            thread.join();
        } catch (Exception e) {
            logger.catching(e);
            System.exit(1);
        }
    }

    public static TwitterReactor<StompFrame> startServer(int port, int poolSize) {
        AsyncProtocolFactory<StompFrame> protocolMaker = new StompProtocolFactory();

        final Charset charset = Charset.forName("UTF-8");
        TokenizerFactory<StompFrame> tokenizerMaker = new TokenizerFactory<StompFrame>() {
            public StompTokenizer<StompFrame> create() {
                return new StompFrameTokenizer(charset);
            }
        };

        TwitterReactor<StompFrame> reactor = new TwitterReactor<StompFrame>(port,
                poolSize, protocolMaker, tokenizerMaker);
        return reactor;
    }

    @Override
    public void kill() throws IOException {
        stopReactor();
    }

    @Override
    public void remove(AsyncConnection<T> connection) {
        
    }
}
