//
//  ClientsCommand.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef ClientsCommand_h
#define ClientsCommand_h

#include <string>
#include "Command.h"
#include "../../include/stomp/TwitterClient.h"

namespace twitter {
    
    namespace commands {
        
        class ClientsCommand : public Command {
            
        protected:
            //Members
            bool online;

        public:
            ClientsCommand(stomp::TwitterClient *client, Args args);
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace

#endif
