//
//  MessageFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_MESSAGEFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_MESSAGEFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define MESSAGE_FRAME_NAME "MESSAGE"

namespace stomp {
    class MessageFrame : public StompFrame {
    public:
        MessageFrame();
        MessageFrame(std::string destination, std::string subscription, std::string messageId, std::string body);
        virtual ~MessageFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_MESSAGEFRAME_H_
