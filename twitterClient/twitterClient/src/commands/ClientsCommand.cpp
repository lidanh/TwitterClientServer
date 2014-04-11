//
//  ClientsCommand.cpp
//  The clients command, asks the server to list it's clients, optional paramter [online] to list only online users.
//  The command works by sending a SEND frame with the body "clients" (clients online) to the server topic
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/ClientsCommand.h"
#include "../../include/stomp/frames/SendFrame.h"

namespace twitter {
    
    namespace commands {
        
        ClientsCommand::ClientsCommand(stomp::TwitterClient *client, Args args): Command("Clients", client, args), online(false)
        {
            
            if (args.size() > 1) {
                this->commandSuccess = false;
                this->errorReason = "Expecting at most 1 arguments :  [online] optional";
            } else if (args.size() == 1) {
                if (args.at(0) == "online") online = true;
            } else {
                online = false;
            }
        }
        
        void ClientsCommand::run() {
            std::string cmd = "clients";
            if (online) { cmd = cmd + " online"; }
            
            stomp::SendFrame frame(this->client->getTopicForUser("server"), cmd);
            this->commandSuccess = this->client->sendFrame(frame);
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace