//
//  StompFrameFactory.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef twitterClient_StompFrameFactory_h
#define twitterClient_StompFrameFactory_h

#include "../frames/StompFrame.h"
#include <string>

namespace stomp {
    class StompFrameFactory {
    public:
        static StompFrame* parse(std::string frameStr);
    private:
        StompFrameFactory();
    };
}   // namespace STOMP

#endif
