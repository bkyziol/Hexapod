package com.bkyziol.hexapod.movement;

import static com.bkyziol.hexapod.movement.ServoControllerValues.HEAD_HORIZONTAL;
import static com.bkyziol.hexapod.movement.ServoControllerValues.HEAD_VERTICAL;

import com.bkyziol.hexapod.Status;
import com.bkyziol.hexapod.camera.CameraSettings;
import com.bkyziol.hexapod.iot.CommandTopic;

public class HeadMovement implements Runnable {

	private static HeadMovementType movement = HeadMovementType.STAND_BY;
	private static int speed = 50;
	private static final CameraSettings cameraSettings = Status.getCameraSettings();

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(20);
				long lastMessageTimestamp = CommandTopic.getLastMessageTimestamp();
				if (lastMessageTimestamp + 2000 > System.currentTimeMillis()) {
					switch (movement) {
					case CENTER:
						HEAD_HORIZONTAL.setPosition(HEAD_HORIZONTAL.getNeutral());
						HEAD_VERTICAL.setPosition(HEAD_VERTICAL.getNeutral());
						break;
					case LEFT:
						HEAD_HORIZONTAL.decreaseAngle(speed);
						break;
					case RIGHT:
						HEAD_HORIZONTAL.increaseAngle(speed);
						break;
					case UP:
						HEAD_VERTICAL.increaseAngle(speed);
						break;
					case DOWN:
						HEAD_VERTICAL.decreaseAngle(speed);
						break;
					default:
						break;
					}
				} else if (lastMessageTimestamp + 5000 < System.currentTimeMillis()) {
					if (cameraSettings.isFaceDetectionEnabled()) {
						
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static HeadMovementType getMovement() {
		return movement;
	}

	public static void setMovement(HeadMovementType movement) {
		HeadMovement.movement = movement;
	}

	public static int getSpeed() {
		return speed;
	}

	public static void setSpeed(int speed) {
		HeadMovement.speed = speed;
	}
}
