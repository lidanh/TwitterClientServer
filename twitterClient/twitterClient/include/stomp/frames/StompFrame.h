//
//  StompFrame.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_STOMPFRAME_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_STOMPFRAME_H_

#include <string>
#include <map>

namespace stomp {
    typedef std::map<std::string, std::string> HeadersMap;
    class FrameVisitor;
    
    class StompFrame {
        
        
    public:
        const char FRAME_DELIMITER;
        
        virtual ~StompFrame() {}
        
        std::string getBody() const { return body; }
        void setBody(std::string body) { this->body = body; }
        std::string getCommand() const { return command; }
        void addHeaderPair(std::string key, std::string value) { headers[key] = value; }
        void removePair(std::string key) { addHeaderPair(key, NULL); }
        const std::string get(std::string key) const;
        HeadersMap::const_iterator headersBegin() const { return headers.begin(); };
        HeadersMap::const_iterator headersEnd() const { return headers.end(); };
        std::string toString() const;
        
        virtual void accept(FrameVisitor *visitor) = 0;
        
    protected:
        std::string command;    //  Type of command
        HeadersMap headers;     //  Map of headers (key,value)
        std::string body;       //  Frame body
        StompFrame(const std::string command_name);
        StompFrame(const std::string command_name, HeadersMap headers);
        StompFrame(const std::string command_name, HeadersMap headers, std::string body);
        StompFrame(const std::string command_name, const std::string body);
        StompFrame(const StompFrame& other);
    };
};  // namespace STOMP

#endif  // TWITTERCLIENT_TWITTERCLIENT_INCLUDE_STOMP_FRAMES_STOMPFRAME_H_
