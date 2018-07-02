package com.bkyziol.hexapod.movement;

public final class Head {

	private static final HeadServo horizontalServo = new HeadServo(22, 4600, 6000, 8000);
	private static final HeadServo verticalServo = new HeadServo(23, 5000, 6000, 8000);

	public static HeadServo getHorizontalServo() {
		return horizontalServo;
	}

	public static HeadServo getVerticalServo() {
		return verticalServo;
	}
}

