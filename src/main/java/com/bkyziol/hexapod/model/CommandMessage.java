package com.bkyziol.hexapod.model;

public class CommandMessage {

	private long commandTimestamp;
	private String bodyMovement;
	private String headMovement;
	private boolean statusReportNeeded;
	private int bodySpeed;
	private int strideLength;
	private int headSpeed;
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

	public String getBodyMovement() {
		return bodyMovement;
	}

	public void setBodyMovement(String bodyMovement) {
		this.bodyMovement = bodyMovement;
	}

	public String getHeadMovement() {
		return headMovement;
	}

	public void setHeadMovement(String headMovement) {
		this.headMovement = headMovement;
	}

	public boolean isStatusReportNeeded() {
		return statusReportNeeded;
	}

	public void setStatusReportNeeded(boolean statusReportNeeded) {
		this.statusReportNeeded = statusReportNeeded;
	}

	public int getBodySpeed() {
		return bodySpeed;
	}

	public void setBodySpeed(int bodySpeed) {
		this.bodySpeed = bodySpeed;
	}

	public int getStrideLength() {
		return strideLength;
	}

	public void setStrideLength(int strideLength) {
		this.strideLength = strideLength;
	}

	public int getHeadSpeed() {
		return headSpeed;
	}

	public void setHeadSpeed(int headSpeed) {
		this.headSpeed = headSpeed;
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