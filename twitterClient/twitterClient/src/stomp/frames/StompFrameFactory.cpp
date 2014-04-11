//
//  StompFrameFactory.cpp
//  This is a factory object for constructing different FRAME objects base on the input string (received from the connection).
//  The input string is parsed and splitted into it's elements: Type, Headers, Body and a StompFrame type object is constructed with this info and returned
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy


#include "../../../include/stomp/frames/StompFrameFactory.h"

#include <boost/algorithm/string.hpp>

#include "../../../include/stomp/frames/StompFrame.h"
#include "../../../include/stomp/frames/ConnectedFrame.h"
#include "../../../include/stomp/frames/ErrorFrame.h"
#include "../../../include/stomp/frames/MessageFrame.h"
#include "../../../include/stomp/frames/ReceiptFrame.h"
#include <vector>
#include <string>

#include <iostream>


namespace stomp {
    StompFrame* StompFrameFactory::parse(std::string frameStr) {
        
        //First break apart the string to type+headers and body - these are seperated by 2 new lines
        unsigned long pos = frameStr.find("\n\n");
        std::string typeAndHeaders = frameStr.substr(0, pos);
        std::string body = frameStr.substr(pos+1, frameStr.length());
        boost::trim(body);
        boost::trim(typeAndHeaders);
        
        std::vector<std::string> tokens;
        boost::split(tokens, typeAndHeaders, boost::is_any_of("\n"), boost::token_compress_off);
        
        // define the frame type and create the proper frame
        StompFrame* frame = 0;
        
        std::string frameType = (tokens[0] == "") ? tokens[1] : tokens[0]; //patch because it seems that activeMQ starts MESSAGE frames with \n
        if (frameType == "CONNECTED") {
            // CONNECTED Frame
            frame = new ConnectedFrame();
        } else if (frameType == "ERROR") {
            // ERROR Frame
            frame = new ErrorFrame();
        } else if (frameType == "MESSAGE") {
            // MESSAGE Frame
            frame = new MessageFrame();
        } else if (frameType == "RECEIPT") {
            // RECEIPT Frame
            frame = new ReceiptFrame();
        } else {
            return NULL;
        }
        
        // add headers (starting from the second line)
        std::vector<std::string>::iterator it = tokens.begin() + 1;
        while (it != tokens.end() && (*it) != "") {
            std::string::size_type index = it->find(":", 0);
            if (index != std::string::npos) {
                // valid header
                frame->addHeaderPair(it->substr(0, index), it->substr(index + 1, it->length()));
            }
            ++it;
        }
        
        frame->setBody(body);
        
        return frame;
    }
}
