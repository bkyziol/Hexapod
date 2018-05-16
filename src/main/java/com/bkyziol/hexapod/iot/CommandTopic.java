package com.bkyziol.hexapod.iot;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.bkyziol.hexapod.Status;
import com.bkyziol.hexapod.camera.CameraSettings;
import com.bkyziol.hexapod.connection.HexapodConnection;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.model.CommandMessage;
import com.google.gson.Gson;

public class CommandTopic extends AWSIotTopic {

	private HexapodConnection connection;
	private long lastMessageTimestamp = 0;

	public CommandTopic() {
		super(TopicName.COMMAND.getName(), AWSIotQos.QOS0);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		lastMessageTimestamp = System.currentTimeMillis();
		Gson gson = new Gson();
		CommandMessage commandMessagePayload = gson.fromJson(message.getStringPayload(), CommandMessage.class);
		String hexapodMovement = commandMessagePayload.getHexapodMovement();
		String responseString = null;

		if (commandMessagePayload.isStatusReportNeeded()) {
			Status.setLastStatusTimestamp(System.currentTimeMillis());
			responseString = "OK";
		}

		switch (hexapodMovement) {
		case "RISE":
			responseString = "STANDING";
			break;
		case "CROUCH":
			responseString = "CROUCHING";
			break;
		}

		if (responseString != null) {
			byte[] payload = new String(responseString).getBytes();
			Thread responseThread = new Thread(new Runnable() {
				@Override
				public void run() {
					connection.sendMessage(TopicName.STATUS, payload);
				}
			});
			responseThread.start();
		}
		
		System.out.println("------------------------------------------");
		System.out.println(hexapodMovement);
		System.out.println(commandMessagePayload.getCameraMovement());
		// System.out.println(commandMessagePayload.isSleepMode());
		// System.out.println(commandMessagePayload.getCameraSpeed());
		// System.out.println(commandMessagePayload.getHexapodSpeed());
		// System.out.println(commandMessagePayload.getStrideLength());
		 System.out.println(commandMessagePayload.isCameraEnabled());
		// System.out.println(commandMessagePayload.getVideoFPS());
		// System.out.println(commandMessagePayload.getVideoQuality());
		// System.out.println("------------------------------------------");
		CameraSettings cameraSettings = Status.getCameraSettings();
		cameraSettings.setCameraEnabled(commandMessagePayload.isCameraEnabled());
		cameraSettings.setFaceDetectionEnabled(commandMessagePayload.isFaceDetectionEnabled());
		cameraSettings.setVideoFPS(commandMessagePayload.getVideoFPS());
		cameraSettings.setVideoQuality(commandMessagePayload.getVideoQuality());
	}

	public long getLastMessageTimestamp() {
		return lastMessageTimestamp;
	}

	public void setConnection(HexapodConnection connection) {
		this.connection = connection;
	}
}