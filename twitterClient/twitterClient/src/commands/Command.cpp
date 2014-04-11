//
//  Command.cpp
//  Abstract command object. gives a generic approach to extending functionality
//  Any inheriting class must implement it's own run() method which will be automatically called by the commands execute() method after calling beforeRun(), and before calling afterRun(), both of which are optional to override in case the inheriting class needs this functionality
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/Command.h"

namespace twitter {
    
    namespace commands {
    
    
        // Constructors
        Command::Command(const std::string command, stomp::TwitterClient *client): command(command),client(client), args(), commandSuccess(true), errorReason("") {};
        Command::Command(const std::string command, stomp::TwitterClient *client , Args args)  :command(command), client(client), args(args), commandSuccess(true),errorReason("") {};
        
        const Command& Command::operator=(const Command &command) {
                return *this;
        }
        
        Command::Command(const Command &other) : command(other.command),client(other.client), args(other.args), commandSuccess(other.commandSuccess), errorReason(other.errorReason) {}
        
        
        Command::~Command(){}
        
        void Command::beforeRun()
        {
            if (!this->client->isConnected())
            {
                this->commandSuccess = false;
                this->errorReason = "Client is not connected";
            }
        }

        
        bool Command::execute() {
            
            
            //Poor mans exception handling to avoid memory leaks. Will not execute before() , run() or after() if at any state the commands fails (could be at constructor, before, run or after.
            if (commandSuccess)
            {
                beforeRun();
            }
            if (commandSuccess)
            {
                run();
            }
            if (commandSuccess)
            {
                afterRun();
            }

            if (!commandSuccess)
            {
                std::cerr << "Something prevented command " << command << " from running" << std::endl;
            }
            
            return commandSuccess;
        }
        
    }
    
}
