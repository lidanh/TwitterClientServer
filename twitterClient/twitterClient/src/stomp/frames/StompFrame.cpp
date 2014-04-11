//
//  StompFrame.cpp
//  Object to represent a generic STOMP frame.
//  It supports a headers map, frame type(name), frame body
//  and other useful features. Each frame type should inherit from this class
//  All frames must support the VISITOR pattern by implementing their own visit() method to allow custom code to run upon receving these frames
//  That custom code should be placed in FrameVisitor.cpp
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/StompFrame.h"
#include <Poco/Format.h>
#include <Poco/StringTokenizer.h>

using Poco::format;
using Poco::StringTokenizer;

namespace stomp {
    // Constructors
    StompFrame::StompFrame(const std::string command_name): FRAME_DELIMITER('\0'), command(command_name), headers(), body("\n"){};
    StompFrame::StompFrame(const std::string command_name, const std::string body): FRAME_DELIMITER('\0'), command(command_name), headers(), body(body) {};
    StompFrame::StompFrame(const std::string command_name, HeadersMap headers): FRAME_DELIMITER('\0'),command(command_name), headers(headers), body("\n") {};
    StompFrame::StompFrame(const std::string command_name, HeadersMap headers, std::string body): FRAME_DELIMITER('\0'), command(command_name), headers(headers), body(body) {};

    
    const std::string StompFrame::get(std::string key) const
    {
        HeadersMap::const_iterator it = headers.find(key);
        return (it != headers.end()) ? it->second : "";
    }
    
    std::string StompFrame::toString() const {
        std::string headersStr = "";
        
        // build headers string [key:value]
        for (HeadersMap::const_iterator it = headers.begin(); it != headers.end(); ++it) {
            headersStr += Poco::format("%s:%s\n", it->first, it->second);
        }
        
        return Poco::format("%s\n%s\n\n%s\n%c", command, headersStr, body, FRAME_DELIMITER);
    }
}
