//
//  ReceiptFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_RECEIPTFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_RECEIPTFRAME_H_

#include <string>
#include "../../../include/stomp/frames/FrameVisitor.h"
#include "../../../include/stomp/frames/StompFrame.h"

#define RECEIPT_FRAME_NAME "RECEIPT"

namespace stomp {
    
    class ReceiptFrame : public StompFrame {
    public:
        ReceiptFrame();
        ReceiptFrame(std::string receipt);
        virtual ~ReceiptFrame() {}
        virtual void accept(FrameVisitor *visitor);
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_RECEIPTFRAME_H_
