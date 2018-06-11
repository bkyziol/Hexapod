package com.bkyziol.hexapod.camera;

import com.bkyziol.hexapod.Main;

public class CameraSettings {
	private static boolean cameraEnabled = false;
	private static boolean faceDetectionEnabled = false;
	private static VideQuality videoQuality = VideQuality.QUALITY_3;
	private static int videoFPS = 4;

	public static boolean isCameraEnabled() {
		return cameraEnabled;
	}

	public static void setCameraEnabled(boolean enabled) {
		CameraSettings.cameraEnabled = enabled;
	}

	public static boolean isFaceDetectionEnabled() {
		return faceDetectionEnabled;
	}

	public static void setFaceDetectionEnabled(boolean faceDetectionEnabled) {
		CameraSettings.faceDetectionEnabled = faceDetectionEnabled;
	}

	public static int getFrameHeight() {
		return videoQuality.getHeight();
	}

	public static int getFrameWidth() {
		return videoQuality.getWidth();
	}

	public static void setVideoQuality(int quality) {
		CameraSettings.videoQuality = VideQuality.getQuality(quality);
	}

	public static int getVideoFPS() {
		return videoFPS;
	}

	public static void setVideoFPS(int videoFPS) {
		if (CameraSettings.videoFPS != videoFPS) {
			System.out.println("new frame rate: " + videoFPS);
			CameraSettings.videoFPS = videoFPS;
			Main.changeFrameRate(videoFPS);
		}
	}
}
