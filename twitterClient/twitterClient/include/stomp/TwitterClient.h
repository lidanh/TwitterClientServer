//
//  TwitterClient.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_STOMPCLIENT_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_STOMPCLIENT_H_


#include <string>
#include <boost/thread.hpp>
#include "../../include/stomp/connectionHandler.h"
#include "../../include/stomp/frames/StompFrame.h"
#include "../../include/stomp/HtmlWriter.h"

namespace stomp {
    class MessageFrame;
    class TwitterClient {
        
        typedef std::map<std::string, std::string> Subscriptions;
        
    protected:
        //Members
        std::string host; //These 4 are used to keep the connection info
        unsigned short port;
        std::string user;
        std::string pass;
        
        HtmlWriter *writer; //A pointer to our HtmlWriter object
        ConnectionHandler *connection; //A connecitonHandler object
        bool connected; //A simple indication for some cases.
        int unique_identifier; //Used to generate unique id's for following users and disconnnect receipts
        
        boost::thread *listener_thread; //Our listener thread
        boost::mutex *mutex; //A mutex used to lock the subscription map in case a incoming frame and a command run at the same time and try to modify the subscription map
        
        Subscriptions subscriptions; //The map of user=>id subscriptions
        
        //Methods
        void close_connection();
        

    public:
        TwitterClient();
        ~TwitterClient();
        TwitterClient(const TwitterClient&);
        const TwitterClient& operator=(const TwitterClient& client);
        
        //Main functions
        void startInterface(); //Start listening on the keyboard
        void listener(); //listener thread

        
        //Connection related
        bool serverAt(const std::string &host, unsigned short port,const std::string &login, const std::string &passwd);
        bool serverAt(const std::string &host, const std::string &port,const std::string &login, const std::string &passwd);
        void onConnect();
        bool isConnected();
        void onDisconnect();
        
        
        //Frame related
        bool sendFrame(const StompFrame &frame);

        //Subscription related methods
        std::string identifier();
        bool registerSubscription(const std::string &subscription_id, const std::string &follow);
        std::string getSubscriptionIdentifier(const std::string &follow);
        
        bool removeSubscription(const std::string &unfollow);
        std::string getUserBySubscription(const std::string &identifier);
        
        //State
        std::string me() { return user; }
        
        //Helpers
        std::string getTopicForUser(const std::string &user);
        std::string getUserFromTopic(const std::string &topic);
        void write(MessageFrame *frame);
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_STOMPCLIENT_H_
