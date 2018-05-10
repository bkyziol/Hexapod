package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.CameraRuntimeException;
import com.bkyziol.hexapod.camera.HexapodCamera;
import com.bkyziol.hexapod.connection.HexapodConnection;
import com.bkyziol.hexapod.connection.HexapodConnection.HexapodConnectionBuilder;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.iot.CameraTopic;
import com.bkyziol.hexapod.iot.CommandTopic;
import com.bkyziol.hexapod.utils.Constants;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

	private static HexapodConnection connection;
	private static HexapodCamera camera;
	private static CommandTopic commandTopic = new CommandTopic();
	private static CameraTopic cameraTopic = new CameraTopic();

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
		connectionBuilder.addTopic(cameraTopic);
		connectionBuilder.addTopic(commandTopic);

		connection = connectionBuilder.build();
		connection.connect();
		
		startSendingFrames();
	}

	private static void startSendingFrames() {
		Runnable framesSender = new Runnable() {
			@Override
			public void run() {
				try {
					byte[] payload = camera.getCompressedFrame();
					connection.sendMessage(TopicName.CAMERA, payload);
				} catch (CameraRuntimeException e) {
					System.out.println("Capture frame exception");
				}
			}
		};
		ScheduledExecutorService sendFramesTimer = Executors.newSingleThreadScheduledExecutor();
		sendFramesTimer.scheduleAtFixedRate(framesSender, 0, 250, TimeUnit.MILLISECONDS);
	}

}
