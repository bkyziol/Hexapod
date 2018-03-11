package com.bkyziol.hexapod.mqtt;

import java.io.InputStream;
import java.util.UUID;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.bkyziol.hexapod.Main;
import com.bkyziol.hexapod.mqtt.AwsIotUtil.KeyStorePasswordPair;
import static com.bkyziol.hexapod.utils.Constants.*;

public class HexapodConnection {
	private final String CLIENT_ID = UUID.randomUUID().toString();
	private final AWSIotMqttClient awsClient;

	private boolean connected;
	private Topic statusTopic;
	private Topic imageTopic;

	public HexapodConnection() {
		InputStream certificateInputStream = Main.class.getResourceAsStream("/" + CERTIFICATE_FILE);
		InputStream privateKeyInputStream = Main.class.getResourceAsStream("/" + PRIVATE_KEY_FILE);
		KeyStorePasswordPair pair = AwsIotUtil.getKeyStorePasswordPair(certificateInputStream, privateKeyInputStream);
		awsClient = new AWSIotMqttClient(CLIENT_ENDPOINT, CLIENT_ID, pair.keyStore, pair.keyPassword);
		imageTopic = new Topic(TopicName.IMAGE.getName(), AWSIotQos.QOS0);
		statusTopic = new Topic(TopicName.STATUS.getName(), AWSIotQos.QOS0);
		connected = false;
	}

	public void connect() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Connection: connecting...");
				while(awsClient.getConnectionStatus() != AWSIotConnectionStatus.CONNECTED) {
					try {
						Thread.sleep(1000);
						awsClient.connect();
						awsClient.subscribe(statusTopic);
						awsClient.subscribe(imageTopic);
					} catch (AWSIotException | InterruptedException e) {
						System.out.println("Connection: problem occured reconnecting!");
					}
				}
				connected = true;
				System.out.println("Connection: connection established!");
			}
		}).start();
	}

	public void stop() throws AWSIotException {
		if (connected) {
			awsClient.disconnect();
			connected = false;
		}
	}

	public void sendImage(byte[] payload) {
		if (connected && payload != null) {
			try {
				awsClient.publish(TopicName.IMAGE.getName(), payload);
				System.out.println("Image send: " + System.currentTimeMillis());
			} catch (AWSIotException e) {
				System.out.println("Problem while sending image.");
			}
		}
	}

	public void sendStatus(String payload) {
		if (connected && payload != null) {
			try {
				awsClient.publish(TopicName.STATUS.getName(), payload);
			} catch (AWSIotException e) {
				System.out.println("Problem while sending status.");
			}
		}
	}
}
