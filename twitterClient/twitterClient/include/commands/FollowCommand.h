//
//  FollowCommand.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef FollowHeader_h
#define FollowHeader_h

#include <string>
#include "Command.h"
#include "../../include/stomp/TwitterClient.h"

namespace twitter {
    
    namespace commands {
        
        class FollowCommand : public Command {
            
        protected:
            //Members
                        
            
        public:
            FollowCommand(stomp::TwitterClient *client, Args args);
            void beforeRun();
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace

#endif
