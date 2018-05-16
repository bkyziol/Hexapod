package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.CameraRuntimeException;
import com.bkyziol.hexapod.camera.CameraSettings;
import com.bkyziol.hexapod.camera.HexapodCamera;
import com.bkyziol.hexapod.connection.HexapodConnection;
import com.bkyziol.hexapod.connection.HexapodConnection.HexapodConnectionBuilder;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.iot.CommandTopic;
import com.bkyziol.hexapod.utils.Constants;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

	private static ScheduledExecutorService sendFramesTimer;
	private static HexapodConnection connection;
	private static HexapodCamera camera;

	static {
		Logger.getLogger("").getHandlers()[0].setLevel(Level.SEVERE);
	}

	public static void main(String[] args) throws InterruptedException {
		camera = new HexapodCamera(
				Constants.OPENCV_LIB_FILE,
				Constants.HAARCASCADES_PATH);
		camera.startCapture();

		HexapodConnectionBuilder connectionBuilder = new HexapodConnectionBuilder(
				Constants.CERTIFICATE_FILE,
				Constants.PRIVATE_KEY_FILE,
				Constants.CLIENT_ENDPOINT,
				UUID.randomUUID().toString());
		CommandTopic commandTopic = new CommandTopic();
		connectionBuilder.addTopic(commandTopic);

		connection = connectionBuilder.build();
		connection.connect();
		commandTopic.setConnection(connection);
		startSendingFrames(250);
	}

	private static void startSendingFrames(int delay) {
		sendFramesTimer = Executors.newSingleThreadScheduledExecutor();
		Runnable framesSender = new Runnable() {
			@Override
			public void run() {
				CameraSettings cameraSettings = Status.getCameraSettings();
				try {
					if (cameraSettings.isCameraEnabled() && Status.getLastStatusTimestamp() + 5000 > System.currentTimeMillis()) {
						byte[] payload = camera.getCompressedFrame();
						System.out.println("frame send");
						connection.sendMessage(TopicName.CAMERA, payload);
					}
				} catch (CameraRuntimeException e) {
					System.out.println("Capture frame exception");
				}
			}
		};
		sendFramesTimer.scheduleAtFixedRate(framesSender, 0, delay, TimeUnit.MILLISECONDS);
	}
	
	public static void changeFrameRate(int fps) {
		int delay = 1000 / fps;
		sendFramesTimer.shutdown();
		startSendingFrames(delay);
	}

}
