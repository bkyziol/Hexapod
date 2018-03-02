package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.Camera;

import com.bkyziol.hexapod.mqtt.Connection;
import static com.bkyziol.hexapod.utils.Constants.BUGSNAG;

public class Main {

	private static Boolean running = true;

	public static void main(String[] args) throws InterruptedException {
		while (running) {
			Camera camera = new Camera();
			Connection connection = new Connection();
			try {
				connection.start();
				camera.start();
				while (true) {
					Thread.sleep(1000);
					int nrFrames = camera.getNumberOfFrames();
					System.out.println(nrFrames);
					connection.publish(String.valueOf(nrFrames));
				}
			} catch (Throwable e) {
				System.out.println("Problem occurred: " + e.getMessage());
				System.out.println("The device will restart");
				BUGSNAG.notify(e);
				Thread.sleep(5_000);
			}
		}
	}
}
