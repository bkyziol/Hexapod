package com.bkyziol.hexapod.movement;
public enum BodyMovementType {
	BACKWARD("BACKWARD"),
	CROUCH("CROUCH"),
	FORWARD("FORWARD"),
	HARD_LEFT("HARD_LEFT"),
	HARD_RIGHT("HARD_RIGHT"),
	LEFT("LEFT"),
	RIGHT("RIGHT"),
	RISE("RISE"),
	SLIGHTLY_LEFT("SLIGHTLY_LEFT"),
	SLIGHTLY_RIGHT("SLIGHTLY_RIGHTT"),
	STAND_BY("STAND_BY"),
	STRAFE_LEFT("STRAFE_LEFT"), 
	STRAFE_RIGHT("STRAFE_RIGHT"), 
	TURN_LEFT("TURN_LEFT"),
	TURN_RIGHT("TURN_RIGHT")
	;

	private final String name;

	private BodyMovementType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}