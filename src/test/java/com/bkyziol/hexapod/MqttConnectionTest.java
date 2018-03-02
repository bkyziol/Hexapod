package com.bkyziol.hexapod;

import static com.bkyziol.hexapod.utils.Constants.CERTIFICATE_FILE;
import static com.bkyziol.hexapod.utils.Constants.CLIENT_ENDPOINT;
import static com.bkyziol.hexapod.utils.Constants.PRIVATE_KEY_FILE;
import static org.junit.Assert.*;

import java.io.InputStream;
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

	private static String clientId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clientId = UUID.randomUUID().toString();
	}

	@Test
	public void test() throws Exception {
		InputStream certificateInputStream = getClass().getResourceAsStream(CERTIFICATE_FILE);
		InputStream privateKeyInputStream = getClass().getResourceAsStream(PRIVATE_KEY_FILE);
		KeyStorePasswordPair pair = AwsIotUtil.getKeyStorePasswordPair(certificateInputStream, privateKeyInputStream);
		AWSIotMqttClient client = new AWSIotMqttClient(CLIENT_ENDPOINT, clientId, pair.keyStore, pair.keyPassword);
		client.connect();
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