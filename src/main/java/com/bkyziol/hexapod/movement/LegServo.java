package com.bkyziol.hexapod.movement;

public class LegServo extends Servo{
	private final int zeroAngleValue;
	private final int converterValue;

	public LegServo(int channel, int zeroAngleValue, int min, int max, int converterValue) {
		super(channel, min, max);
		this.zeroAngleValue = zeroAngleValue;
		this.converterValue = converterValue;
	}

	public int getZeroAngleValue() {
		return zeroAngleValue;
	}

	public int getConverterValue() {
		return converterValue;
	}
}