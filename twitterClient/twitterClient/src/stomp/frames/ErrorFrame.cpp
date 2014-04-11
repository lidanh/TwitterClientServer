//
//  ErrorFrame.cpp
//  Object to represent the ERROR frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/ErrorFrame.h"

namespace stomp {
    ErrorFrame::ErrorFrame(): StompFrame(ERROR_FRAME_NAME) { }
    
    ErrorFrame::ErrorFrame(std::string message): StompFrame(ERROR_FRAME_NAME) {
        addHeaderPair("message", message);
    }
    
    ErrorFrame::ErrorFrame(std::string message, std::string body): StompFrame(ERROR_FRAME_NAME, body) {
        addHeaderPair("message", message);
    }
    
    void ErrorFrame::accept(FrameVisitor *visitor)
    {
        visitor->visit(this);
    }
}
