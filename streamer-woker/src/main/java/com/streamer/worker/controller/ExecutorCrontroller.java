package com.streamer.worker.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamer.core.parser.SqlParser;
import com.streamer.core.parser.SqlTree;
import com.streamer.service.broker.KafkaStreamBroker;
import com.streamer.service.core.ServiceConstant;
import com.streamer.service.core.StreamerEnvironment;
import com.streamer.service.job.KafkaStreamJob;
import com.streamer.worker.http.ApiResponse;
import com.streamer.worker.http.JobSatus;
import com.streamer.worker.log.LogViewer;
import com.streamer.worker.service.AppService;

@RestController
public class ExecutorCrontroller {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private AppService appService;

	@Resource
	private StreamerEnvironment streamerEnvironment;

	@Resource
	private ApplicationContext context;

	@RequestMapping(value = "/executor/run/{name}")
	public ApiResponse run_job(@PathVariable String name, HttpServletResponse hresponse) {
		ApiResponse response = new ApiResponse();

		if (streamerEnvironment.isMaster()) {
			response.setData(JobSatus.NOT_FOUND.name());
			response.setStatus(JobSatus.NOT_FOUND.getStatus());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		List<Map<String, Object>> list = appService.findJobByName(name);

		if (list.isEmpty()) {
			response.setData(JobSatus.NOT_FOUND.name());
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		String cnode = String.valueOf(list.get(0).get("node"));
		if (StringUtils.isEmpty(cnode)) {
			response.setData("job run node is Empty");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		if (!cnode.equals(streamerEnvironment.getNode())) {
			response.setData("job should run node " + streamerEnvironment.getNode() + ", but current is " + cnode);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		int status = Integer.valueOf(list.get(0).get("status").toString());

		if (status > 0) {
			response.setData("任务" + name + "正在运行中");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		String token = UUID.randomUUID().toString();

		try {
			SqlTree sqlTree = SqlParser.parseSql(name, String.valueOf(list.get(0).get("sql")));
			logger.info("Start job " + ServiceConstant.FORMAT_JOB_NAME(name) + " use token : {} ", token);
			appService.start(streamerEnvironment.getNode(), name, token);

			// 初始化线程池
			ExecutorService service = new ThreadPoolExecutor(0, 1, 1, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(1));

			KafkaStreamBroker broker = new KafkaStreamBroker(new KafkaStreamJob(sqlTree), context);

			// 启动任务
			service.execute(broker);
			return response;

		} catch (Exception e) {
			appService.stop(token, streamerEnvironment.getNode(), name);
			logger.error(ServiceConstant.FORMAT_JOB_NAME(name) + e.getMessage(), e);
			response.setData(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

	}

	@RequestMapping("/executor/stop/{name}")
	public ApiResponse stop_job(@PathVariable String name, HttpServletResponse hresponse) {

		ApiResponse response = new ApiResponse();

		String node = streamerEnvironment.getNode();

		if (streamerEnvironment.isMaster()) {
			response.setData(JobSatus.NOT_FOUND.name());
			response.setStatus(JobSatus.NOT_FOUND.getStatus());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		List<Map<String, Object>> list = appService.findJobByName(name);

		if (list.isEmpty()) {
			response.setData(JobSatus.NOT_FOUND.name());
			response.setStatus(JobSatus.NOT_FOUND.getStatus());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		try {
			String token = String.valueOf(list.get(0).get("token"));
			logger.info("Kill " + ServiceConstant.FORMAT_JOB_NAME(name) + " use token : {} ", token);
			appService.stop(token, node, name);
			return response;

		} catch (Exception e) {
			logger.error(ServiceConstant.FORMAT_JOB_NAME(name) + e.getMessage(), e);
			response.setData(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}
	}

	@RequestMapping("/executor/log/{name}/{line}")
	public ApiResponse job_log(@PathVariable String name, @PathVariable long line, HttpServletResponse hresponse) {

		ApiResponse response = new ApiResponse();

		if (streamerEnvironment.isMaster()) {
			response.setData(JobSatus.NOT_FOUND.name());
			response.setStatus(JobSatus.NOT_FOUND.getStatus());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}
		try {
			if (name.equals("_")) {
				response.setData(LogViewer.readLog(name, line));
				return response;
			} else {
				List<Map<String, Object>> list = appService.findJobByName(name);
				if (list.isEmpty()) {
					response.setData(JobSatus.NOT_FOUND.name());
					response.setStatus(JobSatus.NOT_FOUND.getStatus());
					hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
					return response;
				}
				response.setData(LogViewer.readLog(name, line));
				return response;
			}
		} catch (Exception e) {
			logger.error(ServiceConstant.FORMAT_JOB_NAME(name) + e.getMessage(), e);
			response.setData(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			hresponse.setStatus(HttpStatus.BAD_REQUEST.value());
			return response;
		}
	}
}
