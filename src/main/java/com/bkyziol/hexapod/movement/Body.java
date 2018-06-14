package com.bkyziol.hexapod.movement;

public class Body {

	public static final Leg legLeftFront = new Leg(
			false,
			new LegServo(0, 4400, 4400, 7900, 2165),
			new LegServo(6, 6900, 4000, 8000, 2228),
			new LegServo(12, 3250, 4200, 8000, 2005)
		);

	public static final Leg legLeftMiddle = new Leg(
			false,
			new LegServo(2, 6000, 4000, 7700, 2132),
			new LegServo(8, 6550, 4000, 8000, 2068),
			new LegServo(14, 3250, 4600, 8000, 2546)
		);

	public static final Leg legLeftRear = new Leg(
			false,
			new LegServo(4, 7850, 4600, 7850, 1973),
			new LegServo(10, 7100, 4000, 8000, 2132),
			new LegServo(14, 3300, 4200, 8000, 1973)
		);

	public static final Leg legRightFront = new Leg(
			true,
			new LegServo(1, 7600, 4300, 8000, 2100),
			new LegServo(7, 5300, 4000, 8000, 2100),
			new LegServo(13, 8800, 4000, 8000, 2164)
		);

	public static final Leg legRightMiddle = new Leg(
			true,
			new LegServo(3, 6200, 4400, 8000, 2100),
			new LegServo(9, 5400, 4000, 8000, 2036),
			new LegServo(15, 9000, 4500, 8000, 2355)
		);

	public static final Leg legRightRear = new Leg(
			true,
			new LegServo(5, 4200, 4050, 7500, 2100),
			new LegServo(11, 5200, 4000, 8000, 2100),
			new LegServo(17, 8750, 4100, 8000, 2196)
		);

	private Body() {
		super();
	}

}
