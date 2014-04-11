//
//  CommandFactory.cpp
//  A factory for building command objects from their string representation
//  Given a string input from the user, will parse and create the command object
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/commands/CommandFactory.h"

#include <iostream>
#include <string>
#include <boost/algorithm/string.hpp>
#include <boost/tokenizer.hpp>

#include "../../include/commands/LoginCommand.h"
#include "../../include/commands/FollowCommand.h"
#include "../../include/commands/UnfollowCommand.h"
#include "../../include/commands/TweetCommand.h"
#include "../../include/commands/ClientsCommand.h"
#include "../../include/commands/StatsCommand.h"
#include "../../include/commands/LogoutCommand.h"
#include "../../include/commands/StopCommand.h"



namespace twitter {
    
    namespace commands {
    
        //A factory method that accept a string representation of a command with arguments and a stomp client pointer
        // and returns an appropriate Command object that represents the string command
        Command * CommandFactory::createFactory(std::string const &command_string, stomp::TwitterClient *client) {

            Args argumentList;
            boost::split(argumentList, command_string, boost::is_any_of(" "), boost::token_compress_on);
            
            std::string command_name = argumentList[0];
            
            //Remove the first element - that being the command_name, leaving only the actual arguments in the list
            argumentList.erase(argumentList.begin());

            Command *cmd = 0;

            //Ugly, we know, but C++ can't do a switch-case on strings and we didn't have enought time to find a more elegant solution
            if (command_name == "login") {
                cmd = new LoginCommand(client, argumentList);
            } else if (command_name == "follow") {
                cmd = new FollowCommand(client, argumentList);
            } else if (command_name == "unfollow") {
                cmd = new UnfollowCommand(client, argumentList);
            } else if (command_name == "tweet") {
                cmd = new TweetCommand(client, argumentList);
            } else if (command_name == "clients") {
                cmd = new ClientsCommand(client, argumentList);
            } else if (command_name == "stats") {
                cmd = new StatsCommand(client, argumentList);
            } else if (command_name == "logout") {
                cmd = new LogoutCommand(client, argumentList);
            } else if (command_name == "stop") {
                cmd = new StopCommand(client, argumentList);
            } else {
                std::cerr << "Unknown command type" << std::endl;
            }
            
            return cmd;
        }
        
    } //COMMANDS namespace
    
}; //TWITTER namespace
