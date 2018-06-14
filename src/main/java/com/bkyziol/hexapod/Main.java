package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.CameraSettings;
import com.bkyziol.hexapod.camera.FaceTracking;
import com.bkyziol.hexapod.camera.HexapodCamera;
import com.bkyziol.hexapod.connection.HexapodConnection;
import com.bkyziol.hexapod.connection.HexapodConnection.HexapodConnectionBuilder;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.iot.CommandTopic;
import com.bkyziol.hexapod.iot.ServiceTopic;
import com.bkyziol.hexapod.movement.HeadMovement;
import com.bkyziol.hexapod.movement.HeadMovementType;
import com.bkyziol.hexapod.movement.ServoController;
import com.bkyziol.hexapod.movement.Status;
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
	private static CommandTopic commandTopic;
	private static ServiceTopic serviceTopic;
	private static FaceTracking faceDetection;

	static {
		Logger.getLogger("").getHandlers()[0].setLevel(Level.SEVERE);
	}

	public static void main(String[] args) throws InterruptedException {
		camera = new HexapodCamera(
				Constants.OPENCV_LIB_FILE,
				Constants.HAARCASCADES_PATH);
		camera.startCapture();
		faceDetection = new FaceTracking(camera);
		HexapodConnectionBuilder connectionBuilder = new HexapodConnectionBuilder(
				Constants.CERTIFICATE_FILE,
				Constants.PRIVATE_KEY_FILE,
				Constants.CLIENT_ENDPOINT,
				UUID.randomUUID().toString());
		commandTopic = new CommandTopic();
		serviceTopic = new ServiceTopic();
		connectionBuilder.addTopic(commandTopic);
		connectionBuilder.addTopic(serviceTopic);
		connection = connectionBuilder.build();
		connection.connect();
		commandTopic.setConnection(connection);
		startCameraTimer();
		startMovementTimer();
	}

	private static void startMovementTimer() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		Runnable timer = new Runnable() {
			@Override
			public void run() {
//				System.out.print(".");
//				long currentTimestamp = System.currentTimeMillis();
//				long lastMessageTimestamp = commandTopic.getLastMessageTimestamp();
//				if (lastMessageTimestamp + 5000 < currentTimestamp && CameraSettings.isFaceDetectionEnabled()) {
//					Status.setHeadMovementType(HeadMovementType.TRACKING);
//					faceDetection.lookAt();
//				} else if (lastMessageTimestamp + 3000 < currentTimestamp) {
//					Status.setHeadMovementType(HeadMovementType.STAND_BY);
//				} else {
////					HeadMovement.makeMove();
////					BodyMovement.makeMove();
//				}
				ServoController.getMovingState();
			}
		};
		executor.scheduleAtFixedRate(timer, 0, 50, TimeUnit.MILLISECONDS);
//		executor.scheduleAtFixedRate(timer, 0, 4000, TimeUnit.MILLISECONDS);
	}

	private static void startCameraTimer() {
		startCameraTimer(250);
	}
	
	private static void startCameraTimer(int delay) {
		sendFramesTimer = Executors.newSingleThreadScheduledExecutor();
		Runnable framesSender = new Runnable() {
			@Override
			public void run() {
				if (CameraSettings.isCameraEnabled() && Status.getLastStatusTimestamp() + 5000 > System.currentTimeMillis()) {
					byte[] payload = camera.getCompressedFrame();
					if (payload != null) {
						connection.sendMessage(TopicName.CAMERA, payload);
					}
				}
			}
		};
		sendFramesTimer.scheduleAtFixedRate(framesSender, 0, delay, TimeUnit.MILLISECONDS);
	}

	public static void changeFrameRate(int fps) {
		int delay = 1000 / fps;
		sendFramesTimer.shutdown();
		startCameraTimer(delay);
	}
}
