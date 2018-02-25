package com.bkyziol.hexapod.mqtt;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

public class AppToHexapodTopic extends AWSIotTopic {
	public AppToHexapodTopic(String topic, AWSIotQos qos) {
		super(topic, qos);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		System.out.println(message.getStringPayload());
	}

}