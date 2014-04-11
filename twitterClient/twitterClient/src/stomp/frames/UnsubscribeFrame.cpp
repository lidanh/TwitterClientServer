//
//  UnsubscribeFrame.cpp
//  Object to represent the UNSUBSCRIBE frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/UnsubscribeFrame.h"

namespace stomp {
    UnsubscribeFrame::UnsubscribeFrame(std::string identifier): StompFrame(UNSUBSCRIBE_FRAME_NAME) {
        addHeaderPair("id", identifier);
    }
    
    void UnsubscribeFrame::accept(FrameVisitor *visitor)
    { }
}

