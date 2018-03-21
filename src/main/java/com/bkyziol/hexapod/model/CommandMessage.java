package com.bkyziol.hexapod.model;

public class CommandMessage {

	private String hexapodMovement;
	private String cameraMovement;
	private boolean sleepMode;
	private boolean fastMode;
	private long timestamp;

	public String getHexapodMovement() {
		return hexapodMovement;
	}

	public void setHexapodMovement(String hexapodMovement) {
		this.hexapodMovement = hexapodMovement;
	}

	public String getCameraMovement() {
		return cameraMovement;
	}

	public void setCameraMovement(String cameraMovement) {
		this.cameraMovement = cameraMovement;
	}

	public boolean isSleepMode() {
		return sleepMode;
	}

	public void setSleepMode(boolean sleepMode) {
		this.sleepMode = sleepMode;
	}

	public boolean isFastMode() {
		return fastMode;
	}

	public void setFastMode(boolean fastMode) {
		this.fastMode = fastMode;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}