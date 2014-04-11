//
//  StopCommand.cpp
//  Ask the server to stop its activity and disconnect everyone by sending a SEND frame to the server topic
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/StopCommand.h"

#include "../../include/stomp/frames/SendFrame.h"

namespace twitter {
    
    namespace commands {
        
        StopCommand::StopCommand(stomp::TwitterClient *client, Args args): Command("Logout", client, args)
        {
            if (args.size() != 0) {
                this->commandSuccess = false;
                this->errorReason = "stop command expects no arguments!";
            }
        }
        
        void StopCommand::run() {
            stomp::SendFrame frame(this->client->getTopicForUser("server"), "stop");
            this->commandSuccess = this->client->sendFrame(frame);
            
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace