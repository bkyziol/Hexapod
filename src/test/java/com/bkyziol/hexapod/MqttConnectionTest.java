package com.bkyziol.hexapod;

import static com.bkyziol.hexapod.utils.Constants.CLIENT_ENDPOINT;
import static org.junit.Assert.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.bkyziol.hexapod.connection.AwsIotUtil;
import com.bkyziol.hexapod.connection.AwsIotUtil.KeyStorePasswordPair;

public class MqttConnectionTest {

	private static String clientId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		clientId = UUID.randomUUID().toString();
	}

	@Test
	public void test() throws Exception {
		System.out.println(System.getProperty("user.dir"));
		KeyStorePasswordPair pair = AwsIotUtil.getKeyStorePasswordPair(
				"./resources/d6aee98257-certificate.pem.crt",
				"./resources/d6aee98257-private.pem.key"
			);
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