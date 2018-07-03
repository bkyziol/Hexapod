package com.bkyziol.hexapod.movement.utils;

import com.bkyziol.hexapod.movement.FootPosition;

public final class CalculationUtils {

	private static final double HEX_LENGTH = 166;
	private static final double HEX_WIDTH = 96;

	private CalculationUtils() {
		super();
	}

	public static FootPosition calculateForTurnOutsideLeg(double angle, double currentX, double currentY, boolean increase) {
		double alfa = toRadians(angle);
		double a = Math.sqrt(Math.pow(HEX_LENGTH / 2, 2) + Math.pow(HEX_WIDTH / 2, 2));
		double c = Math.sqrt(Math.pow(currentX, 2) + Math.pow(currentY, 2));
		double beta = Math.atan((HEX_LENGTH / 2) / (HEX_WIDTH / 2));
		double b = Math.sqrt(Math.pow(a, 2) + Math.pow(a, 2) - 2 * a * a * Math.cos(alfa));
		double zeta = (Math.PI - alfa) / 2;
		double gamma;
		if (increase) {
			gamma = Math.PI - zeta - beta;
		} else {
			gamma = zeta - beta;
		}
		double theta = Math.atan(currentY / currentX);
		double delta;
		if (increase) {
			delta = theta + gamma;
		} else {
			delta = Math.PI - theta - gamma;
		}
		double z = Math.sqrt(Math.pow(b, 2) + Math.pow(c, 2) - 2 * b * c * Math.cos(delta));
		double epsilon = Math.acos((Math.pow(c, 2) + Math.pow(z, 2) - Math.pow(b, 2)) / (2 * c * z));
		double sigma = Math.PI / 2 - theta;
		double omega;
		if (increase) {
			omega = sigma - epsilon - alfa;
		} else {
			omega = alfa + sigma + epsilon;
		}
		double newX = Math.sin(omega) * z;
		double newY = Math.cos(omega) * z;
		FootPosition footPosition = new FootPosition();
		footPosition.setX(newX);
		footPosition.setY(newY);
		return footPosition;
	}

	public static FootPosition calculateForTurnInsideLeg(double angle, double currentX, double currentY, boolean increase) {
		double alfa = toRadians(angle);
		double a = HEX_WIDTH / 2;
		double c = Math.sqrt(Math.pow(currentX, 2) + Math.pow(currentY, 2));
		double b = Math.sqrt(Math.pow(a, 2) + Math.pow(a, 2) - 2 * a * a * Math.cos(alfa));
		double zeta = (Math.PI - alfa) / 2;
		double theta = Math.atan(currentY / currentX);
		double delta;
		if (increase) {
			delta = Math.PI - zeta + theta;
		} else {
			delta = Math.PI - theta - zeta;
		}
		double z = Math.sqrt(Math.pow(b, 2) + Math.pow(c, 2) - 2 * b * c * Math.cos(delta));
		double epsilon = Math.acos((Math.pow(c, 2) + Math.pow(z, 2) - Math.pow(b, 2)) / (2 * c * z));
		double sigma = Math.PI / 2 - theta;
		double omega;
		if (increase) {
			omega = sigma - epsilon - alfa;
		} else {
			omega = Math.PI - sigma - epsilon - alfa;
		}
		double newX = Math.sin(omega) * z;
		double newY = Math.cos(omega) * z;
		if (!increase) {
			newY = -newY;
		}
		FootPosition footPosition = new FootPosition();
		footPosition.setX(newX);
		footPosition.setY(newY);
		return footPosition;
	}


	public static double toRadians(double angle){
		double angleInRadians = angle * 3.14 / 180;
		return angleInRadians;
	}

	public static double toDegrees(double angle){
		double angleInRadians = angle / 3.14 * 180;
		return angleInRadians;
	}
}
