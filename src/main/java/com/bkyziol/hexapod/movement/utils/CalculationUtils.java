package com.bkyziol.hexapod.movement.utils;

import com.bkyziol.hexapod.movement.FootPosition;

public final class CalculationUtils {

	private static final double HEX_LENGTH = 166;
	private static final double HEX_WIDTH = 96;

	private CalculationUtils() {
		super();
	}

	public static FootPosition calculateForTurn(double angle, double currentX, double currentY, boolean increase) {
		double alfa = toRadians(angle);
		double a = Math.sqrt(Math.pow(HEX_LENGTH / 2, 2) + Math.pow(HEX_WIDTH / 2, 2));
//		System.out.println("a: " + a);
		double c = Math.sqrt(Math.pow(currentX, 2) + Math.pow(currentY, 2));
//		System.out.println("c: " + c);
		double beta = Math.atan((HEX_LENGTH / 2) / (HEX_WIDTH / 2));
//		System.out.println("beta: " + toDegrees(beta));
		double b = Math.sqrt(Math.pow(a, 2) + Math.pow(a, 2) - 2 * a * a * Math.cos(alfa));
//		System.out.println("b: " + b);
		double zeta = (Math.PI - alfa) / 2;
//		System.out.println("zeta: " + toDegrees(zeta));
		double gamma;
		if (increase) {
			gamma = Math.PI - zeta - beta;
		} else {
			gamma = zeta - beta;
		}
//		System.out.println("gamma: " + toDegrees(gamma));
		double theta = Math.atan(currentY / currentX);
//		System.out.println("theta: " + toDegrees(theta));
		double delta;
		if (increase) {
			delta = theta + gamma;
		} else {
			delta = Math.PI - theta - gamma;
		}
//		System.out.println("delta: " + toDegrees(delta));
		double z = Math.sqrt(Math.pow(b, 2) + Math.pow(c, 2) - 2 * b * c * Math.cos(delta));
//		System.out.println("z: " + z);
		double epsilon = Math.acos((Math.pow(c, 2) + Math.pow(z, 2) - Math.pow(b, 2)) / (2 * c * z));
//		System.out.println("epsilon: " + toDegrees(epsilon));
		double sigma = Math.PI / 2 - theta;
//		System.out.println("sigma: " + toDegrees(sigma));
		double omega;
		if (increase) {
			omega = sigma - epsilon - alfa;
		} else {
			omega = alfa + sigma + epsilon;
		}
//		System.out.println("omega: " + toDegrees(omega));
		double newX = Math.sin(omega) * z;
		System.out.println("newX: " + newX);
		double newY = Math.cos(omega) * z;
		System.out.println("newY: " + newY);
		FootPosition footPosition = new FootPosition();
		footPosition.setX(newX);
		footPosition.setY(newY);
		return footPosition;
	}

	public static FootPosition calculateForTurn2(double angle, double currentX, double currentY, boolean increase) {
		double alfa = toRadians(angle);
		double a = HEX_WIDTH / 2;
		System.out.println("a: " + a);
		double c = Math.sqrt(Math.pow(currentX, 2) + Math.pow(currentY, 2));
		System.out.println("c: " + c);
		double b = Math.sqrt(Math.pow(a, 2) + Math.pow(a, 2) - 2 * a * a * Math.cos(alfa));
		System.out.println("b: " + b);
		double zeta = (Math.PI - alfa) / 2;
		System.out.println("zeta: " + toDegrees(zeta));
		double theta = Math.atan(currentY / currentX);
		System.out.println("theta: " + toDegrees(theta));
		double delta;
		if (increase) {
			delta = Math.PI - theta - zeta;
		} else {
			delta = Math.PI - zeta + theta;
		}
		System.out.println("delta: " + toDegrees(delta));
		double z = Math.sqrt(Math.pow(b, 2) + Math.pow(c, 2) - 2 * b * c * Math.cos(delta));
		System.out.println("z: " + z);
		double epsilon = Math.acos((Math.pow(c, 2) + Math.pow(z, 2) - Math.pow(b, 2)) / (2 * c * z));
		System.out.println("epsilon: " + toDegrees(epsilon));
		double sigma = Math.PI / 2 - theta;
		System.out.println("sigma: " + toDegrees(sigma));
		double omega;
		if (increase) {
			omega = sigma - epsilon - alfa;
		} else {
			omega = Math.PI - sigma - epsilon - alfa;
		}
		System.out.println("omega: " + toDegrees(omega));
		double newX = Math.sin(omega) * z;
		System.out.println("newX: " + newX);
		double newY = Math.cos(omega) * z;
		System.out.println("newY: " + newY);
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
