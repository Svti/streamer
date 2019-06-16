package com.streamer.service.core;

public class StreamerConstant {

	public static final int CONN_SIZE = 20;

	public static final int INIT_SIZE = 5;

	// 远程读取日志文件名
	public static final String LOG_NAME = System.getProperty("app.home") + "/logs/" + "streamer.log";

	// 搜索最多多少行
	public static final int MAX_LINE = 10000;

	// ms
	public static final int VALIDATE_TIME = 1000;

	public static final String FORMAT_JOB_NAME(String name) {
		return " JOB:[" + name + "] ";
	}

	public static final int PAGE_SIZE = 10;

	public static final String PAGEVIEW = "pageView";

	public static final String SUCCESS = "success";

	public static final String ERROR = "danger";

	public static final String STATUS = "status";

	public static final String MESSAGE = "msg";

	public static final String NULL_JOB = "_";

	public static final int TIMEOUT = 3;

	public static final long LOG_LINE = 100;

	public static final String MASTER_HOST = "127.0.0.1";

	public static final String MASTER_NAME = "master";

	public static final int RUNNING_JOB = 1;

	public static final int STOPED_JOB = 0;
}
