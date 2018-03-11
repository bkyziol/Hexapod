package com.bkyziol.hexapod.mqtt;

public enum TopicName {
	IMAGE("IMAGE"),
	STATUS("STATUS"),
	COMMAND("COMMAND")
	;

	private final String name;

	private TopicName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
