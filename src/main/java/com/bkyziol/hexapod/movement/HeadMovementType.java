package com.bkyziol.hexapod.movement;
public enum HeadMovementType {
	UP("CAMERA_UP"),
	DOWN("CAMERA_DOWN"),
	LEFT("CAMERA_LEFT"),
	RIGHT("CAMERA_RIGHT"),
	CENTER("CAMERA_CENTER"),
	STAND_BY("STAND_BY")
	;

	private final String name;

	private HeadMovementType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}