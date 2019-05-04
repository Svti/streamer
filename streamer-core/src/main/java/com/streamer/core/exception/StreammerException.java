package com.streamer.core.exception;

public class StreammerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public StreammerException(String message) {
		super(message);
	}

	public StreammerException(String message, Throwable cause) {
		super(message, cause);
	}
}
