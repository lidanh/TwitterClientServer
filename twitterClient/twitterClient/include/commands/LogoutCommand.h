//
//  LogoutCommand.
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef LogoutCommand_h
#define LogoutCommand_h

#include <string>
#include "Command.h"

namespace twitter {
    
    namespace commands {
        
        class LogoutCommand : public Command {
            
        public:
            LogoutCommand(stomp::TwitterClient *client, Args args);
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace

#endif
