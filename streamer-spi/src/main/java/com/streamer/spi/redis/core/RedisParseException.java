package com.streamer.spi.redis.core;

public class RedisParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RedisParseException() {}
	public RedisParseException(String msg) {
		super(msg);
	}

}
