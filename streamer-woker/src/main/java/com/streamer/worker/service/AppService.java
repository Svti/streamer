package com.streamer.worker.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.streamer.service.core.StreamerRole;
import com.streamer.worker.view.PaginationResult;

public interface AppService {

	public int save(String node, String name, String sql, Date now);

	public int update(String node, String name, String sql);

	public int start(String node, String name, String token);

	public int stop(String token, String node, String name);

	public int stopAll(String node);

	public boolean exsit(String name);

	public List<Map<String, Object>> findJobByName(String name);

	public PaginationResult<Map<String, Object>> findAll(String name, String node, String sql, int current, int szie);

	public Map<String, Object> findTotal(int second);

	public int del(String name);

	public int online(String node, String host, int port, StreamerRole streamerRole, Date date);

	public List<Map<String, Object>> findAlivableNode(int second);

	public List<String> expireNodes(int timeout);

	public Map<String, Object> findMasterNode(int second);

	public Map<String, Object> findNodesByName(String node);

	public List<Map<String, Object>> findJobsByNode(String node, int status);

}
