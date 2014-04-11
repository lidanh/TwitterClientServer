package spl.twitter.reactor;

import spl.twitter.tokenizer.StompTokenizer;

/**
 * @name Tokenizer Factory
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * Creates Stomp Tokenizer obejcts
 * @see spl.twitter.tokenizer.StompTokenizer
 */
public interface TokenizerFactory<T> {
   StompTokenizer<T> create();
}
