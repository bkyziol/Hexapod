package com.bkyziol.hexapod.movement;
public enum HeadMovementType {
	UP("UP"),
	DOWN("DOWN"),
	LEFT("LEFT"),
	RIGHT("RIGHT"),
	CENTER("CENTER"),
	STAND_BY("STAND_BY"),
	TRACKING("TRACKING"),
	;

	private final String name;

	private HeadMovementType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}