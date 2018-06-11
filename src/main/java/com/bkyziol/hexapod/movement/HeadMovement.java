package com.bkyziol.hexapod.movement;

import static com.bkyziol.hexapod.movement.ServoControllerValues.HEAD_HORIZONTAL;
import static com.bkyziol.hexapod.movement.ServoControllerValues.HEAD_VERTICAL;

public class HeadMovement implements Runnable {

	private static HeadMovementType movement = HeadMovementType.STAND_BY;
	private static int speed = 50;


	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(20);
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
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setMovement(HeadMovementType movement) {
		HeadMovement.movement = movement;
	}

	public static void setSpeed(int speed) {
		HeadMovement.speed = speed;
	}
}
