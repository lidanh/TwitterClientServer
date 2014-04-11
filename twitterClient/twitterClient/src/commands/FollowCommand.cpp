//
//  FollowCommand.cpp
//  The Follow command, for following users, sends the SUBSCRIBE command with a pre-generated unique identifier
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/FollowCommand.h"
#include "../../include/stomp/frames/SubscribeFrame.h"

namespace twitter {
    
    namespace commands {
        
        FollowCommand::FollowCommand(stomp::TwitterClient *client, Args args): Command("Follow", client, args)
        {
            if (args.size() != 1) {
                this->commandSuccess = false;
                this->errorReason = "Expecting 1 arguments : username to follow";
            }
        }
        
        void FollowCommand::beforeRun()
        {
            // First check to see if we're already following this use
            std::string existing_subscription_identifier = this->client->getSubscriptionIdentifier(this->args.at(0));
            if (existing_subscription_identifier != "" )
            {
                this->commandSuccess = false;
                this->errorReason = "Already following user " + this->args.at(0);
            }
        }
    
        void FollowCommand::run() {
            
            // Get a *possible* future identifier for this subscription
            // This *may* not stick if the server returns an error
            std::string iden = this->client->identifier();
            
            stomp::SubscribeFrame subscribeFrame(this->client->getTopicForUser(this->args.at(0)), iden);
            this->commandSuccess = this->client->sendFrame(subscribeFrame);
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace