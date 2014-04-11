//
//  UnsubscribeFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_UNSUBSCRIBEFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_UNSUBSCRIBEFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define UNSUBSCRIBE_FRAME_NAME "UNSUBSCRIBE"

namespace stomp {
    class UnsubscribeFrame : public StompFrame {
    public:
        UnsubscribeFrame(std::string identifier);
        virtual ~UnsubscribeFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP


#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_UNSUBSCRIBEFRAME_H_
