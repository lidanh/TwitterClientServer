//
//  ConnectedFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_CONNECTEDFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_CONNECTEDFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define CONNECTED_FRAME_NAME "CONNECTED"

namespace stomp {
    class ConnectedFrame : public StompFrame {
    public:
        ConnectedFrame();
        ConnectedFrame(std::string version);
        virtual ~ConnectedFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_CONNECTEDFRAME_H_
