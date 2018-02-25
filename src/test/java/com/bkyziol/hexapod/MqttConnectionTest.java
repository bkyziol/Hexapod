package com.bkyziol.hexapod;

import static com.bkyziol.hexapod.utils.Constants.CERTIFICATE_FILE;
import static com.bkyziol.hexapod.utils.Constants.CLIENT_ENDPOINT;
import static com.bkyziol.hexapod.utils.Constants.PRIVATE_KEY_FILE;
import static org.junit.Assert.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.bkyziol.hexapod.mqtt.AwsIotUtil;
import com.bkyziol.hexapod.mqtt.AwsIotUtil.KeyStorePasswordPair;

public class MqttConnectionTest {

	private static AWSIotMqttClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		String clientId = UUID.randomUUID().toString();
		KeyStorePasswordPair pair = AwsIotUtil.getKeyStorePasswordPair(CERTIFICATE_FILE, PRIVATE_KEY_FILE);
		client = new AWSIotMqttClient(CLIENT_ENDPOINT, clientId, pair.keyStore, pair.keyPassword);
		client.connect();
	}

	@Test
	public void test() throws Exception {
		final CompletableFuture<String> future = new CompletableFuture<>();
		AWSIotMessage message = new AWSIotMessage("TEST_TOPIC", AWSIotQos.QOS1, "TEST_PAYLOAD") {
			@Override
			public void onSuccess() {
				future.complete("success");
			}

			@Override
			public void onFailure() {
				future.complete("failure");
			}

			@Override
			public void onTimeout() {
				future.complete("failure");
			}
		};
		client.publish(message, 2000);
		assertEquals("success", future.get());
		client.disconnect();
	}

}