//
//  UnfollowCommand.cpp
//  The Unfollow command, used to unfollow a user, sends a UNSUBSCRIBE frame with the identifier we previously generated for this user.

//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/UnfollowCommand.h"
#include "../../include/stomp/frames/UnsubscribeFrame.h"

namespace twitter {
    
    namespace commands {
        
        UnfollowCommand::UnfollowCommand(stomp::TwitterClient *client, Args args): Command("Unfollow", client, args)
        {
            if (args.size() != 1) {
                this->commandSuccess = false;
                this->errorReason = "Expecting 1 arguments : username to unfollow";
            }
        }
        
        void UnfollowCommand::run() {
            
            std::string identifier = this->client->getSubscriptionIdentifier(this->args.at(0));
            
            if (identifier == "") {
                this->commandSuccess = false;
                this->errorReason = "You were not following this person to begin with";
                return;
            }
            
            stomp::UnsubscribeFrame unsubscribeFrame(identifier);
            this->commandSuccess = this->client->sendFrame(unsubscribeFrame);
            
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace