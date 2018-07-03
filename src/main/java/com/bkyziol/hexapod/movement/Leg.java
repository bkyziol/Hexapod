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

	public void setFootPosition(double x, double y) throws BodyMovementException {
		setFootPosition(x, y, footPosition.getZ());
	}

	public void setFootPosition(final double x, final double y, final double z) throws BodyMovementException {
		double hipAngleInRadians = Math.atan(y / x);
		double x2 = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) - HIP_LENGTH;
		double triangleBase = Math.sqrt(Math.pow(x2, 2) + Math.pow(z, 2));
		double calfAngleInRadians = Math.acos((Math.pow(CALF_LENGTH, 2) + Math.pow(THIGH_LENGTH, 2) - Math.pow(triangleBase, 2)) / (2 * THIGH_LENGTH * CALF_LENGTH));
		double betaAngle = Math.acos((Math.pow(triangleBase, 2) + Math.pow(THIGH_LENGTH, 2) - Math.pow(CALF_LENGTH, 2)) / (2 * triangleBase * THIGH_LENGTH));
		double alfaAngle = Math.abs(Math.atan(x2 / z));
		double thighAnlgleInRadians = betaAngle + alfaAngle - Math.PI / 2;

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
		if (hipAngleInUS > hip.getMax() || hipAngleInUS < hip.getMin() || thighAnlgleInUS > thigh.getMax()
				|| thighAnlgleInUS < thigh.getMin() || calfAngleInUS > calf.getMax() || calfAngleInUS < calf.getMin()) {
			System.out.println("Leg at " + location.toString() + " unable to reach position \nx: " + x + " \ny: " + y + " \nz: " + z);
			throw new BodyMovementException("Leg at " + location.toString() + " unable to reach position \nx: " + x + " \ny: " + y + " \nz: " + z);
		} else {
			hip.setValue(hipAngleInUS);
			thigh.setValue(thighAnlgleInUS);
			calf.setValue(calfAngleInUS);
			footPosition.setXYZ(x, y, z);
		}
	}

	public void riseFoot() throws BodyMovementException {
		riseFoot(BodyMovement.getStepHeight());
	}

	public void riseFoot(double stepHeight) throws BodyMovementException {
		setFootPosition(footPosition.getX(), footPosition.getY(), footPosition.getZ() - stepHeight);
	}

	public void lowerFoot() throws BodyMovementException {
		lowerFoot(BodyMovement.getStepHeight());
	}

	public void lowerFoot(double stepHeight) throws BodyMovementException {
		setFootPosition(footPosition.getX(), footPosition.getY(), footPosition.getZ() + stepHeight);
	}

	public FootPosition getFootPosition() {
		return footPosition;
	}

	public LegLocation getLocation() {
		return location;
	}
}
