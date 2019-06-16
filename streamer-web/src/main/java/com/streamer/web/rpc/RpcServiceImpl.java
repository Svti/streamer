package com.streamer.web.rpc;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamer.service.context.AppService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class RpcServiceImpl implements RpcService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private OkHttpClient http;

	@Resource
	private AppService appService;

	@Override
	public boolean run(String node, String name) throws IOException {
		Map<String, Object> map = appService.findNodesByName(node);

		if (map == null) {
			return false;
		}

		String url = "http://" + String.valueOf(map.get("host")) + ":" + String.valueOf(map.get("port"))
				+ "/executor/run/" + name;

		Response response = http.newCall(new Request.Builder().url(url).get().build()).execute();

		logger.info("Rpc call url:{} , resp[ code:{} , body:{}]", url, response.code(), response.body().string());

		boolean flag = response.isSuccessful();

		response.close();

		if (flag) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean stop(String node, String name) throws IOException {
		Map<String, Object> map = appService.findNodesByName(node);

		if (map == null) {
			return false;
		}

		String url = "http://" + String.valueOf(map.get("host")) + ":" + String.valueOf(map.get("port"))
				+ "/executor/stop/" + name;
		Response response = http.newCall(new Request.Builder().url(url).get().build()).execute();

		logger.info("Rpc call url:{} , resp[ code:{} , body:{}]", url, response.code(), response.body().string());

		boolean flag = response.isSuccessful();

		response.close();

		if (flag) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String log(String node, String name, long n) throws IOException {
		Map<String, Object> map = appService.findNodesByName(node);

		if (map == null) {
			logger.warn("Rpc call not found node:{}", node);
			return StringUtils.EMPTY;
		}

		String url = "http://" + String.valueOf(map.get("host")) + ":" + String.valueOf(map.get("port"))
				+ "/executor/log/" + name + "/" + n;

		Response response = http.newCall(new Request.Builder().url(url).get().build()).execute();

		boolean flag = response.isSuccessful();

		if (flag) {
			JsonNode jsonNode = new ObjectMapper().readTree(response.body().string());
			logger.info("Rpc call node:{} , url:{} , resp[ code:{}]", node, url, response.code());
			response.close();
			if (jsonNode.has("data")) {
				String log = jsonNode.get("data").asText();
				return log;
			} else {
				return StringUtils.EMPTY;
			}

		} else {
			logger.info("Rpc call node:{} url:{} , resp[ code:{} , body:{}]", node, url, response.code(),
					response.body().string());
			response.close();
			return StringUtils.EMPTY;
		}
	}

}
