package com.bkyziol.hexapod.movement;

public class Status {

	private static HeadMovementType headMovementType;
	private static BodyMovementType bodyMovementType;
	private static boolean sleepMode;
	private static long lastStatusTimestamp;
	private static int bodySpeed;
	private static int headSpeed;

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
	}

	public static long getLastStatusTimestamp() {
		return lastStatusTimestamp;
	}

	public static void setLastStatusTimestamp(long lastPingTimestamp) {
		Status.lastStatusTimestamp = lastPingTimestamp;
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

}
