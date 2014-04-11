//
//  MessageFrame.cpp
//  Object to represent the MESSAGE frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/MessageFrame.h"

namespace stomp {
    MessageFrame::MessageFrame(): StompFrame(MESSAGE_FRAME_NAME) { }
    
    MessageFrame::MessageFrame(std::string destination, std::string subscription, std::string messageId, std::string body): StompFrame(MESSAGE_FRAME_NAME, body) {
        addHeaderPair("destination", destination);
        addHeaderPair("subscription", subscription);
        addHeaderPair("message-id", messageId);
    }
    
    void MessageFrame::accept(FrameVisitor *visitor)
    {
        visitor->visit(this);
    }
}
