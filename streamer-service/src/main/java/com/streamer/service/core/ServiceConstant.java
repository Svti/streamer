package com.streamer.service.core;

public class ServiceConstant {

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

}
