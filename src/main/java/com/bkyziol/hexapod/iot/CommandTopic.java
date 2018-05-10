package com.bkyziol.hexapod.iot;

import java.text.SimpleDateFormat;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.model.CommandMessage;
import com.google.gson.Gson;

public class CommandTopic extends AWSIotTopic {

	private long lastMessageTimestamp = 0;

	public CommandTopic() {
		super(TopicName.COMMAND.getName(), AWSIotQos.QOS0);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		lastMessageTimestamp = System.currentTimeMillis();
		Gson gson = new Gson();
		CommandMessage commandMessagePayload = gson.fromJson(message.getStringPayload(), CommandMessage.class);
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss.SSSS");
		System.out.println(sf.format(commandMessagePayload.getTimestamp()) + " --> " + commandMessagePayload.getCameraMovement());
	}

	public long getLastMessageTimestamp() {
		return lastMessageTimestamp;
	}
}