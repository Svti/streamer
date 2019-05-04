package com.streamer.spi.redis.resp;

import java.sql.ResultSet;
import java.util.Arrays;

import com.streamer.spi.redis.core.RedisConnection;
import com.streamer.spi.redis.core.RedisCursorResultSet;

public class RedisScanResponse implements RedisResponse {

	public static final RedisResponse INSTANCE = new RedisScanResponse();

	private RedisScanResponse() {
	}

	@Override
	public ResultSet processResponse(final RedisConnection connection, final String command, final Object response) {
		Object[] values = (Object[]) response;

		int cursor = Integer.parseInt((String) values[0]);
		Object[] list = (Object[]) values[1];

		return new RedisCursorResultSet(connection, cursor, command, Arrays.copyOf(list, list.length, String[].class));
	}

}
