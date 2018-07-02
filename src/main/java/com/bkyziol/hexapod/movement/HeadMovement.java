package com.bkyziol.hexapod.movement;

public class HeadMovement {

	private static final HeadServo verticalServo = Head.getVerticalServo();
	private static final HeadServo horizontalServo = Head.getHorizontalServo();

	public static void makeMove() throws InterruptedException {
		int speed = Status.getHeadSpeed();
		switch (Status.getHeadMovementType()) {
		case CENTER:
			horizontalServo.setValue(horizontalServo.getCenter());
			verticalServo.setValue(horizontalServo.getCenter());
			break;
		case LEFT:
			horizontalServo.decreaseAngle(speed);
			break;
		case RIGHT:
			horizontalServo.increaseAngle(speed);
			break;
		case UP:
			verticalServo.decreaseAngle(speed);
			break;
		case DOWN:
			verticalServo.increaseAngle(speed);
			break;
		default:
			break;
		}
	}
}
