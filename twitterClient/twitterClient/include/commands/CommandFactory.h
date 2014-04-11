//
//  CommandFactory.h
//  A factory for building command objects from their string representation
//  Given a string input from the user, will parse and create the command object
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef CommandFactory_h
#define CommandFactory_h

#include <string>
#include "../../include/commands/Command.h"
#include "../../include/stomp/TwitterClient.h"

namespace twitter {
    
    namespace commands {
    
        class CommandFactory {
            
        public:
            //A factory method that accept a string representation of a command with arguments and a stomp client pointer
            // and returns an appropriate Command object that represents the string command
            static Command * createFactory(std::string const &command_string, stomp::TwitterClient *client);
            
        };
        
    } //COMMANDS namespace
    
}; //TWITTER namespace


#endif
