//
//  ConnectFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_CONNECTFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_CONNECTFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define CONNECT_FRAME_NAME "CONNECT"

namespace stomp {
    class ConnectFrame : public StompFrame {
    public:
        ConnectFrame();
        ConnectFrame(std::string host, std::string login, std::string password);
        virtual ~ConnectFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_CONNECTFRAME_H_
