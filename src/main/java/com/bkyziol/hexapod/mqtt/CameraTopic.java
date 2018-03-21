package com.bkyziol.hexapod.mqtt;

import java.text.SimpleDateFormat;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

public class CameraTopic extends AWSIotTopic {

	public CameraTopic(String topic, AWSIotQos qos) {
		super(topic, qos);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss.SSSS");
		System.out.println(sf.format(System.currentTimeMillis()));
	}
}