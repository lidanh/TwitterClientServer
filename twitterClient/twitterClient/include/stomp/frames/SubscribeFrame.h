//
//  SubscribeFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_SUBSCRIBEFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_SUBSCRIBEFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define SUBSCRIBE_FRAME_NAME "SUBSCRIBE"

namespace stomp {
    class SubscribeFrame : public StompFrame {
    public:
        SubscribeFrame(std::string destination, std::string identifier);
        virtual ~SubscribeFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP


#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_SUBSCRIBEFRAME_H_
