package com.bkyziol.hexapod.camera;

public enum VideQuality {

	QUALITY_4(480, 360),
	QUALITY_3(320,240),
	QUALITY_2(240,180),
	QUALITY_1(160,120);
	
	private final int width;
	private final int height;

	VideQuality(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public static VideQuality getQuality(int key) {
		switch (key) {
		case 4:
			return QUALITY_4;
		case 3:
			return QUALITY_3;
		case 2:
			return QUALITY_2;
		case 1:
			return QUALITY_1;
		default:
			return QUALITY_3;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
