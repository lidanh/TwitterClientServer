//
//  HtmlWriter.h
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#ifndef TWITTERCLIENT_TWITTERCLIENT_INCLUDE_HTMLWRITER_H_
#define TWITTERCLIENT_TWITTERCLIENT_INCLUDE_HTMLWRITER_H_

#include <iostream>
#include <fstream>

namespace stomp {
    
    
    class HtmlWriter {
        
    protected:
        
        std::string username;
        std::ofstream output_file;
        
        void open(); //creates and opens the file
        void writeHeader(); //writes the header info stuff
        void writeFooter(); // writes footer and closing tags
        
        
    public:
        HtmlWriter(const std::string &username);
        ~HtmlWriter();
        void addTweet(const std::string &from_user, const std::string &message, const std::string &timestamp);
        void close(); //close the file and goodbye
    };
}

#endif
