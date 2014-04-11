//
//  ConnectedFrame.cpp
//  Object to represent the CONNECTED frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/ConnectedFrame.h"

namespace stomp {
    ConnectedFrame::ConnectedFrame(): StompFrame(CONNECTED_FRAME_NAME) { }
    
    ConnectedFrame::ConnectedFrame(std::string version): StompFrame(CONNECTED_FRAME_NAME) {
        addHeaderPair("version", version);
    }
    
    void ConnectedFrame::accept(FrameVisitor *visitor)
    {
        visitor->visit(this);
    }
    
}
