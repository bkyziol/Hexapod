package com.bkyziol.hexapod.iot;


import java.nio.ByteBuffer;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.bkyziol.hexapod.connection.TopicName;

public class CameraTopic extends AWSIotTopic {

	public CameraTopic() {
		super(TopicName.CAMERA.getName(), AWSIotQos.QOS0);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		final byte[] payload = message.getPayload();
		final ByteBuffer buffer = ByteBuffer.wrap(payload).asReadOnlyBuffer();
//		buffer.order(LITTLE_ENDIAN);

		final long timestamp = buffer.getLong();
//		System.out.println(timestamp + " / " + System.currentTimeMillis());
//		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss.SSSS");
//		System.out.println(sf.format(System.currentTimeMillis()));
//		System.out.println("received: " + System.currentTimeMillis());
	}
}