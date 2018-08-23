package com.bkyziol.hexapod.movement;

import com.bkyziol.hexapod.connection.HexapodConnection;
import com.bkyziol.hexapod.connection.TopicName;

public class Status {

	private static HexapodConnection connection;
	private static HeadMovementType headMovementType;
	private static BodyMovementType bodyMovementType;
	private static boolean sleepMode = true;
	private static boolean lookAroundMode = false;
	private static long lastStatusTimestamp;
	private static long lastMoveTimestamp;
	private static long lastFaceDetectedTimestamp;
	private static long nextRandomMoveTimestamp;
	private static int bodySpeed;
	private static int headSpeed;
	private static int strideLength;

	public static void sendStatusMessage() {
		if (connection == null || !connection.isConnected()) {
			return;
		}
		String status;
		if (sleepMode) {
			status = "CROUCHING";
		} else {
			status = "STANDING";
		}
		byte[] payload = status.getBytes();
		Thread responseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				lastStatusTimestamp = System.currentTimeMillis();
				connection.sendMessage(TopicName.STATUS, payload);
			}
		});
		responseThread.start();
	}

	public static void setConnection(HexapodConnection connection) {
		Status.connection = connection;
	}

	public static HeadMovementType getHeadMovementType() {
		return headMovementType;
	}

	public static void setHeadMovementType(HeadMovementType headMovementType) {
		Status.headMovementType = headMovementType;
	}

	public static BodyMovementType getBodyMovementType() {
		return bodyMovementType;
	}

	public static void setBodyMovementType(BodyMovementType bodyMovementType) {
		Status.bodyMovementType = bodyMovementType;
	}

	public static boolean isSleepMode() {
		return sleepMode;
	}

	public static void setSleepMode(boolean sleepMode) {
		Status.sleepMode = sleepMode;
		sendStatusMessage();
	}

	public static boolean isLookAroundMode() {
		return lookAroundMode;
	}

	public static void setLookAroundMode(boolean lookAroundMode) {
		Status.lookAroundMode = lookAroundMode;
	}

	public static long getLastStatusTimestamp() {
		return lastStatusTimestamp;
	}

	public static long getLastMoveTimestamp() {
		return lastMoveTimestamp;
	}

	public static void setLastMoveTimestamp(long lastMoveTimestamp) {
		Status.lastMoveTimestamp = lastMoveTimestamp;
	}

	public static long getLastFaceDetectedTimestamp() {
		return lastFaceDetectedTimestamp;
	}

	public static void setLastFaceDetectedTimestamp(long lastFaceDetectedTimestamp) {
		Status.lastFaceDetectedTimestamp = lastFaceDetectedTimestamp;
	}

	public static long getNextRandomMoveTimestamp() {
		return nextRandomMoveTimestamp;
	}

	public static void setNextRandomMoveTimestamp(long nextRandomMoveTimestamp) {
		Status.nextRandomMoveTimestamp = nextRandomMoveTimestamp;
	}

	public static int getBodySpeed() {
		return bodySpeed;
	}

	public static void setBodySpeed(int bodySpeed) {
		Status.bodySpeed = bodySpeed;
	}

	public static int getHeadSpeed() {
		return headSpeed;
	}

	public static void setHeadSpeed(int headSpeed) {
		Status.headSpeed = headSpeed;
	}

	public static int getStrideLength() {
		return strideLength;
	}

	public static void setStrideLength(int strideLength) {
		Status.strideLength = strideLength;
	}
}
