package com.bkyziol.hexapod.movement;

public enum LegLocation {
	LEFT_FRONT("left front"),
	RIGHT_FRONT("right front"),
	LEFT_MIDDLE("left middle"),
	RIGHT_MIDDLE("right middle"),
	LEFT_REAR("left rear"),
	RIGHT_REAR("right rear");

	private final String location;

	LegLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}
}
