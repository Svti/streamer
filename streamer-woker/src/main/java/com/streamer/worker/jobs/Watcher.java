package com.streamer.worker.jobs;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.streamer.service.context.AppService;
import com.streamer.service.core.StreamerConstant;
import com.streamer.service.core.StreamerEnvironment;
import com.streamer.service.core.StreamerRole;

@Configuration
@EnableScheduling
public class Watcher {

	@Resource
	private AppService appService;

	@Resource
	private Environment environment;

	@Resource
	private StreamerEnvironment streamerEnvironment;

	@Scheduled(fixedRate = StreamerConstant.TIMEOUT * 1000)
	public void online() {
		String node = streamerEnvironment.getNode();
		String host = streamerEnvironment.getHost();
		int port = streamerEnvironment.getPort();
		appService.online(node, host, port, StreamerRole.SLAVE, new Date());
	}
}
