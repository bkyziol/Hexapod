package com.bkyziol.hexapod.movement;
public enum BodyMovementType {
	STAND_BY("STAND_BY"),
	RISE("RISE"),
	CROUCH("CROUCH"),
	;

	private final String name;

	private BodyMovementType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}