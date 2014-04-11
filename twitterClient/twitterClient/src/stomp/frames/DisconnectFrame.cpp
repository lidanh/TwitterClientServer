//
//  DisconnectFrame.cpp
//  Object to represent the DISCONNECT frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/DisconnectFrame.h"

namespace stomp {
    DisconnectFrame::DisconnectFrame(std::string receipt): StompFrame(DISCONNECT_FRAME_NAME) {
        addHeaderPair("receipt", receipt);
    }
    
    void DisconnectFrame::accept(FrameVisitor *visitor)
    {
    }
}
