package com.bkyziol.hexapod.mqtt;

import java.util.UUID;
import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.bkyziol.hexapod.mqtt.AwsIotUtil.KeyStorePasswordPair;
import static com.bkyziol.hexapod.utils.Constants.*;

public final class HexapodConnection {
	private static final Topic statusTopic = new Topic(TopicName.STATUS.getName(), AWSIotQos.QOS0);
	private static final Topic imageTopic = new Topic(TopicName.IMAGE.getName(), AWSIotQos.QOS0);
	private static final String CLIENT_ID = UUID.randomUUID().toString();
	private static final Thread connectionThread  = openConnectionThread();
	private static final AWSIotMqttClient awsClient;

	static {
		KeyStorePasswordPair pair = AwsIotUtil.getKeyStorePasswordPair(CERTIFICATE_FILE, PRIVATE_KEY_FILE);
		awsClient = new AWSIotMqttClient(CLIENT_ENDPOINT, CLIENT_ID, pair.keyStore, pair.keyPassword);
		connect();
	}

	private static synchronized void connect() {
		if (!connectionThread.isAlive()) {
			connectionThread.start();
		}
	}

	public void stop() throws ConnectionRuntimeException {
		if (!awsClient.getConnectionStatus().equals(AWSIotConnectionStatus.DISCONNECTED)) {
			try {
				awsClient.disconnect();
			} catch (AWSIotException e) {
				throw new ConnectionRuntimeException("Unable to disconnect.", e);
			}
		}
	}

	public static void sendImage(byte[] payload) throws ConnectionRuntimeException {
		if (payload == null) {
			return;
		}
		if (awsClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)) {
			try {
				awsClient.publish(TopicName.IMAGE.getName(), payload);
				System.out.println("Image send: " + System.currentTimeMillis());
			} catch (AWSIotException e) {
				throw new ConnectionRuntimeException("Can't publish message.", e);
			}
		} else {
			connect();
			throw new ConnectionRuntimeException("No connection.");
		}
	}

	public static void sendStatus(String payload) throws ConnectionRuntimeException {
		if (payload == null) {
			return;
		}
		if (awsClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)) {
			try {
				awsClient.publish(TopicName.STATUS.getName(), payload);
				System.out.println("Status send: " + System.currentTimeMillis());
			} catch (AWSIotException e) {
				throw new ConnectionRuntimeException("Can't publish message.", e);
			}
		} else {
			connect();
			throw new ConnectionRuntimeException("No connection.");
		}
	}

	public static boolean isConnected() {
		return awsClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)? true : false; 
	}

	private static Thread openConnectionThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				while (!awsClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)) {
					System.out.println("Connecting with AWS...");
					try {
						awsClient.connect(5000, true);
						awsClient.subscribe(statusTopic);
						awsClient.subscribe(imageTopic);
					} catch (AWSIotException | AWSIotTimeoutException e) {
						System.out.println("Connection failed, reconnecting!");
					}
				}
				System.out.println("Connected!");
			}
		});
	}
}
