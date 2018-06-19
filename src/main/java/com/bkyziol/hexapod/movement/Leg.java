package com.bkyziol.hexapod.movement;

public class Leg {

	private final int HIP_LENGTH = 10;
	private final int THIGH_LENGTH = 80;
	private final int CALF_LENGTH = 110;

	private final LegLocation location;

	private final LegServo hip;
	private final LegServo thigh;
	private final LegServo calf;

	private final FootPosition footPosition = new FootPosition();

	public Leg(LegLocation location, LegServo hip, LegServo thigh, LegServo calf) {
		this.location = location;
		this.hip = hip;
		this.thigh = thigh;
		this.calf = calf;
	}

	public boolean setFootPosition(double x, double y) {
		return setFootPosition(x, y, footPosition.getZ());
	}

	public boolean setFootPosition(final double x, final double y, final double z) {
		double hipAngleInRadians;
		double thighAnlgleInRadians;
		double calfAngleInRadians;

		hipAngleInRadians = Math.atan(y / x);
		double x2 = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) - HIP_LENGTH;
		double y2 = z;

		double triangleBase = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));

		calfAngleInRadians = Math.acos((Math.pow(CALF_LENGTH, 2) + Math.pow(THIGH_LENGTH, 2) - Math.pow(triangleBase, 2)) / (2 * THIGH_LENGTH * CALF_LENGTH));

		double betaAngle = Math.acos((Math.pow(triangleBase, 2) + Math.pow(THIGH_LENGTH, 2) - Math.pow(CALF_LENGTH, 2)) / (2 * triangleBase * THIGH_LENGTH));
		double alfaAngle = Math.abs(Math.atan(x2 / y2));

		thighAnlgleInRadians = betaAngle + alfaAngle - Math.PI / 2;

		if (Double.isNaN(hipAngleInRadians) || Double.isNaN(thighAnlgleInRadians) || Double.isNaN(calfAngleInRadians)) {
			return false;
		} else {
			int hipAngleInUS;
			int thighAnlgleInUS;
			int calfAngleInUS;
			if (location == LegLocation.RIGHT_FRONT || location == LegLocation.RIGHT_MIDDLE || location == LegLocation.RIGHT_REAR) {
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
				System.out.println("Hip (" + hip.getChannel() + ") : " + hip.getMin() + " - " + hipAngleInUS + " - " + hip.getMax() + " - Error");
				System.out.println("Thigh (" + thigh.getChannel() + ") : " + thigh.getMin() + " - " + thighAnlgleInUS + " - " + thigh.getMax() + " - Error");
				System.out.println("Calf (" + calf.getChannel() + ") : " + calf.getMin() + " - " + calfAngleInUS + " - " + calf.getMax() + " - Error");
				return false;
			} else {
				hip.setValue(hipAngleInUS);
				thigh.setValue(thighAnlgleInUS);
				calf.setValue(calfAngleInUS);
				footPosition.setXYZ(x, y, z);
				return true;
//				System.out.println("X = " + x + " Y = " + y + " Z = " + z);
//				System.out.println("Hip (" + hip.getChannel() + ") : " + hip.getMin() + " - " + hipAngleInUS + " - " + hip.getMax() + " - Error");
//				System.out.println("Thigh (" + thigh.getChannel() + ") : " + thigh.getMin() + " - " + thighAnlgleInUS + " - " + thigh.getMax() + " - Error");
//				System.out.println("Calf (" + calf.getChannel() + ") : " + calf.getMin() + " - " + calfAngleInUS + " - " + calf.getMax() + " - Error");
			}
		}
	}

	public boolean riseFoot() {
		return riseFoot(BodyMovement.getStepHeight());
	}

	public boolean riseFoot(double stepHeight) {
		return setFootPosition(footPosition.getX(), footPosition.getY(), footPosition.getZ() - stepHeight);
	}

	public boolean lowerFoot() {
		return lowerFoot(BodyMovement.getStepHeight());
	}

	public boolean lowerFoot(double stepHeight) {
		return setFootPosition(footPosition.getX(), footPosition.getY(), footPosition.getZ() + stepHeight);
	}

	public FootPosition getFootPosition() {
		return footPosition;
	}

	public LegLocation getLocation() {
		return location;
	}
}
