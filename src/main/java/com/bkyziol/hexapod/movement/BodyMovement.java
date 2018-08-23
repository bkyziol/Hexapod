package com.bkyziol.hexapod.movement;

import static com.bkyziol.hexapod.movement.Body.*;
import static com.bkyziol.hexapod.movement.utils.CalculationUtils.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;

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

	private static double rightFactor;
	private static double leftFactor;

	private static final int PRECISION = 10;

	private static volatile boolean currentlyInMotion;

	public static Move initPosition = () -> {
		legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, 20);
		legLeftMiddle.setFootPosition(80, 45, 20);
		legLeftRear.setFootPosition(FRONT_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, 20);
		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y, 20);
		legRightMiddle.setFootPosition(80, 45, 20);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y, 20);
	};

	public static Move rise = () -> {
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
		Status.setSleepMode(false);
	};

	public static Move crouch = () -> {
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

		Status.setSleepMode(true);
	};

	public static Move takeStartingPosition = () -> {
		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		riseFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(200);

		legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, FRONT_LEG_STARTING_POSITION_Y);
		legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, MIDDLE_LEG_STARTING_POSITION_Y);
		legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, REAR_LEG_STARTING_POSITION_Y);
		Thread.sleep(200);

		lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(200);
	};

	private static Move stepInPlace = () -> {
		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		riseFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(200);

		lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(200);
	};

	private static Move moveForward = () -> {
		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		int strideLength = Status.getStrideLength();

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, strideLength * rightFactor + 10);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, strideLength / 2 * leftFactor - 10);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -20);

		for (int i = 0; i <= strideLength / 2 - 10; i += PRECISION) {
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

			for (int i = 0; i <= strideLength; i += PRECISION) {
				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, i * leftFactor + 10);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (-strideLength / 2 + i) * rightFactor - 10);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, (-strideLength + i) * leftFactor - 20);

				legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, (strideLength - i) * rightFactor + 10);
				legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (strideLength / 2 - i) * leftFactor - 10);
				legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -i * rightFactor - 20);
				Thread.sleep(20);
			}
			Thread.sleep(600);

			lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(300);

			if (isStillMovingForward()) {
				riseFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(200);

				for (int i = 0; i <= strideLength; i += PRECISION) {
					legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, (strideLength - i) * leftFactor + 10);
					legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (strideLength / 2 - i) * rightFactor - 10);
					legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -i * leftFactor - 20);

					legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, i * rightFactor + 10);
					legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, (-strideLength / 2 + i) * leftFactor - 10);
					legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, (-strideLength + i) * rightFactor - 20);
					Thread.sleep(20);
				}
				Thread.sleep(600);

				lowerFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(300);
			}
		}
		takeStartingPosition.execute();
	};

	private static Move moveBackward = () -> {
		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(200);

		int strideLength = Status.getStrideLength();

		legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, 10);
		legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, - strideLength / 2 - 10);
		legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, - strideLength - 20);

		for (int i = 0; i <= strideLength / 2 - 10; i += PRECISION) {
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

			for (int i = 0; i <= strideLength; i += PRECISION) {
				legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, strideLength - i + 10);
				legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, strideLength / 2 - i - 10);
				legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -20 - i);

				legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, 10 + i);
				legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, -strideLength / 2 + i - 10);
				legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -strideLength + i - 20);
				Thread.sleep(20);
			}
			Thread.sleep(600);

			lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(300);

			if (Status.getBodyMovementType() == BodyMovementType.BACKWARD) {
				riseFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(200);

				for (int i = 0; i <= strideLength; i += PRECISION) {
					legLeftFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, i + 10);
					legRightMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, -strideLength / 2 + i - 10);
					legLeftRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -strideLength + i - 20);

					legRightFront.setFootPosition(FRONT_LEG_STARTING_POSITION_X, strideLength - i + 10);
					legLeftMiddle.setFootPosition(MIDDLE_LEG_STARTING_POSITION_X, strideLength / 2 - i - 10);
					legRightRear.setFootPosition(REAR_LEG_STARTING_POSITION_X, -i -20);
					Thread.sleep(20);
				}
				Thread.sleep(600);

				lowerFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(300);
			}
		}
		takeStartingPosition.execute();
	};

	private static Move strafeRight = () -> {
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
				Thread.sleep(20);
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
					Thread.sleep(20);
				}
				Thread.sleep(400);

				lowerFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(200);
			}
		}
		Thread.sleep(200);
		takeStartingPosition.execute();
	};

	private static Move strafeLeft = () -> {
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
				Thread.sleep(20);
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
					Thread.sleep(20);
				}
				Thread.sleep(400);

				lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(200);
			}
		}
		Thread.sleep(200);
		takeStartingPosition.execute();
	};

	public static Move turnLeft = () -> {
		riseFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(250);
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(false, 2, legLeftFront);
			rotationByAngle(false, 2, legRightMiddle);
			rotationByAngle(false, 2, legLeftRear);

			rotationByAngle(true, 2, legRightFront);
			rotationByAngle(true, 2, legRightRear);
			rotationByAngle(true, 2, legLeftMiddle);
			Thread.sleep(50);
		}
		lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(250);

		while (Status.getBodyMovementType() == BodyMovementType.TURN_LEFT) {
			riseFoots(legRightFront, legLeftMiddle, legRightRear);
			Thread.sleep(250);
			for (int i = 0; i <= 10; i++) {
				rotationByAngle(true, 2, legLeftFront);
				rotationByAngle(true, 2, legRightMiddle);
				rotationByAngle(true, 2, legLeftRear);

				rotationByAngle(false, 2, legRightFront);
				rotationByAngle(false, 2, legRightRear);
				rotationByAngle(false, 2, legLeftMiddle);
				Thread.sleep(50);
			}
			lowerFoots(legRightFront, legLeftMiddle, legRightRear);
			Thread.sleep(250);

			if (Status.getBodyMovementType() == BodyMovementType.TURN_LEFT) {
				riseFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(250);
				for (int i = 0; i <= 10; i++) {
					rotationByAngle(false, 2, legLeftFront);
					rotationByAngle(false, 2, legRightMiddle);
					rotationByAngle(false, 2, legLeftRear);

					rotationByAngle(true, 2, legRightFront);
					rotationByAngle(true, 2, legRightRear);
					rotationByAngle(true, 2, legLeftMiddle);
					Thread.sleep(50);
				}
				lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
				Thread.sleep(250);
			}
		}
		takeStartingPosition.execute();;
	};

	public static Move turnRight = () -> {
		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(250);
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(false, 2, legLeftFront);
			rotationByAngle(false, 2, legRightMiddle);
			rotationByAngle(false, 2, legLeftRear);

			rotationByAngle(true, 2, legRightFront);
			rotationByAngle(true, 2, legRightRear);
			rotationByAngle(true, 2, legLeftMiddle);
			Thread.sleep(50);
		}
		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(250);

		while (Status.getBodyMovementType() == BodyMovementType.TURN_RIGHT) {
			riseFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(250);
			for (int i = 0; i <= 10; i++) {
				rotationByAngle(true, 2, legLeftFront);
				rotationByAngle(true, 2, legRightMiddle);
				rotationByAngle(true, 2, legLeftRear);

				rotationByAngle(false, 2, legRightFront);
				rotationByAngle(false, 2, legRightRear);
				rotationByAngle(false, 2, legLeftMiddle);
				Thread.sleep(50);
			}
			lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
			Thread.sleep(250);

			if (Status.getBodyMovementType() == BodyMovementType.TURN_RIGHT) {
				riseFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(250);
				for (int i = 0; i <= 10; i++) {
					rotationByAngle(false, 2, legLeftFront);
					rotationByAngle(false, 2, legRightMiddle);
					rotationByAngle(false, 2, legLeftRear);

					rotationByAngle(true, 2, legRightFront);
					rotationByAngle(true, 2, legRightRear);
					rotationByAngle(true, 2, legLeftMiddle);
					Thread.sleep(50);
				}
				lowerFoots(legRightFront, legLeftMiddle, legRightRear);
				Thread.sleep(250);
			}
		}
		takeStartingPosition.execute();;
	};

	public static Move lookLeft = () -> {
		riseFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(250);
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(false, 2, legLeftFront);
			rotationByAngle(false, 2, legRightMiddle);
			rotationByAngle(false, 2, legLeftRear);

			rotationByAngle(true, 2, legRightFront);
			rotationByAngle(true, 2, legRightRear);
			rotationByAngle(true, 2, legLeftMiddle);
			Thread.sleep(50);
		}
		lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(250);

		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(250);
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(true, 2, legLeftFront);
			rotationByAngle(true, 2, legRightMiddle);
			rotationByAngle(true, 2, legLeftRear);

			rotationByAngle(false, 2, legRightFront);
			rotationByAngle(false, 2, legRightRear);
			rotationByAngle(false, 2, legLeftMiddle);
			Thread.sleep(50);
		}
		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(250);
	};

	public static Move lookRight = () -> {
		riseFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(250);
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(false, 2, legLeftFront);
			rotationByAngle(false, 2, legRightMiddle);
			rotationByAngle(false, 2, legLeftRear);

			rotationByAngle(true, 2, legRightFront);
			rotationByAngle(true, 2, legRightRear);
			rotationByAngle(true, 2, legLeftMiddle);
			Thread.sleep(50);
		}
		lowerFoots(legRightFront, legLeftMiddle, legRightRear);
		Thread.sleep(250);

		riseFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(250);
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(true, 2, legLeftFront);
			rotationByAngle(true, 2, legRightMiddle);
			rotationByAngle(true, 2, legLeftRear);

			rotationByAngle(false, 2, legRightFront);
			rotationByAngle(false, 2, legRightRear);
			rotationByAngle(false, 2, legLeftMiddle);
			Thread.sleep(50);
		}
		lowerFoots(legLeftFront, legRightMiddle, legLeftRear);
		Thread.sleep(250);
	};

	public static Move rotateLeft = () -> {
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(true, 2, legLeftFront);
			rotationByAngle(true, 2, legRightMiddle);
			rotationByAngle(true, 2, legLeftRear);

			rotationByAngle(true, 2, legRightFront);
			rotationByAngle(true, 2, legRightRear);
			rotationByAngle(true, 2, legLeftMiddle);
			Thread.sleep(20);
		}
		Thread.sleep(100);
	};

	public static Move rotateRight = () -> {
		for (int i = 0; i <= 5; i++) {
			rotationByAngle(false, 2, legLeftFront);
			rotationByAngle(false, 2, legRightMiddle);
			rotationByAngle(false, 2, legLeftRear);

			rotationByAngle(false, 2, legRightFront);
			rotationByAngle(false, 2, legRightRear);
			rotationByAngle(false, 2, legLeftMiddle);
			Thread.sleep(20);
		}
		Thread.sleep(100);
	};

	public static Move enableLookAroundPosition = () -> {
		if (Status.isLookAroundMode()) {
			return;
		}

		FootPosition oldLeftFront = legLeftFront.getFootPosition();
		FootPosition oldLeftMiddle = legLeftMiddle.getFootPosition();
		FootPosition oldLeftRear = legLeftRear.getFootPosition();
		FootPosition oldRightFront = legRightFront.getFootPosition();
		FootPosition oldRightMiddle = legRightMiddle.getFootPosition();
		FootPosition oldRightRead = legRightRear.getFootPosition();

		for (int i = 0; i <= 4; i += 1) {
			legLeftFront.setFootPosition(oldLeftFront.getX(), oldLeftFront.getY() - 5);
			legLeftMiddle.setFootPosition(oldLeftMiddle.getX(), oldLeftMiddle.getY() - 5);
			legLeftRear.setFootPosition(oldLeftRear.getX(), oldLeftRear.getY() - 5);
			legRightFront.setFootPosition(oldRightFront.getX(), oldRightFront.getY() - 5);
			legRightMiddle.setFootPosition(oldRightMiddle.getX(), oldRightMiddle.getY() - 5);
			legRightRear.setFootPosition(oldRightRead.getX(), oldRightRead.getY() - 5);
			Thread.sleep(20);
		}
		Thread.sleep(200);

		Status.setLookAroundMode(true);
	};

	public static Move disableLookAroundPosition = () -> {
		if (!Status.isLookAroundMode()) {
			return;
		}

		FootPosition oldLeftFront = legLeftFront.getFootPosition();
		FootPosition oldLeftMiddle = legLeftMiddle.getFootPosition();
		FootPosition oldLeftRear = legLeftRear.getFootPosition();
		FootPosition oldRightFront = legRightFront.getFootPosition();
		FootPosition oldRightMiddle = legRightMiddle.getFootPosition();
		FootPosition oldRightRead = legRightRear.getFootPosition();

		for (int i = 0; i <= 4; i += 1) {
			legLeftFront.setFootPosition(oldLeftFront.getX(), oldLeftFront.getY() + 5);
			legLeftMiddle.setFootPosition(oldLeftMiddle.getX(), oldLeftMiddle.getY() + 5);
			legLeftRear.setFootPosition(oldLeftRear.getX(), oldLeftRear.getY() + 5);
			legRightFront.setFootPosition(oldRightFront.getX(), oldRightFront.getY() + 5);
			legRightMiddle.setFootPosition(oldRightMiddle.getX(), oldRightMiddle.getY() + 5);
			legRightRear.setFootPosition(oldRightRead.getX(), oldRightRead.getY() + 5);
			Thread.sleep(20);
		}
		Thread.sleep(200);

		Status.setLookAroundMode(false);
	};

	private static Move riseTorso = () -> {
		for (int i = 0; i < 10; i++) {
			legLeftFront.lowerFoot(5);
			legRightFront.lowerFoot(5);
			legLeftMiddle.lowerFoot(5);
			legRightMiddle.lowerFoot(5);
			legLeftRear.lowerFoot(5);
			legRightRear.lowerFoot(5);
			Thread.sleep(10);
		}
		Thread.sleep(200);
		for (int i = 0; i < 10; i++) {
			legLeftFront.riseFoot(5);
			legRightFront.riseFoot(5);
			legLeftMiddle.riseFoot(5);
			legRightMiddle.riseFoot(5);
			legLeftRear.riseFoot(5);
			legRightRear.riseFoot(5);
			Thread.sleep(10);
		}
		Thread.sleep(200);
	};

	private static Move bounceTorso = () -> {
		for (int i = 0; i < 4; i++) {
			legLeftFront.riseFoot(5);
			legRightFront.riseFoot(5);
			legLeftMiddle.riseFoot(5);
			legRightMiddle.riseFoot(5);
			legLeftRear.riseFoot(5);
			legRightRear.riseFoot(5);
			Thread.sleep(10);
		}
		Thread.sleep(100);
		for (int i = 0; i < 8; i++) {
			legLeftFront.lowerFoot(5);
			legRightFront.lowerFoot(5);
			legLeftMiddle.lowerFoot(5);
			legRightMiddle.lowerFoot(5);
			legLeftRear.lowerFoot(5);
			legRightRear.lowerFoot(5);
			Thread.sleep(10);
		}
		Thread.sleep(100);
		for (int i = 0; i < 4; i++) {
			legLeftFront.riseFoot(5);
			legRightFront.riseFoot(5);
			legLeftMiddle.riseFoot(5);
			legRightMiddle.riseFoot(5);
			legLeftRear.riseFoot(5);
			legRightRear.riseFoot(5);
			Thread.sleep(10);
		}
	};

	private static Move riseRandomFoot = () -> {
		Leg leg;
		Integer random = new Random().nextInt(5);
		switch (random) {
		case 0:
			leg = legLeftFront;
			break;
		case 1:
			leg = legLeftMiddle;
			break;
		case 2:
			leg = legLeftRear;
			break;
		case 3:
			leg = legRightFront;
			break;
		case 4:
			leg = legRightMiddle;
			break;
		default:
			leg = legRightRear;
			break;
		}
		leg.riseFoot();
		Thread.sleep(200);
		leg.lowerFoot();
	};

	public static BooleanSupplier isMovementPossible = () -> {
		if (currentlyInMotion) {
			return false;
		}
		if (Status.isSleepMode()) {
			return false;
		}
		return true;
	};

	public static BooleanSupplier canRise = () -> {
		if (currentlyInMotion) {
			return false;
		}
		if (!Status.isSleepMode()) {
			return false;
		}
		return true;
	};

	public static void makeMove() throws InterruptedException {
		if (!currentlyInMotion) {
			switch (Status.getBodyMovementType()) {
			case BACKWARD:
				executeMove(isMovementPossible, moveBackward);
				break;
			case CROUCH:
				executeMove(isMovementPossible, crouch);
				break;
			case FORWARD:
				leftFactor = 1;
				rightFactor = 1;
				executeMove(isMovementPossible, moveForward);
				break;
			case HARD_LEFT:
				leftFactor = 0.1;
				rightFactor = 1;
				executeMove(isMovementPossible, moveForward);
				break;
			case HARD_RIGHT:
				leftFactor = 1;
				rightFactor = 0.1;
				executeMove(isMovementPossible, moveForward);
				break;
			case LEFT:
				leftFactor = 0.4;
				rightFactor = 1;
				executeMove(isMovementPossible, moveForward);
				break;
			case RIGHT:
				leftFactor = 1;
				rightFactor = 0.4;
				executeMove(isMovementPossible, moveForward);
				break;
			case RISE:
				executeMove(canRise, rise);
				break;
			case SLIGHTLY_LEFT:
				leftFactor = 0.7;
				rightFactor = 1;
				executeMove(isMovementPossible, moveForward);
				break;
			case SLIGHTLY_RIGHT:
				leftFactor = 1;
				rightFactor = 0.7;
				executeMove(isMovementPossible, moveForward);
				break;
			case STRAFE_LEFT:
				executeMove(isMovementPossible, strafeLeft);
				break;
			case STRAFE_RIGHT:
				executeMove(isMovementPossible, strafeRight);
				break;
			case TURN_LEFT:
				executeMove(isMovementPossible, turnLeft);
				break;
			case TURN_RIGHT:
				executeMove(isMovementPossible, turnRight);
				break;
			default:
				break;
			}
		}
	}

	public static void executeMove(BooleanSupplier condition, Move move) {
		new Thread(() -> {
			if (condition.getAsBoolean()) {
				try {
					currentlyInMotion = true;
					move.execute();
				} catch (InterruptedException | BodyMovementException e) {
					System.out.println(e.getMessage());
				}
				currentlyInMotion = false;
			}
		}).start();
	}

	private static void rotationByAngle(boolean clockwise, double angle, Leg leg) throws BodyMovementException {
		FootPosition footPosition = leg.getFootPosition();
		double X = footPosition.getX();
		double Y = footPosition.getY();

		if (angle <= 0) {
			return;
		}
		FootPosition newFootPosition = null;
		if (leg.getLocation() == LegLocation.LEFT_FRONT) {
			if (clockwise) {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, Y, true);
			} else {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, Y, false);
			}
		}
		if (leg.getLocation() == LegLocation.RIGHT_FRONT) {
			if (clockwise) {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, Y, false);
			} else {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, Y, true);
			}
		}
		if (leg.getLocation() == LegLocation.LEFT_REAR) {
			if (clockwise) {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, -Y, false);
				newFootPosition.setY(-newFootPosition.getY());
			} else {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, -Y, true);
				newFootPosition.setY(-newFootPosition.getY());
			}
		}
		if (leg.getLocation() == LegLocation.RIGHT_REAR) {
			if (clockwise) {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, -Y, true);
				newFootPosition.setY(-newFootPosition.getY());
			} else {
				newFootPosition = calculateForTurnOutsideLeg(angle, X, -Y, false);
				newFootPosition.setY(-newFootPosition.getY());
			}
		}
		if (leg.getLocation() == LegLocation.LEFT_MIDDLE) {
			if (clockwise) {
				newFootPosition = calculateForTurnInsideLeg(angle, X, Y, true);
			} else {
				newFootPosition = calculateForTurnInsideLeg(angle, X, Y, false);
			}
		}
		if (leg.getLocation() == LegLocation.RIGHT_MIDDLE) {
			if (clockwise) {
				newFootPosition = calculateForTurnInsideLeg(angle, X, Y, false);
			} else {
				newFootPosition = calculateForTurnInsideLeg(angle, X, Y, true);
			}
		}
		leg.setFootPosition(newFootPosition.getX(), newFootPosition.getY());
	}

	public static void makeRandomMove() throws InterruptedException {
		Integer random = new Random().nextInt(3);
		switch (random) {
		case 0:
			executeMove(isMovementPossible, stepInPlace);
			break;
		case 1:
			executeMove(isMovementPossible, riseRandomFoot);
			break;
		case 2:
			executeMove(isMovementPossible, riseTorso);
			break;
		case 3:
			executeMove(isMovementPossible, bounceTorso);
			break;
		}
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

	private static void riseFoots(Leg... legs) throws InterruptedException, BodyMovementException {
		for (Leg leg : legs) {
			leg.riseFoot();
		}
	}

	private static void lowerFoots(Leg... legs) throws InterruptedException, BodyMovementException {
		for (Leg leg : legs) {
			leg.lowerFoot();
		}
	}

	public static int getStepHeight() {
		return STEP_HEIGHT;
	}
}