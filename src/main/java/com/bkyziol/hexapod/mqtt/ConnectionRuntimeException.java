package com.bkyziol.hexapod.mqtt;

public class ConnectionRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectionRuntimeException(String message, Exception cause) {
		super(message, cause);
	}

	public ConnectionRuntimeException(String message) {
		super(message);
	}
}
