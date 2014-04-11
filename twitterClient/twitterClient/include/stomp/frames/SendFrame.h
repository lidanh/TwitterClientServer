//
//  SendFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_SENDFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_SENDFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define SEND_FRAME_NAME "SEND"

namespace stomp {
    class SendFrame : public StompFrame {
    public:
        SendFrame(std::string destination, std::string body);
        virtual ~SendFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP


#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_SENDFRAME_H_
