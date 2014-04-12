# Twitter Client & Server

![image](https://dl.dropboxusercontent.com/u/4041100/github/twitter.jpg)

This project was an assignment for **systems programming course**, Ben Gurion University, December 2013. You can read the assignment requirements [here](http://www.cs.bgu.ac.il/~spl141/wiki.files/assignment4_ver2.pdf). I got 120/100 for this assignment (Reactor server was a bonus part).

The project is a terminal-twitter clone (client+server) with **STOMP protocol**, including login, public twitting, follow and @tagging.

**The server** was implemented in ***Java***, in two ways: Thread-per-client server, and a scalable non-blocking-IO server based on ***[Reactor Pattern](http://en.wikipedia.org/wiki/Reactor_pattern)***. Both of them share the same engine of course, for simplicity and modularity. logging implemented with ***log4j***. build process with ***ant***.

**The client** was implemented in **C++**, with ***boost*** library for multithreading. Fully compatible with ***Active MQ*** (Same STOMP Frames). 
The client creates a responsive HTML output file for user's feed, built with ***twitter bootstrap***. [for example](http://www.cs.bgu.ac.il/~lidanh/lidan.html):

[![image](https://dl.dropboxusercontent.com/u/4041100/github/twitter_client.jpg)](http://www.cs.bgu.ac.il/~lidanh/lidan.html)


## Project Structure

The project has 4 sub-projects:

* Twitter Client
* Twitter Engine
* Thread-per-Client
* Reactor Server

The *Reactor server* and the *Thread-per-client server* uses the twitter engine project. The twitter client is independent, and most of its features can work even with ActiveMQ server.


![image](https://dl.dropboxusercontent.com/u/4041100/github/twitter_tech.jpg)
