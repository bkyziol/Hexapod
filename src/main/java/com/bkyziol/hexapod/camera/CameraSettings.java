package com.bkyziol.hexapod.camera;

import com.bkyziol.hexapod.Main;

public class CameraSettings {
	private boolean cameraEnabled = false;
	private boolean faceDetectionEnabled = false;
	private VideQuality videoQuality = VideQuality.QUALITY_3;
	private int videoFPS = 4;

	public boolean isCameraEnabled() {
		return cameraEnabled;
	}

	public void setCameraEnabled(boolean enabled) {
		this.cameraEnabled = enabled;
	}

	public boolean isFaceDetectionEnabled() {
		return faceDetectionEnabled;
	}

	public void setFaceDetectionEnabled(boolean faceDetectionEnabled) {
		this.faceDetectionEnabled = faceDetectionEnabled;
	}

	public int getFrameHeight() {
		return videoQuality.getHeight();
	}

	public int getFrameWidth() {
		return videoQuality.getWidth();
	}

	public void setVideoQuality(int quality) {
		this.videoQuality = VideQuality.getQuality(quality);
	}

	public int getVideoFPS() {
		return videoFPS;
	}

	public void setVideoFPS(int videoFPS) {
		if (this.videoFPS != videoFPS) {
			System.out.println("new frame rate: " + videoFPS);
			this.videoFPS = videoFPS;
			Main.changeFrameRate(videoFPS);
		}
	}
}
