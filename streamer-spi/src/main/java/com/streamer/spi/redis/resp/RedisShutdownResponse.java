package com.streamer.spi.redis.resp;

import java.sql.ResultSet;

import com.streamer.spi.redis.core.RedisConnection;

public class RedisShutdownResponse implements RedisResponse {

	public static final RedisResponse INSTANCE = new RedisShutdownResponse();

	private RedisShutdownResponse() {

	}

	@Override
	public ResultSet processResponse(final RedisConnection connection, final String command, final Object response) {
		return null;
	}
}
