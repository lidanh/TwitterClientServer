package spl.twitter.reactor;

import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;

import spl.twitter.engine.AsyncProtocolFactory;

/**
 * @name Reactor Data
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * a simple data structure that hold information about the reactor, including getter methods
 */
public class ReactorData<T> {

    private final ExecutorService _executor;
    private final Selector _selector;
    private final AsyncProtocolFactory<T> _protocolMaker;
    private final TokenizerFactory<T> _tokenizerMaker;
    
    public ExecutorService getExecutor() {
        return _executor;
    }

    public Selector getSelector() {
        return _selector;
    }

	public ReactorData(ExecutorService _executor, Selector _selector, AsyncProtocolFactory<T> protocol, TokenizerFactory<T> tokenizer) {
		this._executor = _executor;
		this._selector = _selector;
		this._protocolMaker = protocol;
		this._tokenizerMaker = tokenizer;
	}

	public AsyncProtocolFactory<T> getProtocolMaker() {
		return _protocolMaker;
	}

	public TokenizerFactory<T> getTokenizerMaker() {
		return _tokenizerMaker;
	}

}
