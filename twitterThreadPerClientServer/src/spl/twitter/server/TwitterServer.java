package spl.twitter.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.StompFrame;
import spl.stomp.protocol.StompProtocolFactory;
import spl.twitter.engine.AsyncConnection;
import spl.twitter.engine.AsyncProtocolFactory;
import spl.twitter.engine.AsyncServerListener;
import spl.twitter.engine.TwitterEngine;

/**
 * @author Lidan Hifi
 * @author Ken Saggy
 *         <p/>
 *         Twitter Thread-Per-Client server
 * @name Twitter Server
 */
class TwitterServer implements Runnable, AsyncServerListener<StompFrame> {
    private static final Logger logger = LogManager
            .getLogger(TwitterServer.class.getName());
    private ServerSocket serverSocket;
    private int listenPort;
    private AsyncProtocolFactory<StompFrame> protocolFactory;
    private StompTokenizer tokenizer;
    private AtomicBoolean isRunning;
    private List<AsyncConnection<StompFrame>> activeConnections = new Vector<AsyncConnection<StompFrame>>();

    /**
     * Create new Twitter Server object
     *
     * @param port
     * @param protocol
     */
    public TwitterServer(int port, AsyncProtocolFactory<StompFrame> protocol) {
        this.serverSocket = null;
        this.listenPort = port;
        this.protocolFactory = protocol;
        this.tokenizer = new StompFrameTokenizer(Charset.forName("UTF-8"));
        this.isRunning = new AtomicBoolean(true);
    }

    /**
     * Run the server in a different thread
     */
    public void run() {
        boolean succeed = true;

        try {
            serverSocket = new ServerSocket(listenPort);
            logger.info("Listening to port {}...", listenPort);
        } catch (IOException e) {
            logger.fatal("Cannot listen to port {}", listenPort);
            succeed = false;
        }

        while (succeed) {
            try {
                // accept client's connections
                ConnectionHandler newConnection = new ConnectionHandler(
                        serverSocket.accept(), protocolFactory, tokenizer, this);
                activeConnections.add(newConnection);

                // create a new thread for each client (connection handler)
                new Thread(newConnection).start();
            } catch (IOException e) {
                if (isRunning.get()) {
                    logger.error("Failed to accept on port {}", listenPort);
                } else {
                    logger.info("Stop listening to new connections...");
                    break;
                }
            }
        }

        logger.info("Server closed.");
    }

    /**
     * Remove the given connection handler from the server's connections list
     */
    public synchronized void remove(AsyncConnection<StompFrame> connection) {
        activeConnections.remove(connection);
    }

    /**
     * Close connection and remove it from the server
     *
     * @param connection
     */
    private void closeConnection(AsyncConnection<StompFrame> connection) {
        if (!connection.isClosed())
            connection.close();
    }

    /**
     * Kill the server (close the open connections)
     */
    public synchronized void kill() throws IOException {
        this.isRunning.set(false);

        // close serversocket, so it cannot accept new connections
        serverSocket.close();

        // Close all active connections gracefully
        Iterator<AsyncConnection<StompFrame>> iterator = activeConnections.iterator();
        while (iterator.hasNext()) {
            AsyncConnection<StompFrame> conn = iterator.next();
            closeConnection(conn);
            iterator.remove();
            logger.info("connection {} removed from server", conn);
        }
    }

    public static void main(String[] args) throws IOException {
        // Get port. default port is 1140
        int port = 1140;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            // if port was not given in the args, use default port (1140)
            System.out.println("Port was not configured.");
            System.out.println("Using default port (" + port + ")");
        }

        // print server logo
        System.out.println("          .,,,,,,,.                         ");
        System.out.println("        .,:,,,,,,,,.                        ");
        System.out.println("        .:,,,,,,,,:,.                       ");
        System.out.println("        .:,,,,,,,::,.                       ");
        System.out.println("        .:,,:::,,::,.                       ");
        System.out.println("       ..::::::,:::,.                       ");
        System.out.println("       ..::::::::::,,.................      ");
        System.out.println("       ..:::::::::::,,,,,,,,,,,,,,,,,:,.    ");
        System.out.println("       ..::::::::::::,:,,::,:,:,,,:,::::,.  ");
        System.out.println("       .,::::::::::::::::::::::::::::::::.  ");
        System.out.println("       ..:~::::::::::::::::::::::::::::::.  ");
        System.out.println("       .,::::::::::::::::::::::::::::::::.  ");
        System.out.println("        .::~:::~::::::::::::::::::::::::.   ");
        System.out.println("       ..:~~::::~:~~:::::::::::::::::,,.    ");
        System.out.println("       .,~::~:~:~~::..................      ");
        System.out.println("       ..:~~~~::~~~:.                       ");
        System.out.println("       .,~~~~~~~~~~:.                       ");
        System.out.println("       ..~~~~~~~~~~:.    TWITTER SERVER     ");
        System.out.println("       ..~~~~~~~~~~~.    by Lidan Hifi and Ken Saggy");
        System.out.println("        .:~~~~~~~~~~:..                     ");
        System.out.println("        .,~~~~~~~~~~~~:,,,,,,,,,,,.,...     ");
        System.out.println("         .:~~~~~~~~~~~~~=~=~~~~~~~~~~~:,.   ");
        System.out.println("         .,:=~~~~~~~~~~~~~~~~~~~~~~~~~~=:.  ");
        System.out.println("           .:~=~=~~=~~~=~~~=~~=~~~~~==~=~,  ");
        System.out.println("            .:~======~==~==~=~~========~~,  ");
        System.out.println("             .,:~=======================~.  ");
        System.out.println("               ..:~~==================~~..  ");
        System.out.println("                  ...,,::::::::::::::,..    ");

        AsyncProtocolFactory<StompFrame> factory = new StompProtocolFactory();
        TwitterServer server = new TwitterServer(port, factory);
        TwitterEngine engine = TwitterEngine.getInstance(); // initialize from
        // the main thread
        engine.setConnectionsListener(server);
        Thread serverThread = new Thread(server);
        serverThread.start();
        serverThread.setName("Server");
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            logger.info("Server stopped.");
        }
    }
}