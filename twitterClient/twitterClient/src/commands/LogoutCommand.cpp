//
//  LogoutCommand.cpp
//  The logout command tells the server we which to logout by sending a DISCONNECT frame to the server with a unique identifier (which will be returned in the RECEIPT.
//  This DOES NOT close the connection itself with the server just yet - that will only occur once we receive the RECEIPT.
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/LogoutCommand.h"
#include "../../include/stomp/frames/DisconnectFrame.h"
namespace twitter {
    
    namespace commands {
        
        LogoutCommand::LogoutCommand(stomp::TwitterClient *client, Args args): Command("Logout", client, args)
        {
            if (args.size() != 0) {
                this->commandSuccess = false;
                this->errorReason = "logout command expects no arguments!";
            }
        }
    
        
        void LogoutCommand::run()
        {
            
            
            stomp::DisconnectFrame disconnect(this->client->identifier());
            bool succ = this->client->sendFrame(disconnect);
            
            if (!succ) {
                this->commandSuccess = false;
                this->errorReason = "Unknown error";
            }
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace