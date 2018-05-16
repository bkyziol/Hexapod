package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.CameraSettings;

public class Status {

	private static boolean sleepMode;
	private static long lastStatusTimestamp;
	private static CameraSettings cameraSettings = new CameraSettings();;

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

	public static CameraSettings getCameraSettings() {
		return cameraSettings;
	}
}
