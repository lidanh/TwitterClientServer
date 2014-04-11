//
//  TweetCommand.cpp
//  A tweet command, used to tweet out your own messages
//  by sending out a SEND frame to the server
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/TweetCommand.h"

#include <sstream>
#include "../../include/stomp/frames/SendFrame.h"

namespace twitter {
    
    namespace commands {
        
        TweetCommand::TweetCommand(stomp::TwitterClient *client, Args args): Command("Tweet", client, args), message()
        {
            
            if (args.size() < 1) {
                this->commandSuccess = false;
                this->errorReason = "Expecting at least one argument : message to tweet (2 including command)";
            } else {
                
                //Build a single string from all the broken words
                std::stringstream tweet;
                for (Args::const_iterator it = this->args.begin();
                     it != this->args.end();
                     ++it)
                {
                    tweet << (*it) << " ";
                }
                
                message = tweet.str(); 
            }
        }
        
        void TweetCommand::run() {

            stomp::SendFrame sendFrame(this->client->getTopicForUser(this->client->me()), message);
            this->commandSuccess = this->client->sendFrame(sendFrame);
            
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace