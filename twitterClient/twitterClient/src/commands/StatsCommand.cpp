//
//  StatsCommand.cpp
//  The stats command asks the server to list it's stats by sending a SEND frame with the body "stats" to the server topic.
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/StatsCommand.h"
#include "../../include/stomp/frames/SendFrame.h"

namespace twitter {
    
    namespace commands {
        
        StatsCommand::StatsCommand(stomp::TwitterClient *client, Args args): Command("Stats", client, args)
        {
            if (args.size() != 0) {
                this->commandSuccess = false;
                this->errorReason = "Stats command expects no arguments!";
            }
        }
        
        void StatsCommand::run() {

            stomp::SendFrame frame(this->client->getTopicForUser("server"), "stats");
            this->commandSuccess = this->client->sendFrame(frame);

        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace