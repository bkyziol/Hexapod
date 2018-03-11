package com.bkyziol.hexapod.mqtt;

import javax.swing.JLabel;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

public class Topic extends AWSIotTopic {
	JLabel imgLabel = new JLabel();

	public Topic(String topic, AWSIotQos qos) {
		super(topic, qos);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		System.out.println("Message received: " + System.currentTimeMillis());
		System.out.println();
	}
}