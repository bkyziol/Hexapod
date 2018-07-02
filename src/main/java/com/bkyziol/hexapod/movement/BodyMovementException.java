package com.bkyziol.hexapod.movement;

public class BodyMovementException extends Exception {

	private static final long serialVersionUID = 1L;
	public static final String OUT_OF_REACH = "Unable to reach position";

	public BodyMovementException(String message, Throwable cause) {
		super(message, cause);
	}

	public BodyMovementException(String message) {
		super(message);
	}
}
