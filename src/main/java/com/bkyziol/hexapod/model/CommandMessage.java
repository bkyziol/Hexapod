package com.bkyziol.hexapod.model;

public class CommandMessage {

	private long commandTimestamp;
	private String hexapodMovement;
	private String cameraMovement;
	private boolean statusReportNeeded;
	private boolean sleepMode;
	private int hexapodSpeed;
	private int strideLength;
	private int cameraSpeed;
	private boolean cameraEnabled;
	private boolean faceDetectionEnabled;
	private int videoQuality;
	private int videoFPS;

	public long getCommandTimestamp() {
		return commandTimestamp;
	}

	public void setCommandTimestamp(long commandTimestamp) {
		this.commandTimestamp = commandTimestamp;
	}

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

	public boolean isStatusReportNeeded() {
		return statusReportNeeded;
	}

	public void setStatusReportNeeded(boolean statusReportNeeded) {
		this.statusReportNeeded = statusReportNeeded;
	}

	public boolean isSleepMode() {
		return sleepMode;
	}

	public void setSleepMode(boolean sleepMode) {
		this.sleepMode = sleepMode;
	}

	public int getHexapodSpeed() {
		return hexapodSpeed;
	}

	public void setHexapodSpeed(int hexapodSpeed) {
		this.hexapodSpeed = hexapodSpeed;
	}

	public int getStrideLength() {
		return strideLength;
	}

	public void setStrideLength(int strideLength) {
		this.strideLength = strideLength;
	}

	public int getCameraSpeed() {
		return cameraSpeed;
	}

	public void setCameraSpeed(int cameraSpeed) {
		this.cameraSpeed = cameraSpeed;
	}
	
	public boolean isCameraEnabled() {
		return cameraEnabled;
	}
	
	public void setCameraEnabled(boolean cameraEnabled) {
		this.cameraEnabled = cameraEnabled;
	}

	public boolean isFaceDetectionEnabled() {
		return faceDetectionEnabled;
	}

	public void setFaceDetectionEnabled(boolean faceDetectionEnabled) {
		this.faceDetectionEnabled = faceDetectionEnabled;
	}

	public int getVideoQuality() {
		return videoQuality;
	}

	public void setVideoQuality(int videoQuality) {
		this.videoQuality = videoQuality;
	}

	public int getVideoFPS() {
		return videoFPS;
	}

	public void setVideoFPS(int videoFPS) {
		this.videoFPS = videoFPS;
	}

}