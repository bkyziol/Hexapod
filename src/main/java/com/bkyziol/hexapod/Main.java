package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.CameraRuntimeException;
import com.bkyziol.hexapod.camera.HexapodCamera;
import com.bkyziol.hexapod.mqtt.HexapodConnection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

	static {
		Logger.getLogger("").getHandlers()[0].setLevel(Level.SEVERE);
	}

	public static void main(String[] args) throws InterruptedException {
		HexapodConnection.connect();
		HexapodCamera camera = new HexapodCamera();
		camera.open();
		Thread.sleep(2000);
		try {
			camera.start();
		} catch (CameraRuntimeException e1) {
			System.out.println("Camera start exception");
		}
		Runnable frameSender = new Runnable() {
			@Override
			public void run() {
				try {
					camera.sendFrame();
				} catch (CameraRuntimeException e) {
					System.out.println("Capture frame exception");
				}
			}
		};
		ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
		timer.scheduleAtFixedRate(frameSender, 0, 250, TimeUnit.MILLISECONDS);
		while(true) {
			System.out.println("--------------------------------------");
			Thread.sleep(1000);
		}
	}
}
