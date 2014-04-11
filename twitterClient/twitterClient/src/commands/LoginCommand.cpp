//
//  LoginCommand.h
//  The login command, initiates the connection handler to the correct server and port
//  Once connected sends the CONNECTED frame to that server
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/LoginCommand.h"
#include "../../include/stomp/frames/ConnectFrame.h"

namespace twitter {
    
    namespace commands {

        LoginCommand::LoginCommand(stomp::TwitterClient *client, Args args): Command("Login", client, args)
        {
            if (args.size() != 4) {
                this->commandSuccess = false;
                this->errorReason = "Expecting 4 arguments";
                std::cerr << this->errorReason << " " << args.size() << " given" << std::endl;
            }
    
        }
        
        void LoginCommand::beforeRun() {}
        
        void LoginCommand::afterRun() {
            if (!this->client->isConnected())            {
                this->commandSuccess = false;
                this->errorReason = "Could not connect!";
            }
        }
        
        void LoginCommand::run() {
            bool connected = this->client->serverAt(this->args.at(0), this->args.at(1), this->args.at(2), this->args.at(3)); //Set server details: host and port
            if (!connected) {
                this->commandSuccess = false;
                this->errorReason = "Could not connect to server";
                return;
            }

            stomp::ConnectFrame connect(this->args.at(0), this->args.at(2), this->args.at(3));
            bool succ = this->client->sendFrame(connect);
        
            if (!succ) {
                this->commandSuccess = false;
                this->errorReason = "Could not send CONNECT frame successfully";
            }
         
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace