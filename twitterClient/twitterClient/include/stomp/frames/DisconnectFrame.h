//
//  DisconnectFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_DISCONNECTFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_DISCONNECTFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define DISCONNECT_FRAME_NAME "DISCONNECT"

namespace stomp {
    class DisconnectFrame : public StompFrame {
    public:
        DisconnectFrame(std::string receipt);
        virtual ~DisconnectFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_DISCONNECTFRAME_H_
