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
			case RISE:
				rise();
				break;
			case CROUCH:
				crouch();
				break;
			default:
				break;
			}
		}
	}

	public static void rise() {
		if (currentlyInMotion) {
			System.out.println("in motion");
			return;
		}
		if (!Status.isSleepMode()) {
			System.out.println("already up");
			return;
		}
		System.out.println("rise - start");
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
		waitTillEndOfMove();
		legLeftMiddle.setFootPosition(INNER_LEG_STARTING_POSITION_X, INNER_LEG_STARTING_POSITION_Y, SUSPENSION_HEIGHT);
		waitTillEndOfMove();
		legRightMiddle.setFootPosition(INNER_LEG_STARTING_POSITION_X, INNER_LEG_STARTING_POSITION_Y, SUSPENSION_HEIGHT - STEP_HEIGHT);
		waitTillEndOfMove();
		legRightMiddle.setFootPosition(INNER_LEG_STARTING_POSITION_X, INNER_LEG_STARTING_POSITION_Y, SUSPENSION_HEIGHT);
		waitTillEndOfMove();
		System.out.println("rise - end");
		currentlyInMotion = false;
		Status.setSleepMode(false);
	}

	
	public static void crouch() {
		if (currentlyInMotion) {
			System.out.println("in motion");
			return;
		}
		if (Status.isSleepMode()) {
			System.out.println("already down");
			return;
		}
		System.out.println("crouch - start");
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
		System.out.println("crouch - end");
		currentlyInMotion = false;
		Status.setSleepMode(true);
	}

	public static void waitTillEndOfMove() {
		try {
			int i = 0;
			while (i < 3) {
				System.out.print(".");
				if (ServoController.getMovingState() == 0) {
					i++;
				}
				Thread.sleep(20);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("waitTillEndOfMove() end");
	}
}