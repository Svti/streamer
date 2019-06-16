package com.streamer.worker.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import com.mysql.jdbc.StringUtils;
import com.streamer.core.exception.StreammerException;
import com.streamer.service.context.AppService;
import com.streamer.service.core.StreamerEnvironment;
import com.streamer.service.core.StreamerRole;
import com.streamer.worker.constant.Constant;
import com.streamer.worker.utils.WebUtils;

@SpringBootApplication
@ComponentScan(basePackages = "com.streamer")
public class WorkerApplication implements EmbeddedServletContainerCustomizer, ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private Environment environment;

	@Resource
	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		System.setProperty("saffron.default.charset", "UTF-8");
		System.setProperty("saffron.default.nationalcharset", "UTF-8");
		System.setProperty("calcite.default.charset", "UTF-8");
		System.setProperty("calcite.default.nationalcharset", "UTF-8");
		StreamerEnvironment env = context.getBean(StreamerEnvironment.class);

		// worker 节点，必定为slave
		env.setMaster(false);
		String node = env.getNode();
		String host = env.getHost();
		int port = env.getPort();

		AppService appService = context.getBean(AppService.class);

		// 停止当前节点所有任务
		appService.stopAll(node);

		// 判断是否有主节点
		Map<String, Object> map = appService.findMasterNode(Constant.TIMEOUT);

		do {
			map = appService.findMasterNode(Constant.TIMEOUT);
			if (map == null) {
				try {
					logger.error("The are not found master in this cluster , please check it and config it. After "
							+ Constant.TIMEOUT + " seconds and try...");
					Thread.sleep(Constant.TIMEOUT * 1000);
				} catch (InterruptedException e) {
				}
			} else {
				appService.online(node, host, port, StreamerRole.SLAVE, new Date());
			}
		} while (map == null);

	}

	@Bean(name = "streamerEnvironment")
	public StreamerEnvironment streamerEnvironment() {
		StreamerEnvironment current = new StreamerEnvironment();

		current.setMaster(Boolean.valueOf(environment.getProperty("node.master")));

		String _port = environment.getProperty("http-server.http.port");
		int port = 19991;

		if (StringUtils.isEmptyOrWhitespaceOnly(_port)) {
			logger.warn("The http-server.http.port is not found is config file, please check it!");
		} else {
			port = Integer.valueOf(_port);
		}

		logger.info("The streamer system set port as {}", port);
		current.setPort(port);

		String host = environment.getProperty("http-server.http.host");
		if (StringUtils.isEmptyOrWhitespaceOnly(host)) {
			logger.warn("The http-server.http.host is not found is config file, please check it!");
			try {
				host = WebUtils.bind().getHostAddress();
				logger.warn("The system should set host as {}", host);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		current.setHost(host);

		String node = environment.getProperty("node.id");
		if (StringUtils.isEmptyOrWhitespaceOnly(node)) {
			logger.warn("The node.id is not found is config file, please check it!");
			try {
				node = InetAddress.getLocalHost().getHostName() + "-" + current.getPort();
			} catch (UnknownHostException e) {
				node = UUID.randomUUID().toString() + "-" + current.getPort();
			}
		}
		logger.info("The streamer system set node as {}", node);
		current.setNode(node);

		return current;
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		StreamerEnvironment env = context.getBean(StreamerEnvironment.class);
		try {
			container.setPort(env.getPort());
			container.setAddress(InetAddress.getByName(env.getHost()));
		} catch (IOException e) {
			throw new StreammerException("Can not bind config host:" + env.getHost() + " and port:" + env.getPort(), e);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(WorkerApplication.class, args);
	}
}
