//
//  TwitterClient.cpp
//  Main entry point and the brains of the whole client
//  This object is in charge of listening to keyboard commands, starting a listener thread for incomming traffic and handling client logic (subscriptions, html, interface)
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/stomp/TwitterClient.h"

#include <boost/lexical_cast.hpp>
#include <boost/thread.hpp>

#include "../../include/stomp/frames/ErrorFrame.h"
#include "../../include/stomp/frames/ConnectFrame.h"
#include "../../include/stomp/frames/DisconnectFrame.h"
#include "../../include/stomp/frames/StompFrameFactory.h"

#include "../../include/commands/CommandFactory.h"

#include <iostream>
#include <sstream>

#include "../../include/stomp/frames/FrameVisitor.h"

namespace stomp {
    
    /**
     Constructor
    */
    TwitterClient::TwitterClient() : host(), port(), user(),pass(), writer(0), connection(0), connected(false), unique_identifier(0), listener_thread(0), mutex(0), subscriptions()
    {
        this->connected = false;
        this->mutex = new boost::mutex();
    };
    
    /**
     Destructor
     **/
    TwitterClient::~TwitterClient()
    {
        if (this->writer != NULL) {
            this->writer->close();
            delete this->writer;
            this->writer = 0;
        }
        if (this->connection != NULL)
        {
            delete this->connection; //This will also close the connection in the connectionHandler destructor
        }
        if (this->listener_thread != NULL)
        {
            delete this->listener_thread;
        }
        if (this->mutex != NULL)
        {
            delete this->mutex;
        }
    }

    /** Copy constructor and = operator, both don't allow an actualy copy to be made, only a single reference which is why the copy over the exact same pointers
     */
    TwitterClient::TwitterClient(const TwitterClient& other) : 
    host(other.host), port(other.port), user(other.user),pass(other.pass), writer(other.writer), connection(other.connection), 
    connected(other.connected), unique_identifier(other.unique_identifier), listener_thread(other.listener_thread), mutex(other.mutex), subscriptions(other.subscriptions) {}
        
    const TwitterClient& TwitterClient::operator=(const TwitterClient& client)
    {
        return *this;
    }
    
    /**
     Main interface function, loops infinitly and waits for user to input strings in std::in, each command is then sent to the CommandFactory to be made into a command object (assuming the command is valid), the command is then run, and the result is displayed back to the user
     */
    void TwitterClient::startInterface()
    {
        
        while (1) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cout << ">> ";
            std::flush(std::cout);
            std::cin.getline(buf, bufsize);
            std::string str_cmd(buf);
            
            if (str_cmd == "exit_client") {
                this->onDisconnect();
                break;
            }
            
            twitter::commands::Command * cmd = twitter::commands::CommandFactory::createFactory(str_cmd, this);
            if (cmd == 0) {
                std::cerr << "Error: Unknown command name : " << str_cmd << std::endl;
            } else {
                bool success = (*cmd).execute();
                if (!success) {
                    std::cerr << "Error: " << (*cmd).reason() << std::endl;
                }
                
                delete cmd; //After the command ran, delete it from memory
            } //if-else

        } //while
        
        std::cout << "Thank you, Goodbye!" << std::endl;
    }
    
    /**
     This method runs in a different thread, starts after the connection has been made (used ran the login command).
     For each frame we receieve, we send it to the FrameFactory which parses the string and returns a frame object; the frame is then handled using the VISITOR pattern that's built in to each frame type
    */
    void TwitterClient::listener()
    {

        FrameVisitor *visitor = new FrameVisitor(this);
        while (isConnected() && !boost::this_thread::interruption_requested()) {
            
            try {
                std::string answer;
                bool succ = connection->getFrameAscii(answer, '\0');

                if (succ) {
                    stomp::StompFrame *frame = 0;
                    if (answer.at(0) != 0) { //Handle annoying bug in connectionHandler
                        frame = stomp::StompFrameFactory::parse(answer);
                    }
                    if (frame != NULL) {
                        frame->accept(visitor);
                        delete frame; //After the frame is handled, delete it from memory
                        frame = 0;
                    } else {
                        std::cout << answer << std::endl;
                    }
                } else {
                    std::cout << "Disconnected. Exiting...\n" << std::endl;
                    this->onDisconnect();
                    delete visitor;
                    visitor = 0;
                    std::exit(EXIT_FAILURE); //This means someone shutdown the server unexpectedly.
                    break;
                }
            } catch (std::exception& e) {
            }
        } // while
        
        //Time to exit thread
        delete visitor;
    }


    
    bool TwitterClient::serverAt(const std::string &host, unsigned short port,const std::string &login, const std::string &passwd)
    {
        this->host = host;
        this->port = port;
        this->user = login;
        this->pass = passwd;
        
        this->connection = new ConnectionHandler(this->host, this->port);
        this->connected = (*connection).connect(); //This only connects to the host/port, does not send CONNECT frame to STOMP server.
        
        //Starting listener thread
        listener_thread = new boost::thread(&TwitterClient::listener, this);
        
        
        return this->connected; //Return whether we were sucessful in setting up a socket connection to the server.
    }
    
    /**
     Just another polymorphism on serverAt to be used if nessecary
     */
    bool TwitterClient::serverAt(const std::string &host, const std::string &port,const std::string &login, const std::string &passwd)
    {
        unsigned short numeric_port = boost::lexical_cast<unsigned short>(port);
        return serverAt(host, numeric_port, login, passwd);
    }
    
    /**
     On connect trigger: handles logic that needs to happen when a connection is made
    */
    void TwitterClient::onConnect()
    {
        if (this->connected)
        {
            this->subscriptions.empty(); //reset subscription map on every connect just to make sure
            this->writer = new HtmlWriter(this->user); //Start new HtmlWriter for this user
        }
    }
    
    /**
     In case someone outside the client (e.g. commands) want to verify the connection state
    */
    bool TwitterClient::isConnected()
    {
        return connected;
    }
    
    /**
     On disconnect trigger: handles logic that needs to happen on disconnecting: closing, clean up etc.
    */
    void TwitterClient::onDisconnect()
    {
        this->subscriptions.empty();
        if (this->writer != NULL) {
            this->writer->close();
            delete this->writer;
            this->writer = 0;
        }
        this->close_connection();
    }
    
    /**
     Protected method for actually closing the connection itself
    */
    void TwitterClient::close_connection()
    {
        if (connection != NULL) {
            listener_thread->interrupt();
            delete this->listener_thread;
            listener_thread = 0;
            
            (*connection).close();
            connected = false;
            delete this->connection; //clear memory of connectionHandler
            this->connection = 0;
        }
        //lock release automatically
    }
    
    //Accept a StompFrame objet reference, send that frame over the wire
    bool TwitterClient::sendFrame(const StompFrame& frame)
    {
        if (!this->connected) //If trying to send a frame while disconnected return an ERROR frame
        {
            return false;
        }
        
        std::string str_frame = frame.toString();
        
        return (*connection).sendLine(str_frame);
    }

    
    /**
     Register a new subscription. For each new subscription a client makes this function will generate and keep track of the unique identieres that identifies each subscription
     */
    bool TwitterClient::registerSubscription(const std::string &subscription_id, const std::string &follow)
    {
        
        
        Subscriptions::iterator it = subscriptions.find(follow);

        std::string identif;
        if (it != subscriptions.end()) {
            // if this subscription exist, return the existing identifier with have in the table
            return false; //it->second;
        }
        
        boost::mutex::scoped_lock lock(*this->mutex);
        std::pair<std::map<char,int>::iterator,bool> ret;
        this->subscriptions.insert(std::pair<std::string, std::string>(follow, subscription_id));
        
        this->unique_identifier++; //and increment for the next person
    
        
        return true;
    }
    
    std::string TwitterClient::getSubscriptionIdentifier(const std::string &follow)
    {
        Subscriptions::iterator it = subscriptions.find(follow);
        
        if (it == subscriptions.end()) {
            return "";
        }
        
        return it->second;
    }
    
    bool TwitterClient::removeSubscription(const std::string &unfollow)
    {
        if (unfollow == this->user)
        {
            return false; //This action is not allowed
        }
        
        Subscriptions::iterator it = subscriptions.find(unfollow);
        if (it != subscriptions.end())
        {
            //Acuiqre lock on subscriptions before earsing from it
            boost::mutex::scoped_lock lock(*this->mutex);
            subscriptions.erase(it);
            return true;
        }
        
        return false; //Unknown user - not in subscription map
    }
    
    std::string TwitterClient::getUserBySubscription(const std::string &identifier)
    {
        Subscriptions::const_iterator it;
        std::string user = "";
        
        for (it = subscriptions.begin(); it != subscriptions.end(); ++it)
        {
            if (it->second == identifier)
            {
                user = it->first;
                break;
            }
        }
        
        return user;
    }
    
    std::string TwitterClient::getTopicForUser(const std::string &user)
    {
        return "/topic/" + user;
    }
    
    std::string TwitterClient::getUserFromTopic(const std::string &topic)
    {
        //truncate /topic/ from the destination header /topic/username
        return topic.substr(topic.find("/", 1)+1, topic.length());
    }
    
    void TwitterClient::write(MessageFrame *frame)
    {
        std::string user(this->getUserFromTopic(frame->get("destination")));
        this->writer->addTweet(user, frame->getBody(), frame->get("timestamp"));
    }
    
    
    /** Protected Members */
    std::string TwitterClient::identifier()
    {
        std::stringstream s;
        s << this->unique_identifier + 1;
        return s.str();
    }
    
}