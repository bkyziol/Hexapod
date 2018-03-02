package com.bkyziol.hexapod.camera;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class Camera extends Thread {
	private volatile boolean running = true;
	private volatile BufferedImage image;
	private volatile long imageTimestamp;
	private volatile int numberOfFrames = 0;

	@Override
	public void run() {
		try {
			while (running) {
				try {
					if (System.getProperty("os.arch").equals("arm")) {
						Webcam.setDriver(new V4l4jDriver());
					}
					System.out.println("Search for camera...");
					Webcam webcam = Webcam.getDefault();
					if (webcam != null) {
						System.out.println("Camera found: " + webcam.getName());
						webcam.setViewSize(WebcamResolution.VGA.getSize());
						webcam.open();
						while (running) {
							image = webcam.getImage();
							if (image != null) {
								imageTimestamp = System.currentTimeMillis();
								numberOfFrames++;
								// System.out.println(numberOfFrames);
							} else {
								System.out.println("No image captured");
								Thread.sleep(1_000);
							}
						}
						webcam.close();
					} else {
						System.out.println("Camera: no camera detected");
						Thread.sleep(5_000);
					}
				} catch (Throwable e) {
					System.out.println("Camera problem: " + e.getMessage());
					Thread.sleep(5_000);
				}
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void terminate() {
		running = false;
	}

	public BufferedImage getImage() {
		return image;
	}

	public long getImageTimestamp() {
		return imageTimestamp;
	}

	public int getNumberOfFrames() {
		return numberOfFrames;
	}
}