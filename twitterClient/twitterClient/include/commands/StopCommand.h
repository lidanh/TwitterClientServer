//
//  StopCommand.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef StopCommand_h
#define StopCommand_h

#include <string>
#include "Command.h"

namespace twitter {
    
    namespace commands {
        
        class StopCommand : public Command {
        
        public:
            StopCommand(stomp::TwitterClient *client, Args args);
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace

#endif
