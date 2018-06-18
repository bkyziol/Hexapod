package com.bkyziol.hexapod.movement;

import static com.bkyziol.hexapod.movement.Body.*;
import static com.bkyziol.hexapod.movement.utils.CalculationUtils.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BodyMovement {

	private static final int SUSPENSION_HEIGHT = 80;
	private static final int STEP_HEIGHT = 40;
	private static final int FRONT_LEG_STARTING_POSITION_X = 60;
	private static final int FRONT_LEG_STARTING_POSITION_Y = 60;
	private static final int MIDDLE_LEG_STARTING_POSITION_X = 90;
	private static final int MIDDLE_LEG_STARTING_POSITION_Y = 0;
	private static final int REAR_LEG_STARTING_POSITION_X = 60;
	private static final int REAR_LEG_STARTING_POSITION_Y = -60;

	private static final int STRAFE_LENGTH = 60;
	private static final int STRIDE_LENGTH = 100;

	private static double rightFactor;
	private static double leftFactor;

	private static final int SPEED = 100;
	private static final int DELAY = 20;
	private static final int PRECISION = 10;

	private static boolean currentlyInMotion = false;

	public static void makeMove() throws InterruptedException {
		if (!currentlyInMotion) {
			switch (Status.getBodyMovementType()) {
			case BACKWARD:
				moveBackward();
				break;
			case CROUCH:
				crouch();
				break;
			case FORWARD:
				leftFactor = 1;
				rightFactor = 1;
				moveForward();
				break;
			case HARD_LEFT:
				leftFactor = 0.1;
				rightFactor = 1;
				moveForward();
				break;
			case HARD_RIGHT:
				leftFactor = 1;
				rightFactor = 0.1;
				moveForward();
				break;
			case LEFT:
				leftFactor = 0.4;
				rightFactor = 1;
				moveForward();
				break;
			case RIGHT:
				leftFactor = 1;
				rightFactor = 0.4;
				moveForward();
				break;
			case RISE:
				rise();
				break;
			case SLIGHTLY_LEFT:
				leftFactor = 0.7;
				rightFactor = 1;
				moveForward();
				break;
			case SLIGHTLY_RIGHT:
				leftFactor = 1;
				rightFactor = 0.7;
				moveForward();
				break;
			case STRAFE_LEFT:
				strafeLeft();
				break;
			case STRAFE_RIGHT:
				strafeRight();
				break;
			case TURN_LEFT:
				turnLeft(10);
				break;
			case TURN_RIGHT:
			default:
				break;
			}
		}
	}

	public static void initPosition() {
		legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, 20);
		legLeftMiddle.setFootPosition(80, 45, 20);
		legLeftRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, 20);
		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, 20);
		legRightMiddle.setFootPosition(80, 45, 20);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, 20);
	}


	private static void riseFoots(Leg... legs) throws InterruptedException {
		for (Leg leg : legs) {
			leg.riseFoot();
		}
	}


	private static void lowerFoots(Leg... legs) throws InterruptedException {
		for (Leg leg : legs) {
			leg.lowerFoot();
		}
	}

	public static void rise() throws InterruptedException {
		if (currentlyInMotion) {
			return;
		}
		if (!Status.isSleepMode()) {
			return;
		}

		currentlyInMotion = true;
		ServoController.setBodySpeed(SPEED);
		for (int i = 20; i <= SUSPENSION_HEIGHT; i += PRECISION) {
			legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, i);
			legLeftMiddle.setFootPosition(80, 45, i);
			legLeftRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, i);
			legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, i);
			legRightMiddle.setFootPosition(80, 45, i);
			legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, i);
			Thread.sleep(20);
		}
		Thread.sleep(750);
		legLeftMiddle.riseFoot();
		Thread.sleep(100);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		Thread.sleep(100);
		legLeftMiddle.lowerFoot();
		Thread.sleep(200);
		legRightMiddle.riseFoot();
		Thread.sleep(100);
		legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		Thread.sleep(100);
		legRightMiddle.lowerFoot();
		Thread.sleep(100);
		currentlyInMotion = false;
		Status.setSleepMode(false);
		Thread.sleep(2000);
	}

	public static void crouch() throws InterruptedException {
		if (!isMovementPossible()) {
			return;
		}

		currentlyInMotion = true;
		ServoController.setBodySpeed(SPEED);

		legLeftMiddle.riseFoot();
		Thread.sleep(100);
		legLeftMiddle.setFootPosition(80, 45);
		Thread.sleep(100);
		legLeftMiddle.lowerFoot();
		Thread.sleep(200);
		legRightMiddle.riseFoot();
		Thread.sleep(100);
		legRightMiddle.setFootPosition(80, 45);
		Thread.sleep(100);
		legRightMiddle.lowerFoot();
		Thread.sleep(100);

		for (int i = SUSPENSION_HEIGHT; i >= 20; i -= PRECISION) {
			legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, i);
			legLeftMiddle.setFootPosition(80, 45, i);
			legLeftRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, i);
			legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, i);
			legRightMiddle.setFootPosition(80, 45, i);
			legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, i);
			Thread.sleep(20);
		}
		Thread.sleep(750);
		currentlyInMotion = false;
		Status.setSleepMode(true);
	}

	public static void takeStartingPosition() throws InterruptedException{
		if (!isMovementPossible()) {
			return;
		}
		ServoController.setBodySpeed(100);

		legRightFront.riseFoot();
		legLeftMiddle.riseFoot();
		legRightRear.riseFoot();
		Thread.sleep(200);
		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		legRightFront.lowerFoot();
		legLeftMiddle.lowerFoot();
		legRightRear.lowerFoot();
		Thread.sleep(200);

		legLeftFront.riseFoot();
		legRightMiddle.riseFoot();
		legLeftRear.riseFoot();
		Thread.sleep(200);

		legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
		legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		legLeftFront.lowerFoot();
		legRightMiddle.lowerFoot();
		legLeftRear.lowerFoot();
		currentlyInMotion = false;
	}

	private static void moveForward() throws InterruptedException {
		if (!isMovementPossible()) {
			return;
		}
		ServoController.setBodySpeed(100);

		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, STRIDE_LENGTH * rightFactor + 10);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, STRIDE_LENGTH / 2 * leftFactor - 10);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -20);

		for (int i = 0; i <= STRIDE_LENGTH / 2 - 10; i += PRECISION) {
			legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y - i * leftFactor);
			legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y - i * rightFactor);
			legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y - i * leftFactor);
			Thread.sleep(20);
		}
		Thread.sleep(200);

		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(300);

		while (isStillMovingForward()) {
			riseFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(200);

			for (int i = 0; i <= STRIDE_LENGTH; i += PRECISION) {
				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, i * leftFactor + 10);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (-STRIDE_LENGTH / 2 + i) * rightFactor - 10);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, (-STRIDE_LENGTH + i) * leftFactor - 20);

				legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, (STRIDE_LENGTH - i) * rightFactor + 10);
				legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (STRIDE_LENGTH / 2 - i) * leftFactor - 10);
				legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -i * rightFactor - 20);
				Thread.sleep(20);
			}
			Thread.sleep(800);

			lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(300);

			if (isStillMovingForward()) {
				riseFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(200);

				for (int i = 0; i <= STRIDE_LENGTH; i += PRECISION) {
					legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, (STRIDE_LENGTH - i) * leftFactor + 10);
					legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (STRIDE_LENGTH / 2 - i) * rightFactor - 10);
					legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -i * leftFactor -20 );

					legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, i * rightFactor + 10);
					legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (-STRIDE_LENGTH / 2 + i) * leftFactor - 10);
					legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, (-STRIDE_LENGTH + i) * rightFactor - 20);
					Thread.sleep(20);
				}
				Thread.sleep(800);

				lowerFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(300);
			}
			if (!isStillMovingForward()) {
				riseFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(200);

				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
				Thread.sleep(200);

				lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(300);
			}
		}

		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(300);

		currentlyInMotion = false;
	}

	private static void moveBackward() throws InterruptedException {
		if (!isMovementPossible()) {
			return;
		}
		ServoController.setBodySpeed(100);

		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, 10);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, -STRIDE_LENGTH / 2 - 10);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -STRIDE_LENGTH - 20);

		for (int i = 0; i <= STRIDE_LENGTH / 2 - 10; i += PRECISION) {
			legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y + i);
			legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y + i);
			legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y + i);
			Thread.sleep(20);
		}
		Thread.sleep(200);

		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(300);

		while (Status.getBodyMovementType() == BodyMovementType.BACKWARD) {
			riseFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(200);

			for (int i = 0; i <= STRIDE_LENGTH; i += PRECISION) {
				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, STRIDE_LENGTH - i + 10);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, STRIDE_LENGTH / 2 - i - 10);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -20 - i);

				legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, 10 + i);
				legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, -STRIDE_LENGTH / 2 + i - 10);
				legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -STRIDE_LENGTH + i - 20);
				Thread.sleep(20);
			}
			Thread.sleep(800);

			lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(300);

			if (Status.getBodyMovementType() == BodyMovementType.BACKWARD) {
				riseFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(200);

				for (int i = 0; i <= STRIDE_LENGTH; i += PRECISION) {
					legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, i + 10);
					legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, -STRIDE_LENGTH / 2 + i - 10);
					legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -STRIDE_LENGTH + i - 20);

					legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, STRIDE_LENGTH - i + 10);
					legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, STRIDE_LENGTH / 2 - i - 10);
					legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -i -20);
					Thread.sleep(20);
				}
				Thread.sleep(800);

				lowerFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(300);
			}
			if (Status.getBodyMovementType() != BodyMovementType.BACKWARD) {
				riseFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(200);

				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
				Thread.sleep(200);

				lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(300);
			}
		}

		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(300);

		currentlyInMotion = false;
	}

	private static void strafeRight() throws InterruptedException{
		ServoController.setBodySpeed(100);;

		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, FRONT_LEG_STARTING_POSITION_Y);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, MIDDLE_LEG_STARTING_POSITION_Y);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		while(Status.getBodyMovementType() == BodyMovementType.STRAFE_RIGHT)
		{
			riseFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(200);

			legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, FRONT_LEG_STARTING_POSITION_Y);
			legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, MIDDLE_LEG_STARTING_POSITION_Y);
			legLeftRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, REAR_LEG_STARTING_POSITION_Y);

			for (int i = STRAFE_LENGTH / 2; i >= -STRAFE_LENGTH / 2; i -= PRECISION) {
				legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + i, FRONT_LEG_STARTING_POSITION_Y);
				legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - i, MIDDLE_LEG_STARTING_POSITION_Y);
				legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X + i, REAR_LEG_STARTING_POSITION_Y);
			}
			Thread.sleep(400);

			lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(200);

			if (Status.getBodyMovementType() == BodyMovementType.STRAFE_RIGHT){
				riseFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(200);

				legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, FRONT_LEG_STARTING_POSITION_Y);
				legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, MIDDLE_LEG_STARTING_POSITION_Y);
				legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, REAR_LEG_STARTING_POSITION_Y);


				for (int i = -STRAFE_LENGTH / 2; i <= STRAFE_LENGTH / 2; i += PRECISION) {
					legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + i, FRONT_LEG_STARTING_POSITION_Y);
					legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - i, MIDDLE_LEG_STARTING_POSITION_Y);
					legLeftRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X + i, REAR_LEG_STARTING_POSITION_Y);
				}
				Thread.sleep(400);

				lowerFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(200);
			}
		}

		takeStartingPosition();
		Thread.sleep(200);
	}

	private static void strafeLeft() throws InterruptedException{
		ServoController.setBodySpeed(100);;

		riseFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(200);

		legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, FRONT_LEG_STARTING_POSITION_Y);
		legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, MIDDLE_LEG_STARTING_POSITION_Y);
		legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(200);

		while(Status.getBodyMovementType() == BodyMovementType.STRAFE_LEFT)
		{
			riseFoots(legRightFront, legLeftMiddle, legRightRear);
			Thread.sleep(200);

			legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, FRONT_LEG_STARTING_POSITION_Y);
			legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, MIDDLE_LEG_STARTING_POSITION_Y);
			legRightRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, REAR_LEG_STARTING_POSITION_Y);

			for (int i = STRAFE_LENGTH / 2; i >= -STRAFE_LENGTH / 2; i -= PRECISION) {
				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + i, FRONT_LEG_STARTING_POSITION_Y);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - i, MIDDLE_LEG_STARTING_POSITION_Y);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X + i, REAR_LEG_STARTING_POSITION_Y);
			}
			Thread.sleep(400);

			lowerFoots(legRightFront, legLeftMiddle, legRightRear);
			Thread.sleep(200);

			if (Status.getBodyMovementType() == BodyMovementType.STRAFE_LEFT){
				riseFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(200);

				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, FRONT_LEG_STARTING_POSITION_Y);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - STRAFE_LENGTH / 2, MIDDLE_LEG_STARTING_POSITION_Y);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X + STRAFE_LENGTH / 2, REAR_LEG_STARTING_POSITION_Y);


				for (int i = -STRAFE_LENGTH / 2; i <= STRAFE_LENGTH / 2; i += PRECISION) {
					legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X + i, FRONT_LEG_STARTING_POSITION_Y);
					legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X - i, MIDDLE_LEG_STARTING_POSITION_Y);
					legRightRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X + i, REAR_LEG_STARTING_POSITION_Y);
				}
				Thread.sleep(400);

				lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(200);
			}
		}

		takeStartingPosition();
		Thread.sleep(200);
	}

	private static void turnLeft(double angleOfRotation) throws InterruptedException {
		if (!isMovementPossible()) {
			return;
		}

		ServoController.setBodySpeed(100);

		System.out.println("turnLeft");
		while (Status.getBodyMovementType() == BodyMovementType.TURN_LEFT) {
			for (int i = 1; i <= angleOfRotation; i = i + 1) {
				rotationByAngle(i, legLeftFront);
				rotationByAngle(-i, legRightMiddle);
				rotationByAngle(i, legLeftRear);
				rotationByAngle(-i, legRightFront);
				rotationByAngle(i, legLeftMiddle);
				rotationByAngle(-i, legRightRear);
				Thread.sleep(500);
			}
			Thread.sleep(800);
		}

		takeStartingPosition();
		Thread.sleep(200);
	}

	private static void rotationByAngle(double angle, Leg leg) {
		FootPosition newFootPosition = calculateForTurn(angle, leg.getFootPosition());
		leg.setFootPosition(newFootPosition.getX(), newFootPosition.getY());
	}

	private static double toRadians(double angle){
		return angle * Math.PI / 180;
	}

	private static boolean isMovementPossible() {
		if (currentlyInMotion) {
			System.out.println("in motion");
			return false;
		}
		if (Status.isSleepMode()) {
			return false;
		}
		return true;
	}

	private static boolean isStillMovingForward() {
		Set<BodyMovementType> movementTypes = new HashSet<>();
		movementTypes.add(BodyMovementType.FORWARD);
		movementTypes.add(BodyMovementType.HARD_LEFT);
		movementTypes.add(BodyMovementType.HARD_RIGHT);
		movementTypes.add(BodyMovementType.LEFT);
		movementTypes.add(BodyMovementType.RIGHT);
		movementTypes.add(BodyMovementType.SLIGHTLY_LEFT);
		movementTypes.add(BodyMovementType.SLIGHTLY_RIGHT);
		return movementTypes.contains(Status.getBodyMovementType()) ? true : false;
	}
//	public static void waitTillEndOfMove() {
//		try {
//			int i = 0;
//			while (i < 4) {
//				System.out.print(".");
//				if (ServoController.getMovingState() == 0) {
//					i++;
//				}
//				Thread.sleep(20);
//			}
//			
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println(" end");
//	}

	public static int getStepHeight() {
		return STEP_HEIGHT;
	}
}