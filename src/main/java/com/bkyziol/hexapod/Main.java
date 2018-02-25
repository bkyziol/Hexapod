package com.bkyziol.hexapod;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.bkyziol.hexapod.mqtt.AppToHexapodTopic;
import com.bkyziol.hexapod.mqtt.AwsIotUtil;
import com.bkyziol.hexapod.mqtt.AwsIotUtil.KeyStorePasswordPair;

import static com.bkyziol.hexapod.utils.Constants.*;

import java.util.UUID;

public class Main {

	private static final String HEXAPOD_TO_APP_TOPIC = "HEXAPOD_TO_APP";
	private static final String APP_TO_HEXAPOD_TOPIC = "APP_TO_HEXAPOD";

	public static void main(String[] args) {
		try {
			String clientEndpoint = CLIENT_ENDPOINT;
			String certificateFile = CERTIFICATE_FILE;
			String privateKeyFile = PRIVATE_KEY_FILE;
			String clientId = UUID.randomUUID().toString();
			KeyStorePasswordPair pair = AwsIotUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
			AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
			client.connect();
			AWSIotQos qos = AWSIotQos.QOS0;
			AppToHexapodTopic topic = new AppToHexapodTopic(HEXAPOD_TO_APP_TOPIC, qos);
			client.subscribe(topic);
		} catch (AWSIotException e) {
			BUGSNAG.notify(e);
			System.out.println(e);
		}
	}
}
