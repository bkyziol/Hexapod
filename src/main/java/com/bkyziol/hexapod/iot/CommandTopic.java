package com.bkyziol.hexapod.iot;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.bkyziol.hexapod.camera.CameraSettings;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.model.CommandMessage;
import com.bkyziol.hexapod.movement.BodyMovementType;
import com.bkyziol.hexapod.movement.HeadMovementType;
import com.bkyziol.hexapod.movement.Status;
import com.google.gson.Gson;

public class CommandTopic extends AWSIotTopic {

	public CommandTopic() {
		super(TopicName.COMMAND.getName(), AWSIotQos.QOS0);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		Gson gson = new Gson();
		CommandMessage commandMessagePayload = gson.fromJson(message.getStringPayload(), CommandMessage.class);

		Status.setHeadSpeed(commandMessagePayload.getHeadSpeed());
		Status.setBodySpeed(commandMessagePayload.getBodySpeed());
		String bodyMovement = commandMessagePayload.getBodyMovement();
		String headMovement = commandMessagePayload.getHeadMovement();
		if (!bodyMovement.equals("STAND_BY") || !headMovement.equals("STAND_BY")) {
			Status.setLastMoveTimestamp(System.currentTimeMillis());
		}

		Status.setBodyMovementType(BodyMovementType.valueOf(bodyMovement));
		Status.setHeadMovementType(HeadMovementType.valueOf(headMovement));

		CameraSettings.setCameraEnabled(commandMessagePayload.isCameraEnabled());
		CameraSettings.setFaceDetectionEnabled(commandMessagePayload.isFaceDetectionEnabled());
		CameraSettings.setVideoFPS(commandMessagePayload.getVideoFPS());
		CameraSettings.setVideoQuality(commandMessagePayload.getVideoQuality());

		if (commandMessagePayload.isStatusReportNeeded()) {
			Status.sendStatusMessage();
		}

		System.out.println("------------------------------------------");
		System.out.println("BODY: " + bodyMovement);
		System.out.println("HEAD: " + headMovement );
		System.out.println("CAMERA: " + commandMessagePayload.isCameraEnabled());
		System.out.println("FACE DETECTION: " + commandMessagePayload.isFaceDetectionEnabled());
	}
}