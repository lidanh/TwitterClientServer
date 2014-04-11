//
//  ReceiptFrame.cpp
//  Object to represent the RECEIPT frame type
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../../include/stomp/frames/ReceiptFrame.h"

namespace stomp {
    ReceiptFrame::ReceiptFrame(): StompFrame(RECEIPT_FRAME_NAME) { }
    
    ReceiptFrame::ReceiptFrame(std::string receipt): StompFrame(RECEIPT_FRAME_NAME) {
        addHeaderPair("receipt-id", receipt);
    }
    
    void ReceiptFrame::accept(FrameVisitor *visitor)
    {
        visitor->visit(this);
    }
}
