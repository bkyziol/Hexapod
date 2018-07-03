package com.bkyziol.hexapod.movement;

public abstract class Servo {
	private final int channel;
	private final int min;
	private final int max;

	protected int current;

	public Servo(int channel, int min, int max) {
		this.channel = channel;
		this.min = min;
		this.max = max;
	}

	public int getChannel() {
		return channel;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public void setValue(int value) {
		if (value > this.max) {
			this.current = this.max;
		} else if (value < this.min) {
			this.current = this.min;
		} else {
			this.current = value;
		}
		ServoController.setTarget(this.channel, this.current);
	}

	public int getCurrent() {
		return current;
	}
}