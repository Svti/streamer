package com.streamer.spi.redis.resp;

import java.sql.ResultSet;

import com.streamer.spi.redis.core.RedisConnection;
import com.streamer.spi.redis.core.RedisResultSet;

public class RedisSimpleResponse implements RedisResponse {

	public static final RedisResponse INSTANCE = new RedisSimpleResponse();

	private RedisSimpleResponse() {
	}

	@Override
	public ResultSet processResponse(final RedisConnection connection, final String command, final Object response) {
		return new RedisResultSet(new String[] { (String) response });
	}
}
