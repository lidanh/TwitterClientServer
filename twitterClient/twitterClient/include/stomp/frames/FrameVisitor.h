//
//  FrameVisitor.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_FRAMEVISITOR_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_FRAMEVISITOR_H_

#include "../../../include/stomp/TwitterClient.h"
#include "../../../include/stomp/frames/ErrorFrame.h"
#include "../../../include/stomp/frames/MessageFrame.h"
#include "../../../include/stomp/frames/ReceiptFrame.h"
#include "../../../include/stomp/frames/ConnectedFrame.h"

namespace stomp {
    
    class ErrorFrame;
    class MessageFrame;
    class ReceiptFrame;
    class ConnectedFrame;
    
    class FrameVisitor {
        
    protected:
        TwitterClient *client; //in case and visitor needs access to client methods - which it usually does
        
    public:
        FrameVisitor(TwitterClient *client) : client(client) {};
        virtual ~FrameVisitor() {};
        FrameVisitor(const stomp::FrameVisitor&);
        const FrameVisitor& operator=(const stomp::FrameVisitor&);
        virtual void visit(ConnectedFrame *frame);
        virtual void visit(ErrorFrame *frame);
        virtual void visit(MessageFrame *frame);
        virtual void visit(ReceiptFrame *frame);
    };
}



#endif
