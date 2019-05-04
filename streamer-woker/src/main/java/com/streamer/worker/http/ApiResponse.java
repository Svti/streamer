package com.streamer.worker.http;

import org.springframework.http.HttpStatus;

public class ApiResponse {

	public int status = HttpStatus.OK.value();

	public long timestamp = System.currentTimeMillis();

	public Object data = HttpStatus.OK.name();

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
