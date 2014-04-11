//
//  SubscribeFrame.cpp
//  Object to represent the SUBSCRIBE frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/SubscribeFrame.h"

namespace stomp {
    SubscribeFrame::SubscribeFrame(std::string destination, std::string identifier): StompFrame(SUBSCRIBE_FRAME_NAME) {
        addHeaderPair("destination", destination);
        addHeaderPair("id", identifier);
    }
    
    void SubscribeFrame::accept(FrameVisitor *visitor)
    { }
}

