package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.HexapodCamera;
import com.bkyziol.hexapod.mqtt.HexapodConnection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		HexapodConnection connection = new HexapodConnection();
		HexapodCamera camera = new HexapodCamera(connection);
		connection.connect();
		camera.open();
		Thread.sleep(2000);
		camera.startCapturing();
		Runnable frameSender = new Runnable() {
			@Override
			public void run() {
//				camera.captureAndSend();
				camera.sendFrame();
			}
		};
		ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
		timer.scheduleAtFixedRate(frameSender, 0, 250, TimeUnit.MILLISECONDS);
	}
}
