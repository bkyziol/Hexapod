package com.bkyziol.hexapod.movement;

import static com.bkyziol.hexapod.movement.Body.*;

public class BodyMovement {

	private static final int SUSPENSION_HEIGHT = 80;
	private static final int STEP_HEIGHT = 40;
	private static final int OUTER_LEG_STARTING_POSITION_X = 60;
	private static final int OUTER_LEG_STARTING_POSITION_Y = 60;
	private static final int INNER_LEG_STARTING_POSITION_X = 90;
	private static final int INNER_LEG_STARTING_POSITION_Y = 0;

	private static boolean currentlyInMotion = false;

	public static void makeMove() {
		if (!currentlyInMotion) {
			switch (Status.getBodyMovementType()) {
			default:
				break;
			}
		}
	}

	public static boolean standUp() {
		if (currentlyInMotion) {
			return false;
		}
		System.out.println("standUp() - start");
		currentlyInMotion = true;
		ServoController.setBodySpeed(200);
		for (int i = 20; i <= SUSPENSION_HEIGHT; i = i + 10) {
			legLeftFront.setFootPosition(OUTER_LEG_STARTING_POSITION_X, OUTER_LEG_STARTING_POSITION_Y, i);
			legLeftMiddle.setFootPosition(80, 45, i);
			legLeftRear.setFootPosition(OUTER_LEG_STARTING_POSITION_X, -OUTER_LEG_STARTING_POSITION_Y, i);

			legRightFront.setFootPosition(OUTER_LEG_STARTING_POSITION_X, OUTER_LEG_STARTING_POSITION_Y, i);
			legRightMiddle.setFootPosition(80, 45, i);
			legRightRear.setFootPosition(OUTER_LEG_STARTING_POSITION_X, -OUTER_LEG_STARTING_POSITION_Y, i);
		}

		waitTillEndOfMove();

		ServoController.setBodySpeed(Status.getBodySpeed());

		legLeftMiddle.setFootPosition(INNER_LEG_STARTING_POSITION_X, INNER_LEG_STARTING_POSITION_Y, SUSPENSION_HEIGHT - STEP_HEIGHT);
//		waitTillEndOfMove();
		legLeftMiddle.setFootPosition(INNER_LEG_STARTING_POSITION_X, INNER_LEG_STARTING_POSITION_Y, SUSPENSION_HEIGHT);
//		waitTillEndOfMove();
		legRightMiddle.setFootPosition(INNER_LEG_STARTING_POSITION_X, INNER_LEG_STARTING_POSITION_Y, SUSPENSION_HEIGHT - STEP_HEIGHT);
//		waitTillEndOfMove();
		legRightMiddle.setFootPosition(INNER_LEG_STARTING_POSITION_X, INNER_LEG_STARTING_POSITION_Y, SUSPENSION_HEIGHT);
//		waitTillEndOfMove();
		System.out.println("standUp() - end");
		currentlyInMotion = false;
		Status.setSleepMode(false);
		return true;
	}

	
	public static boolean lieDown() {
		if (currentlyInMotion) {
			return false;
		}
		System.out.println("lieDown() - start");
		currentlyInMotion = true;
		ServoController.setBodySpeed(Status.getBodySpeed());

		legLeftMiddle.setFootPosition(80, 45, SUSPENSION_HEIGHT - STEP_HEIGHT);
		waitTillEndOfMove();
		legLeftMiddle.setFootPosition(80, 45, SUSPENSION_HEIGHT);
		waitTillEndOfMove();
		legRightMiddle.setFootPosition(80, 45, SUSPENSION_HEIGHT - STEP_HEIGHT);
		waitTillEndOfMove();
		legRightMiddle.setFootPosition(80, 45, SUSPENSION_HEIGHT);
		waitTillEndOfMove();

		ServoController.setBodySpeed(50);

		for (int i = SUSPENSION_HEIGHT; i >= 20; i = i - 10) {
			legLeftFront.setFootPosition(OUTER_LEG_STARTING_POSITION_X, OUTER_LEG_STARTING_POSITION_Y, i);
			legLeftMiddle.setFootPosition(80, 45, i);
			legLeftRear.setFootPosition(OUTER_LEG_STARTING_POSITION_X, -OUTER_LEG_STARTING_POSITION_Y, i);

			legRightFront.setFootPosition(OUTER_LEG_STARTING_POSITION_X, OUTER_LEG_STARTING_POSITION_Y, i);
			legRightMiddle.setFootPosition(80, 45, i);
			legRightRear.setFootPosition(OUTER_LEG_STARTING_POSITION_X, -OUTER_LEG_STARTING_POSITION_Y, i);
		}

		waitTillEndOfMove();
		System.out.println("lieDown() - end");
		currentlyInMotion = false;
		Status.setSleepMode(true);
		return true;
	}

	private static void waitTillEndOfMove() {
		boolean ready = false;
		while(!ready) {
			System.out.println("|");
			if (ServoController.getMovingState() == 0) {
				ready = true;
			}
		}
		System.out.println("waitTillEndOfMove() end");
	}
}