package com.bkyziol.hexapod;

import com.bkyziol.hexapod.camera.HexapodCamera;
import com.bkyziol.hexapod.mqtt.ConnectionRuntimeException;
import com.bkyziol.hexapod.mqtt.HexapodConnection;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.varia.LevelMatchFilter;



public class Main {

	static {
		Logger.getLogger("").getHandlers()[0].setLevel(Level.SEVERE);
	}

	public static void main(String[] args) throws InterruptedException {
		HexapodCamera camera = new HexapodCamera();
		camera.open();
		Thread.sleep(2000);
		camera.start();
		Thread.sleep(5000);
		camera.close();
		Thread.sleep(5000);
		System.out.println(camera.getNumberOfSentFrames());
		System.out.println(camera.getNumberOfCapturedFrames());
//		System.out.println(HexapodConnection.isConnected());
//		camera.startCapturing();
//		Runnable frameSender = new Runnable() {
//			@Override
//			public void run() {
//				camera.captureAndSend();
//				camera.sendFrame();
//				String string = new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis());
//				System.out.println(string);
//			}
//		};
//		ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
//		timer.scheduleAtFixedRate(frameSender, 0, 250, TimeUnit.MILLISECONDS);
//		timer.scheduleAtFixedRate(frameSender, 0, 2000, TimeUnit.MILLISECONDS);
	}
}
