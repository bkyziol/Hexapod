package com.bkyziol.hexapod.movement;

public class HeadServo extends Servo {

	private final int center;

	public HeadServo(int channel, int min, int center, int max) {
		super(channel, min, max);
		this.center = center;
		this.current = center;
	}

	public void increaseAngle(int i) {
		setValue(this.current + i);
	}

	public void decreaseAngle(int i) {
		setValue(this.current - i);
	}

	public int getCenter() {
		return center;
	}
}