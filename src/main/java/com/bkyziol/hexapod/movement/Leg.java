package com.bkyziol.hexapod.movement;

public class Leg {

	private final int HIP_LENGTH = 10;
	private final int THIGH_LENGTH = 80;
	private final int CALF_LENGTH = 110;

	private final boolean legIsOnTheRightSide;

	private final LegServo hip;
	private final LegServo thigh;
	private final LegServo calf;

	public Leg(boolean legIsOnTheRightSide, LegServo hip, LegServo thigh, LegServo calf) {
		this.legIsOnTheRightSide = legIsOnTheRightSide;
		this.hip = hip;
		this.thigh = thigh;
		this.calf = calf;
	}

	public void setFootPosition(double x, double y, double z) {
		double hipAngleInRadians;
		double thighAnlgleInRadians;
		double calfAngleInRadians;

		hipAngleInRadians = Math.atan(y / x);
		double x2 = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) - HIP_LENGTH;
		double y2 = -z;

		double triangleBase = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));

		calfAngleInRadians = Math.acos((Math.pow(CALF_LENGTH, 2) + Math.pow(THIGH_LENGTH, 2) - Math.pow(triangleBase, 2)) / (2 * THIGH_LENGTH * CALF_LENGTH));

		double betaAngle = Math.acos((Math.pow(triangleBase, 2) + Math.pow(THIGH_LENGTH, 2) - Math.pow(CALF_LENGTH, 2)) / (2 * triangleBase * THIGH_LENGTH));
		double alfaAngle = Math.abs(Math.atan(x / y));

		thighAnlgleInRadians = betaAngle + alfaAngle - Math.PI / 2;

		if (Double.isNaN(hipAngleInRadians) || Double.isNaN(thighAnlgleInRadians) || Double.isNaN(calfAngleInRadians)) {
			return;
		} else {
			int hipAngleInUS;
			int thighAnlgleInUS;
			int calfAngleInUS;
			if (legIsOnTheRightSide) {
				hipAngleInUS = (int) (hip.getZeroAngleValue() - hipAngleInRadians * hip.getConverterValue());
				thighAnlgleInUS = (int) (thigh.getZeroAngleValue() + thighAnlgleInRadians * thigh.getConverterValue());
				calfAngleInUS = (int) (calf.getZeroAngleValue() - calfAngleInRadians * calf.getConverterValue());
			} else {
				hipAngleInUS = (int) (hip.getZeroAngleValue() + hipAngleInRadians * hip.getConverterValue());
				thighAnlgleInUS = (int) (thigh.getZeroAngleValue() - thighAnlgleInRadians * thigh.getConverterValue());
				calfAngleInUS = (int) (calf.getZeroAngleValue() + calfAngleInRadians * calf.getConverterValue());
			}
			if (hipAngleInUS > hip.getMax() || hipAngleInUS < hip.getMin() || thighAnlgleInUS > thigh.getMax() || thighAnlgleInUS < thigh.getMin() || calfAngleInUS > calf.getMax() || calfAngleInUS < calf.getMin()) {
				System.out.println("X = " + x + " Y = " + y + " Z = " + z);
				System.out.println("Servo " + hip.getChannel() + " : " + hip.getMin() + " - " + hipAngleInUS + " - " + hip.getMax() + " - Error");
				System.out.println("Servo " + thigh.getChannel() + " : " + thigh.getMin() + " - " + thighAnlgleInUS + " - " + thigh.getMax() + " - Error");
				System.out.println("Servo " + calf.getChannel() + " : " + calf.getMin() + " - " + calfAngleInUS + " - " + calf.getMax() + " - Error");
			} else {
				hip.setValue(hipAngleInUS);
				thigh.setValue(thighAnlgleInUS);
				calf.setValue(calfAngleInUS);
			}
		}
	}
}
