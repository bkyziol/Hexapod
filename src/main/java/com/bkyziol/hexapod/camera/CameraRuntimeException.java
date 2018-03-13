package com.bkyziol.hexapod.camera;

public class CameraRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public CameraRuntimeException(String message, Exception cause) {
		super(message, cause);
	}

	public CameraRuntimeException(String message) {
		super(message);
	}
}
