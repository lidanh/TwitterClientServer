//
//  LoginCommand.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef LoginCommand_h
#define LoginCommand_h

#include <string>
#include "Command.h"
#include "../../include/stomp/TwitterClient.h"


namespace twitter {
    
    namespace commands {
    
        class LoginCommand : public Command {
            
        protected:
            //Members
           
            //Methods
            void beforeRun();
            void afterRun();
            
        public:
            LoginCommand(stomp::TwitterClient *client, Args args);
            void run();
        };
        
    } //COMMANDS namespace
    
} //TWITTER namespace


#endif
