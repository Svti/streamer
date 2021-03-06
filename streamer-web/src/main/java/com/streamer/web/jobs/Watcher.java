package com.streamer.web.jobs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.streamer.service.context.AppService;
import com.streamer.service.core.StreamerConstant;
import com.streamer.service.core.StreamerRole;

@Configuration
@EnableScheduling
public class Watcher {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private AppService appService;

	@Resource
	private Environment environment;

	@Scheduled(fixedRate = StreamerConstant.TIMEOUT * 1000)
	public void online() {
		// 在线监听
		appService.online(StreamerConstant.MASTER_NAME, StreamerConstant.MASTER_HOST,
				Integer.valueOf(environment.getProperty("server.port")), StreamerRole.MASTER, new Date());

		List<String> nodes = appService.expireNodes(StreamerConstant.TIMEOUT * 3);

		for (String enode : nodes) {
			for (Map<String, Object> map : appService.findJobsByNode(enode, StreamerConstant.RUNNING_JOB)) {
				String name = String.valueOf(map.get("name"));
				try {
					appService.stop(String.valueOf(map.get("token")), enode, name);
					logger.info("Found node:{} is expire , job:{} will be kill", enode, name);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
}
