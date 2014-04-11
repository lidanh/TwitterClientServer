//
//  ConnectFrame.cpp
//  Object to represent the CONNECT frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/ConnectFrame.h"

namespace stomp {
    ConnectFrame::ConnectFrame(): StompFrame(CONNECT_FRAME_NAME) {
        addHeaderPair("accept-version", "1.2");
    }
    
    ConnectFrame::ConnectFrame(std::string host, std::string login, std::string password): StompFrame(CONNECT_FRAME_NAME) {
        addHeaderPair("accept-version", "1.2");
        addHeaderPair("host", host);
        addHeaderPair("login", login);
        addHeaderPair("passcode", password);
    }
    
    void ConnectFrame::accept(FrameVisitor *visitor)
    {
    }
}
