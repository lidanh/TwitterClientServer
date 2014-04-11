//
//  SendFrame.cpp
//  Object to represent the SEND frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/SendFrame.h"

namespace stomp {
    SendFrame::SendFrame(std::string destination, std::string body): StompFrame(SEND_FRAME_NAME, body) {
        addHeaderPair("destination", destination);
    }
    
    void SendFrame::accept(FrameVisitor *visitor)
    { }
}
