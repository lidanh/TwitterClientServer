//
//  Command.h
//  Header file for Command type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef Command_h
#define Command_h

#include <string>
#include <vector>
#include "../stomp/TwitterClient.h"
#include "../stomp/frames/StompFrame.h"

namespace twitter {
    
    namespace commands {
        
        typedef std::vector<std::string> Args;
        
        class Command {
            
        protected:
            std::string command;
            stomp::TwitterClient *client;
            Args args;
            
            bool commandSuccess;
            std::string errorReason;
            
            virtual void beforeRun();
            virtual void afterRun() {};
            
            //Must be implemented in each subclassing command
            virtual void run() = 0;

            
        public:
            Command(const std::string command, stomp::TwitterClient *client);
            Command(const std::string command, stomp::TwitterClient *client, Args args);
            virtual ~Command();
            Command(const Command &other);
            const Command& operator=(const Command &command);
            
            void setCommandName(std::string command) { this->command = command; };
            void setArgs(Args args) { this->args = args; };
            
            bool execute();
            bool wasSuccessful() { return this->commandSuccess; };
            std::string reason() { return this->errorReason; }
        };
    }
}

#endif
