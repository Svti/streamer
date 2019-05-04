package com.streamer.spi.redis.resp;

import java.sql.ResultSet;
import java.util.Arrays;

import com.streamer.spi.redis.core.RedisConnection;
import com.streamer.spi.redis.core.RedisResultSet;

public class RedisArrayResponse implements RedisResponse {

	public static final RedisResponse INSTANCE = new RedisArrayResponse();

	private RedisArrayResponse() {
	}

	@Override
	public ResultSet processResponse(final RedisConnection connection, final String command, final Object response) {
		Object[] list = (Object[]) response;
		return new RedisResultSet(Arrays.copyOf(list, list.length, String[].class));
	}

}
