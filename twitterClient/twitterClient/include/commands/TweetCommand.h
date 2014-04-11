//
//  TweetCommand.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TweetCommand_h
#define TweetCommand_h

#include <string>
#include "Command.h"
#include "../../include/stomp/TwitterClient.h"

namespace twitter {
    
    namespace commands {
        
        class TweetCommand : public Command {
            
        protected:
            //Members
            std::string message;
            
        public:
            TweetCommand(stomp::TwitterClient *client, Args args);
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace

#endif
