package com.bkyziol.hexapod.mqtt;

import static com.bkyziol.hexapod.utils.Constants.CERTIFICATE_FILE;
import static com.bkyziol.hexapod.utils.Constants.CLIENT_ENDPOINT;
import static com.bkyziol.hexapod.utils.Constants.PRIVATE_KEY_FILE;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.UUID;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.bkyziol.hexapod.mqtt.AwsIotUtil.KeyStorePasswordPair;

public class Connection {
	private static final String HEXAPOD_TO_APP_TOPIC = "HEXAPOD_TO_APP";
	private static final String APP_TO_HEXAPOD_TOPIC = "APP_TO_HEXAPOD";
	private static final String CLIENT_ID = UUID.randomUUID().toString();

	private static AWSIotMqttClient client;
	private static HexapodTopic appToHexapodTopic;

	public void start() throws InterruptedException, AWSIotException, URISyntaxException {
		System.out.println("Connection: connecting...");
		InputStream certificateInputStream = getClass().getResourceAsStream(CERTIFICATE_FILE);
		InputStream privateKeyInputStream = getClass().getResourceAsStream(PRIVATE_KEY_FILE);
		KeyStorePasswordPair pair = AwsIotUtil.getKeyStorePasswordPair(certificateInputStream, privateKeyInputStream);
		client = new AWSIotMqttClient(CLIENT_ENDPOINT, CLIENT_ID, pair.keyStore, pair.keyPassword);
		client.connect();
		AWSIotQos qos = AWSIotQos.QOS0;
		appToHexapodTopic = new HexapodTopic(APP_TO_HEXAPOD_TOPIC, qos);
		client.subscribe(appToHexapodTopic);
		System.out.println("Connection: connected!");
	}

	public void stop() throws AWSIotException {
		client.disconnect();
	}

	public void publish(String payload) throws AWSIotException {
		client.publish(HEXAPOD_TO_APP_TOPIC, payload);
	}
}
