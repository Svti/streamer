package com.streamer.spi.redis.resp;

import java.sql.ResultSet;

import com.streamer.spi.redis.core.RedisConnection;

public interface RedisResponse {
	ResultSet processResponse(RedisConnection connection, String command, Object response);
}
