package com.bkyziol.hexapod.mqtt;

public enum TopicName {
	CAMERA("hexapod/camera"),
	STATUS("hexapod/status"),
	COMMAND("hexapod/command")
	;

	private final String name;

	private TopicName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
