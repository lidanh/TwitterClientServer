//
//  FrameVisitor.cpp
//  The VISITOR part of the visitor pattern that's built onto the Stomp Frame objects.
//  Each frame type that needs to act (either independently or upon the client) when it arrives will override the visit() method with it's frame type.
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/FrameVisitor.h"
#include <boost/algorithm/string.hpp>
#include <boost/tokenizer.hpp>
#include <string>
#include <vector>
#include <iostream>

namespace stomp {

    FrameVisitor::FrameVisitor(const stomp::FrameVisitor& other) : client(other.client) {}
        
    const FrameVisitor& FrameVisitor::operator=(const stomp::FrameVisitor& visitor)
    {
        return *this;
    }
    
    void FrameVisitor::visit(ConnectedFrame *frame)
    {
        std::cout << "You are now CONNECTED" << std::endl;
        this->client->onConnect();
        this->client->registerSubscription("0", this->client->me()); // Follow ourselfs uppon successful connection
        std::cout << ">> ";
        std::flush(std::cout);
    }
    
    void FrameVisitor::visit(ErrorFrame *frame)
    {
        
        std::cout << "ERROR: " << frame->get("message") << std::endl;
        std::cout << "Details: " << frame->getBody() << std::endl;
        std::cout << ">> ";
        std::flush(std::cout);
    }
    
    void FrameVisitor::visit(MessageFrame *frame)
    {
        
        if (frame->get("subscription") != "0" && frame->get("destination") == this->client->getTopicForUser(this->client->me())) {
            //This is a message from the server saying we're successfully following whoever, or unfollow whoever, other wise, if this was a tweet from myself the subscription would 0
            
            //If this is a "successfully following" message, this subscription would not exist in our map
            //If it does exist in our map, this must be a confirmation of unfollowing.
            std::string user_of_subscription = this->client->getUserBySubscription(frame->get("subscription"));
            
            if (user_of_subscription == "") { //An identifier for this user doesn't exist, meaning this is a confirmation for a new following
                std::vector<std::string> bodyParts;
                std::string body = frame->getBody();
                boost::split(bodyParts, body, boost::is_any_of(" \n"), boost::token_compress_on);
                std::string username = bodyParts[1]; //This is ugly as fuck because the TA doesn't understand the conflicts in the specs he wrote.
                /**
                 Scenario: client sends subscribe userA, id:1
                            Since this subscription might fail, it is still not "saved" in the client's state
                            Server returns a Message frame, but the topic is me (because this message is addressed to me from the server)   
                            with id:1 [which i sent in the firstplace] - but all we know is "1" , we do not know which username this coroloates to - this is the reason for parsing the username from the frame body - which is disgusting
                 */
                
                this->client->registerSubscription(frame->get("subscription"), username);
            } else { //An identifier already exists, meaning we were already following this person, so this is an unfollow confirmation
                this->client->removeSubscription(user_of_subscription);
            };

            std::cout << "Now " << frame->getBody() << std::endl; //Should print Now following {username}
            
        } else { //A tweet perhaps?
            std::cout << "Received tweet from " << this->client->getUserFromTopic(frame->get("destination")) << ": ";
            std::cout << frame->getBody() << std::endl;
            this->client->write(frame); //Make sure the frame is written to the html
            
        }

        std::cout << ">> ";
        std::flush(std::cout);
    }
    
    void FrameVisitor::visit(ReceiptFrame *frame)
    {
        std::cout << "Got ReceiptFrame(id:" << frame->get("receipt-id") << ") [expected: " << this->client->identifier() << "], closing  connection" << std::endl;
        std::cout << ">> ";
        std::flush(std::cout);
        
        this->client->onDisconnect();

    }
}