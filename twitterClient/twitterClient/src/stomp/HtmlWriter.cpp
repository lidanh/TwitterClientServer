//
//  HtmlWriter.cpp
//  A simple object to allow easy html output of received tweets.
//  Handles file creation and closing automatically for you.
//  All the client has to do is initiate this object with a username, and call addTweet on every tweet we receive.
//
//  Authors:    Ken Saggy
//              Lidan Hifi
//
//  Copyright 2013 Lidan Hifi, Ken Saggy

#include "../../include/stomp/HtmlWriter.h"
#include <string>
#include <iostream>
#include <fstream>

namespace stomp {

    HtmlWriter::HtmlWriter(const std::string &username): username(username), output_file()
    {
        this->open();
    }
    
    HtmlWriter::~HtmlWriter()
    {
        if (output_file)
        {
            output_file.close();
        }
    }
    
    void HtmlWriter::open()
    {
        std::string filename = username + ".html";
        output_file.open(filename.c_str() , std::ofstream::out | std::ofstream::trunc);
        if (!output_file.is_open()) {
            std::cerr << "Could not create " << filename << " file" << std::endl;
        }
        this->writeHeader();
        
    }
    
    void HtmlWriter::writeHeader()
    {
        if (!output_file.is_open()) { return; }
        output_file << "<!DOCTYPE html> <html lang=\"en\"> <head> <meta charset=\"utf-8\"> <title>@" << username << " Twitter Feed</title></head>";
        output_file << "</title> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> <meta name=\"description\" content=\"Twitter Feed\"> <meta name=\"author\" content=\"Lidan Hifi, Ken Saggy\"> <link href=\"assets/css/animate.min.css\" rel=\"stylesheet\"> <link href=\"assets/css/bootstrap.css\" rel=\"stylesheet\"> <link href=\"assets/css/bootstrap-responsive.css\" rel=\"stylesheet\"> <link href=\"assets/css/flexslider.css\" rel=\"stylesheet\"> <link rel=\"stylesheet\" href=\"assets/css/refineslide.css\"> <link rel=\"stylesheet\" href=\"assets/css/refineslide-theme-dark.css\"> <link href=\"assets/css/parallaxslider/style.css\" rel=\"stylesheet\"> <noscript> <link rel=\"stylesheet\" type=\"text/css\" href=\"css/parallax_slider/nojs.css\"/> </noscript> <link href=\"assets/css/font-awesome.min.css\" rel=\"stylesheet\"> <link href=\"assets/css/social.css\" rel=\"stylesheet\"> <link href=\"assets/css/style.css\" rel=\"stylesheet\" id=\"colors\"> <!--[if lt IE 7]><link href=\"assets/css/font-awesome-ie7.min.css\" rel=\"stylesheet\"><![endif]--> <link href='http://fonts.googleapis.com/css?family=Patua+One' rel='stylesheet' type='text/css'> <!--[if lt IE 9]><script src=\"http://html5shim.googlecode.com/svn/trunk/html5.js\" type=\"text/javascript\"></script><![endif]--> <link rel=\"shortcut icon\" href=\"assets/ico/favicon.ico\"> </head> <body data-spy=\"scroll\" data-target=\".scroller-spy\" data-twttr-rendered=\"true\"> <div class=\"main-wrapper\"> <section id=\"top-section\"> <div class=\"headertop needhead\"> <div class=\"action-banner-bg\"></div> <div class=\"action-banner-bg-top\"></div> <div class=\"nav-reaction fixing-custom\"> <div class=\"navbar navbar-static-top \"> <div class=\"navbar-inner\"> <div class=\"container\"> <a class=\"brand\" href=\"#\"> <span>@" << username << "<small>Twitter Feed</small></span></a> <div id=\"main-nav\" class=\"scroller-spy\"> <div class=\"nav-collapse collapse\"> <ul class=\"nav\" id=\"nav\"> <li class=\"active\"><a href=\"#top-section\">Home</a> </li> <li><a href=\"#about-section\">About us</a> </li> </ul> </div> </div> </div> </div> </div> </div> <div class=\"banner-rotator\"> <div id=\"da-slider\" class=\"da-slider featured-tweets\"> <nav class=\"da-arrows\"> <span class=\"da-arrows-prev\"></span> <span class=\"da-arrows-next\"></span> </nav> </div> </div> </div> </section> <div class=\"container\"> <div id=\"tweets-container\">";
        
    }
    
    void HtmlWriter::addTweet(const std::string &from_user, const std::string &message, const std::string &timestamp)
    {
        if (!output_file.is_open()) { return; }
        output_file << "<div class=\"row-fluid tweet-container\"> <div class=\"span12\"> <div class=\"row-fluid\"> <div class=\"span1\"> <span class=\"twitter-sign\"> <i class=\"fa-icon-twitter fa-icon-large main-color\"></i></span> </div> <div class=\"span11 large-text tweet\"> <p><span class=\"main-color\">";
        output_file << "<span class=\"username\">@" << from_user << "</span> // </span>";
        output_file << "<span class=\"tweetData\">" << message << "</span></p>";
        output_file << "<p class=\"timestamp\">" << timestamp << "</p>";
        output_file << "</div> </div> </div> </div>";
    }
    
    void HtmlWriter::writeFooter()
    {
        if (!output_file.is_open()) { return; }
        output_file << "</div> <section id=\"about-section\"> <div class=\"color-bottom-line row\"> <div class=\"span12\"> <h3 class=\"line center standart-h2title\"> <span class=\"large-text main-color\">ABOUT US</span> </h3> </div> </div> <div class=\"container team\"> <div class=\"row-fluid\"> <div class=\"thumbnail span3 team-item\"> <div class=\"sample project-item-image-container\"> <img src=\"images/ken.jpg\" alt=\"\"/> </div> <div class=\"caption team-caption\"> <div class=\"transit-to-top\"> <h3 class=\"\">Ken Saggy <small>C++ Client Developer</small></h3> <div class=\"widget_nav_menu\"> <ul class=\"socialIcons\"> <li class=\"facebook\"><a href=\"https://www.facebook.com/kensaggy\">facebook </a></li> <li class=\"linkedin\"><a href=\"http://www.linkedin.com/in/kensaggy\">linkedin </a></li> <li class=\"twitter\"><a href=\"https://twitter.com/hackingllama\">twitter</a></li> </ul> </div> </div> </div> </div> <div class=\"thumbnail span3 team-item\"> <div class=\"sample project-item-image-container\"> <img src=\"images/lidan.jpg\" alt=\"\"/> </div> <div class=\"caption team-caption\"> <div class=\"transit-to-top\"> <h3 class=\"\">Lidan Hifi <small>Java Server Developer</small></h3> <div class=\"widget_nav_menu\"> <ul class=\"socialIcons\"> <li class=\"facebook\"><a href=\"https://www.facebook.com/lidan\">facebook </a></li> <li class=\"linkedin\"><a href=\"http://www.linkedin.com/in/lidan\">linkedin </a></li> <li class=\"twitter\"><a href=\"https://twitter.com/Lidanh\">twitter</a></li> </ul> </div> </div> </div> </div> </div> </div> </section> </div> <section id=\"contact-section\"> <footer class=\"footer\"> <div class=\"container\"> <hr class=\"half1 copyhr\"> <div class=\"row-fluid copyright\"> <div class=\"span12 center\">Copyright &copy; 2014. Lidan Hifi &amp; Ken Saggy</div> </div> </div> </footer> </section> <!--[END] MAIN WRAPPER--> </div> <script type=\"text/javascript\" src=\"assets/js/jquery-1.7.1.min.js\"></script> <script type=\"text/javascript\" src=\"assets/js/modernizr.custom.28468.js\"></script> <script src=\"assets/js/bootstrap.min.js\" type=\"text/javascript\"></script> <script type=\"text/javascript\" src=\"assets/js/jquery.easing.js\"></script> <script src=\"assets/js/superfish.js\" type=\"text/javascript\"></script> <script src=\"assets/js/custom.js\" type=\"text/javascript\"></script> <script src=\"assets/js/jquery.ui.totop.js\" type=\"text/javascript\"></script> <script type=\"text/javascript\" src=\"assets/js/jquery.mousewheel.js\"></script> <script src=\"assets/js/jquery.flexslider-min.js\" type=\"text/javascript\"></script> <script type=\"text/javascript\" src=\"assets/js/jquery.cslider.js\"></script> <script type=\"text/javascript\" src=\"assets/js/jquery.parallax-1.1.3.js\"></script> <script type=\"text/javascript\" src=\"assets/js/jquery.localscroll-1.2.7-min.js\"></script> <script type=\"text/javascript\" src=\"assets/js/jquery.scrollTo-1.4.2-min.js\"></script> <script type=\"text/javascript\" src=\"assets/js/jquery.inview.js\"></script> <script type=\"text/javascript\" src=\"assets/js/moment.min.js\"></script> <script src=\"assets/js/respond.min.js\"></script> <script src=\"assets/js/jquery.refineslide.js\"></script> <script src=\"assets/js/custom_refine_slider.js\"></script><script type=\"text/javascript\">jQuery(document).ready(function(e){jQuery(\"#nav\").localScroll(800);jQuery(\"#features-section\").parallax(\"50%\",0.1);jQuery(\"#about-section\").parallax(\"50%\",0.1);jQuery(\"#tweets-container\").parallax(\"20%\",0.4);jQuery(\".bg\").parallax(\"50%\",0.4);var b=e(\"#tweets-container\");b.children().each(function(h,g){b.prepend(g)});var a=3;for(var c=0;c<(b.children().length<a?b.children().length:a);c++){var f=b.children().eq(c);e(\"<div class='da-slide'><h2>\"+f.find(\".username\").html()+\"</h2><p class='large-text'>\"+f.find(\".tweetData\").html()+\"</p><span class='da-link'><a href='#tweets-container'><span class='main-link'>View Tweet</span> <span class='arrow'> <i class='fa-icon-eye-open'></i></span></a></span><div class='da-img visible-desktop'><img src='images/\"+(c+1)+\".png' /></div></div>\").prependTo(\".featured-tweets\")}e(\".timestamp\").each(function(g,h){e(h).html(moment(new Date(1000 * e(h).html())).fromNow())});e(\"#da-slider\").cslider({autoplay:true,bgincrement:50})});</script></body></html>";
    }
    
    void HtmlWriter::close()
    {
        this->writeFooter();
        if (output_file.is_open())
        {
            output_file.close();
        }
    }
}