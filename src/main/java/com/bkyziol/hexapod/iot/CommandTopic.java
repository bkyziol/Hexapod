package com.bkyziol.hexapod.iot;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.bkyziol.hexapod.camera.CameraSettings;
import com.bkyziol.hexapod.connection.HexapodConnection;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.model.CommandMessage;
import com.bkyziol.hexapod.movement.BodyMovement;
import com.bkyziol.hexapod.movement.HeadMovementType;
import com.bkyziol.hexapod.movement.Status;
import com.google.gson.Gson;

public class CommandTopic extends AWSIotTopic {

	private HexapodConnection connection;
	private long lastMessageTimestamp = 0;

	public CommandTopic() {
		super(TopicName.COMMAND.getName(), AWSIotQos.QOS0);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		Gson gson = new Gson();
		CommandMessage commandMessagePayload = gson.fromJson(message.getStringPayload(), CommandMessage.class);

		Status.setHeadSpeed(commandMessagePayload.getHeadSpeed());
		Status.setBodySpeed(commandMessagePayload.getBodySpeed());

		CameraSettings.setCameraEnabled(commandMessagePayload.isCameraEnabled());
		CameraSettings.setFaceDetectionEnabled(commandMessagePayload.isFaceDetectionEnabled());
		CameraSettings.setVideoFPS(commandMessagePayload.getVideoFPS());
		CameraSettings.setVideoQuality(commandMessagePayload.getVideoQuality());

		String bodyMovement = commandMessagePayload.getBodyMovement();
		String headMovement = commandMessagePayload.getHeadMovement();
		if (!bodyMovement.equals("STAND_BY") || !headMovement.equals("STAND_BY")) {
			lastMessageTimestamp = System.currentTimeMillis();
		}

		String responseString = null;
		if (commandMessagePayload.isStatusReportNeeded()) {
			Status.setLastStatusTimestamp(System.currentTimeMillis());
			responseString = "OK";
		}

		switch (bodyMovement) {
		case "RISE":
			if (BodyMovement.standUp()) {
				responseString = "STANDING";
			}
			break;
		case "CROUCH":
			if (BodyMovement.lieDown()) {
				responseString = "CROUCHING";
			}
			break;
		}

		switch (headMovement) {
		case "LEFT":
			Status.setHeadMovementType(HeadMovementType.LEFT);
			break;
		case "RIGHT":
			Status.setHeadMovementType(HeadMovementType.RIGHT);
			break;
		case "CENTER":
			Status.setHeadMovementType(HeadMovementType.CENTER);
			break;
		case "UP":
			Status.setHeadMovementType(HeadMovementType.UP);
			break;
		case "DOWN":
			Status.setHeadMovementType(HeadMovementType.DOWN);
			break;
		default:
			Status.setHeadMovementType(HeadMovementType.STAND_BY);
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

//		System.out.println("------------------------------------------");
//		System.out.println("BODY: " + bodyMovement);
//		System.out.println("HEAD: " + headMovement );
//		System.out.println("CAMERA: " + commandMessagePayload.isCameraEnabled());
//		System.out.println("FACE DETECTION: " + commandMessagePayload.isFaceDetectionEnabled());
	}

	public long getLastMessageTimestamp() {
		return lastMessageTimestamp;
	}

	public void setConnection(HexapodConnection connection) {
		this.connection = connection;
	}
}