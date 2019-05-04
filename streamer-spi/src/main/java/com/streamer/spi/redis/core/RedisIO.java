package com.streamer.spi.redis.core;

import java.io.IOException;

public interface RedisIO {
	
	Object sendRaw(String command) throws IOException, RedisResultException;

	void close() throws IOException;
}
