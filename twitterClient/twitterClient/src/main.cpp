#include "../include/stomp/TwitterClient.h"

/**
 * This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
 */
int main (int argc, char *argv[]) {
    
    //Create our client instance that takes care of everything
    stomp::TwitterClient *client = new stomp::TwitterClient();
    client->startInterface();
    delete client;
    client = 0;
    return 0;

}
