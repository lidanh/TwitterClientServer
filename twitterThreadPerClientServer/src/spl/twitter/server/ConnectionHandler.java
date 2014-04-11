package spl.twitter.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spl.stomp.protocol.StompCommand;
import spl.stomp.protocol.StompFrame;
import spl.twitter.engine.AsyncConnection;
import spl.twitter.engine.AsyncProtocol;
import spl.twitter.engine.AsyncProtocolFactory;
import spl.twitter.engine.AsyncServerListener;

/**
 * @name Server Connection Handler
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Manage a single connection between the server and the client 
 */
class ConnectionHandler implements Runnable, AsyncConnection<StompFrame> {
    private static final Logger logger = LogManager.getLogger(ConnectionHandler.class.getName());
    private BufferedReader in;
    private PrintWriter out;
    private Socket clientSocket;
    private AsyncProtocol<StompFrame> protocol;
    private StompTokenizer tokenizer;
    private AsyncServerListener<StompFrame> server;
    /** Connection handler's message queue, 
     *  hold messages that is sent to the client after successful login,
     *  and before sending CONNECTED frame
     */
    private Queue<StompFrame> messagesQueue;

    /**
     * Create new connection handler for a given connection (client socket, protocol, server)
     * @param acceptedSocket
     * @param protocolFactory
     * @param tokenizer
     * @param server
     */
    public ConnectionHandler(Socket acceptedSocket,
            AsyncProtocolFactory<StompFrame> protocolFactory, StompTokenizer tokenizer, AsyncServerListener<StompFrame> server) {
        this.in = null;
        this.out = null;
        this.clientSocket = acceptedSocket;
        this.protocol = protocolFactory.create(this);
        this.tokenizer = tokenizer;
        this.server = server;
        this.messagesQueue = new LinkedBlockingQueue<StompFrame>();
        logger.info("Accepted connection from client {}!", this);
    }

    public void run() {
        Thread.currentThread().setName(this.toString());
        
        try {
            initialize();
        } catch (IOException e) {
            logger.error("Error in initializing I/O");
        }

        try {
            process();
        } catch (IOException e) {
            logger.error("Error in I/O");
        }

        // kill from server, it calls to close() method, 
        // and then delete the connection reference from the server
        close();
        server.remove(this);
        logger.info("Connection closed.");
    }

    /**
     * Process message
     * @throws IOException
     */
    public void process() throws IOException {
        while (true) {
            StompFrame response = protocol.processMessage(tokenizer.getFrame(in));
            
            if (response != null) {
                out.println(response);
                logger.debug("The following frame was sent to the client: \n{}", response);
                
                if (response.getCommand() == StompCommand.CONNECTED) {
                    // user is authorized. 
                    // send the messages that are waiting for him
                    flushMessages();
                }
            }
            
            if (protocol.shouldClose()) {
                break;
            }
        }
    }

    /**
     * Initializing connection handler- I/O channels
     * @throws IOException
     */
    public void initialize() throws IOException {
        // Initialize I/O
        in = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(
                clientSocket.getOutputStream(), "UTF-8"), true);
        
        logger.trace("I/O initialized");
    }

    /**
     * Close the connection:
     * update the protocol, close the socket and the I/O channels
     */
    public void close() {
        try {
            protocol.connectionTerminated();
            clientSocket.close();
            
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            logger.error("Exception in closing I/O");
        }
    }

    /**
     * Send the given stomp frame to the client
     */
    @Override
    public void send(StompFrame frame) {
        messagesQueue.add(frame);
        flushMessages();
    }

    /**
     * Flush the queue and send the awaiting frame to the client
     */
    private void flushMessages() {
        if (protocol.isAuthorized() && !clientSocket.isClosed()) {
            while (!messagesQueue.isEmpty()) {
                StompFrame frame = messagesQueue.poll();
                out.println(frame);
                logger.debug("The following frame was sent to the client: \n{}", frame);
            }
        }
    }

    /**
     * Determine if client's socket is closed
     */
    @Override
    public boolean isClosed() {
        return clientSocket.isClosed();
    }
    
    @Override
    public String toString() {
        return String.format("%s:%s", clientSocket.getInetAddress(), clientSocket.getPort());
    }
}
