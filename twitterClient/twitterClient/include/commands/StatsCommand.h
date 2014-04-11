//
//  StatsCommand.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef StatsCommand_h
#define StatsCommand_h

#include <string>
#include "Command.h"

namespace twitter {
    
    namespace commands {
        
        class StatsCommand : public Command {

        public:
            StatsCommand(stomp::TwitterClient *client, Args args);
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace

#endif
