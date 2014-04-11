//
//  UnfollowCommand.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef UnfollowCommand_h
#define UnfollowCommand_h

#include <string>
#include "Command.h"
#include "../../include/stomp/TwitterClient.h"

namespace twitter {
    
    namespace commands {
        
        class UnfollowCommand : public Command {
            
        protected:
            //Members
            
        public:
            UnfollowCommand(stomp::TwitterClient *client, Args args);
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace


#endif
