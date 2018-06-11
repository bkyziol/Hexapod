package com.bkyziol.hexapod.iot;

import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.bkyziol.hexapod.connection.TopicName;
import com.bkyziol.hexapod.model.ServiceMessage;
import com.bkyziol.hexapod.movement.ServoController;
import com.google.gson.Gson;

public class ServiceTopic extends AWSIotTopic {

	public ServiceTopic() {
		super(TopicName.SERVICE.getName(), AWSIotQos.QOS0);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		Gson gson = new Gson();
		ServiceMessage serviceMessagePayload = gson.fromJson(message.getStringPayload(), ServiceMessage.class);
		Map<Integer, Integer> servosPositions = serviceMessagePayload.getServosPositions();

		for (Entry<Integer, Integer> entry : servosPositions.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
			ServoController.setTarget(entry.getKey(), entry.getValue());
		}
	}
}