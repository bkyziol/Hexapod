package com.bkyziol.hexapod.camera;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.bkyziol.hexapod.mqtt.HexapodConnection;


import static com.bkyziol.hexapod.utils.Constants.*;

public class HexapodCamera {

	private ScheduledExecutorService timer;
	private volatile Mat frame;
	private volatile int numberOfCapturedFrames = 0;  //TODO REMOVE

	private int frameWidth = FRAME_WIDTH;
	private int frameHeight = FRAME_HEIGTH;
	private VideoCapture camera;

	private CascadeClassifier faceCascade;
	int absoluteFaceSize = 0;

	private final HexapodConnection connection;
	private final boolean needToRotate;

	public HexapodCamera(HexapodConnection connection) {
		System.load(new File(OPENCV_LIB_FILE).getAbsolutePath());
		this.frame = new Mat();
		this.connection = connection;
		this.faceCascade = new CascadeClassifier();
		System.out.println(HAARCASCADES_PATH + "/haarcascade_frontalface_alt.xml");
		this.faceCascade.load(HAARCASCADES_PATH + "/haarcascade_frontalface_alt.xml");
		if (System.getProperty("os.arch").equals("arm")) {
			needToRotate = true;
		} else {
			needToRotate = false;
		}
	}

	public void open() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					try {
						Thread.sleep(1_000);
						System.out.println("OpenCV version:" + Core.VERSION);
						camera = new VideoCapture(0);
						camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, frameWidth);
						camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, frameHeight);
					} catch (Throwable e) {
						System.out.println("Problem while opening camera: " + e.getMessage());
					}
				} while (camera == null || !camera.isOpened());
			}
		}).start();
	}

	public void startCapturing() {
		if (camera.isOpened()) {
			Runnable frameGrabber = new Runnable() {
				@Override
				public void run() {
					System.out.println("start capture: " + System.currentTimeMillis());
					camera.read(frame);
					System.out.println("stop capture: " + System.currentTimeMillis());
					System.out.println();
				}
			};
			this.timer = Executors.newSingleThreadScheduledExecutor();
			this.timer.scheduleAtFixedRate(frameGrabber, 0, 200, TimeUnit.MILLISECONDS);
		}
	}

	public void captureAndSend() {
		long time = System.currentTimeMillis();
		System.out.println("start: " + System.currentTimeMillis());
		if (camera != null && camera.isOpened()) {
			Mat newFrame = new Mat();
			camera.read(newFrame);
			System.out.println("camera read: " + System.currentTimeMillis());
			if (needToRotate) {
				rotateImage(newFrame);
				System.out.println("rotate: " + System.currentTimeMillis());
			}
			detectFace(newFrame);
			System.out.println("detect face: " + System.currentTimeMillis());
			resizeImage(newFrame, 320 ,240);
			System.out.println("frame resize: " + System.currentTimeMillis());
			convertToGray(newFrame);
			System.out.println("gray scale: " + System.currentTimeMillis());
			numberOfCapturedFrames++;
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", newFrame, mob);
			System.out.println("jpg codec: " + System.currentTimeMillis());
			byte[] imageByteArray = mob.toArray();
			connection.sendImage(imageByteArray);
			System.out.println("summary: " + (System.currentTimeMillis() - time) + " / " + newFrame.size() + " / " + imageByteArray.length);
			System.out.println();
		} else {
			System.out.println("Camera is closed.");
		}
	}

	public void changeFrameSize(int frameWidth, int frameHeight) {
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		if (camera != null && camera.isOpened()) {
			camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, frameWidth);
			camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, frameHeight);
		}
	}

	public int getNumberOfCapturedFrames() {
		return numberOfCapturedFrames;
	}

	private Mat resizeImage(Mat frame, int newWidth, int newHeigth) {
		System.out.print("old size: " + frame.size());
		Size size = new Size(newWidth, newHeigth);
		Imgproc.resize(frame, frame, size);
		System.out.println(" new size: " + frame.size());
		return frame;
	}

	private Mat convertToGray(Mat frame) {
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2GRAY);
		return frame;
	}

	private Mat rotateImage(Mat frame) {
		Core.rotate(frame, frame, Core.ROTATE_180);
		return frame;
	}

	public void sendFrame() {
		if (frame != null && frame.width() > 0 && frame.height() > 0) {
			System.out.println("start: " + System.currentTimeMillis());
			Mat frameCopy = frame.clone();
			System.out.println("clone: " + System.currentTimeMillis());
			if (needToRotate) {
				rotateImage(frameCopy);
				System.out.println("rotate: " + System.currentTimeMillis());
			}
			detectFace(frameCopy);
			System.out.println("detect face: " + System.currentTimeMillis());
			resizeImage(frameCopy, 320 ,240);
			System.out.println("resize: " + System.currentTimeMillis());
			convertToGray(frameCopy);
			System.out.println("convert to gray: " + System.currentTimeMillis());
			numberOfCapturedFrames++;
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", frameCopy, mob);
			System.out.println("convert to jpg: " + System.currentTimeMillis());
			byte[] imageByteArray = mob.toArray();
			connection.sendImage(imageByteArray);
			System.out.println();
		} else {
			System.out.println("Empty frame");
		}
	}

	private Mat detectFace(Mat frame) {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(grayFrame, grayFrame);
		if (this.absoluteFaceSize == 0) {
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0) {
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
		return frame;
	}

	private void stop() {
		if (timer != null && !timer.isShutdown()) {
			try {
				timer.shutdown();
				timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		if (camera.isOpened()) {
			camera.release();
		}
	}
}