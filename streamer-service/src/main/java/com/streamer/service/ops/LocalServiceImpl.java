package com.streamer.service.ops;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.streamer.core.support.Signal;

public class LocalServiceImpl implements LocalService {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public int stopLocalNodeByName(String token, String name) {
		if (Signal.map.get(token) != null) {
			Signal.map.get(token).set(0L);
		}
		Signal.map.remove(token);
		return jdbcTemplate.update("UPDATE jobs SET status = ? , token = ? WHERE name =?", 0, null, name);
	}
}
