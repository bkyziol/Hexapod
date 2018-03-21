package com.bkyziol.hexapod.mqtt;

import java.text.SimpleDateFormat;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.bkyziol.hexapod.model.CommandMessage;
import com.google.gson.Gson;

public class CommandTopic extends AWSIotTopic {

	public CommandTopic(String topic, AWSIotQos qos) {
		super(topic, qos);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		Gson gson = new Gson();
		CommandMessage orderMessagePayload = gson.fromJson(message.getStringPayload(), CommandMessage.class);
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss.SSSS");
		System.out.println(sf.format(orderMessagePayload.getTimestamp()) + " --> " + orderMessagePayload.getCameraMovement());
	}
}