package com.bkyziol.hexapod.mqtt;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

public class HexapodTopic extends AWSIotTopic {
	public HexapodTopic(String topic, AWSIotQos qos) {
		super(topic, qos);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		// Long time = Long.valueOf(message.getStringPayload());
		// Long diff = System.currentTimeMillis() - time;
		// System.out.println(diff.toString());
		// System.out.println(message.getStringPayload());
		System.out.println("message arrived");
	}

}