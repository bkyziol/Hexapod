package com.bkyziol.hexapod.movement;

public class ChannelValues {
	private final int channel;
	private final int min;
	private final int neutral;
	private final int max;

	private int current;

	public ChannelValues(int channel, int min, int neutral, int max) {
		this.channel = channel;
		this.min = min;
		this.neutral = neutral;
		this.max = max;
		this.current = neutral;
	}

	public int getChannel() {
		return channel;
	}

	public int getMin() {
		return min;
	}

	public int getNeutral() {
		return neutral;
	}

	public int getMax() {
		return max;
	}

	public void setPosition(int position) {
		if (position < this.max && position > this.min) {
			this.current = position;
			ServoController.setTarget(this.channel, this.current);
//			System.out.println(this.channel + " -> " + this.current);
//		} else {
//			System.out.println("Out of bound");
		}
	}

	public int getCurrent() {
		return current;
	}

	public void increaseAngle(int i) {
		setPosition(this.current + i);
	}

	public void decreaseAngle(int i) {
		setPosition(this.current - i);
	}

}