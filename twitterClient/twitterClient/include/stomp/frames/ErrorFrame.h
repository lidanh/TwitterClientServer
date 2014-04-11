//
//  ErrorFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_ERRORFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_ERRORFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"


#define ERROR_FRAME_NAME "ERROR"

namespace stomp {

    class ErrorFrame : public StompFrame {
    public:
        ErrorFrame();
        ErrorFrame(std::string message);
        ErrorFrame(std::string message, std::string body);
        
        virtual void accept(FrameVisitor *visitor);
        virtual ~ErrorFrame() {}
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_ERRORFRAME_H_
