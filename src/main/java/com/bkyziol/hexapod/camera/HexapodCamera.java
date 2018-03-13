package com.bkyziol.hexapod.camera;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

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

import com.bkyziol.hexapod.mqtt.ConnectionRuntimeException;
import com.bkyziol.hexapod.mqtt.HexapodConnection;


import static com.bkyziol.hexapod.utils.Constants.*;

public class HexapodCamera {

	private volatile Mat frame  = new Mat();
	private volatile int numberOfCapturedFrames = 0;  //TODO REMOVE
	private volatile int numberOfSentFrames = 0;  //TODO REMOVE
	private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	private int frameWidth = FRAME_WIDTH;
	private int frameHeight = FRAME_HEIGTH;
	private VideoCapture camera;

	private CascadeClassifier faceCascade  = new CascadeClassifier();
	private int absoluteFaceSize = 0;

	private final boolean needToRotate;

	private final Thread openCameraThread = openCameraThread();

	static {
		System.load(new File(OPENCV_LIB_FILE).getAbsolutePath());
	}

	public HexapodCamera() {
		this.faceCascade.load(HAARCASCADES_PATH + "/haarcascade_frontalface_alt.xml");
		if (System.getProperty("os.arch").equals("arm")) {
			needToRotate = true;
		} else {
			needToRotate = false;
		}
	}

	public void open() {
		if (!openCameraThread.isAlive()) {
			System.out.println("Opening camera");
			openCameraThread.start();
		} else {
			System.out.println("Camera is already opening");
		}
	}

	public void start() throws CameraRuntimeException {
		if (camera != null && camera.isOpened()) {
			Runnable frameGrabber = new Runnable() {
				@Override
				public void run() {
					camera.read(frame);
					numberOfCapturedFrames++;
				}
			};
			timer.scheduleAtFixedRate(frameGrabber, 0, 50, TimeUnit.MILLISECONDS);
		} else {
			System.out.println("Camera is off");
			open();
			throw new CameraRuntimeException("Camera is off");
		}
	}

	public void captureAndSend() throws CameraRuntimeException {
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
			numberOfSentFrames++;
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", newFrame, mob);
			System.out.println("jpg codec: " + System.currentTimeMillis());
			byte[] imageByteArray = mob.toArray();
//			connection.sendImage(imageByteArray);
			saveToFile(imageByteArray);
			System.out.println("summary: " + (System.currentTimeMillis() - time) + " / " + newFrame.size() + " / " + imageByteArray.length);
			System.out.println();
		} else {
			System.out.println("Camera is off");
			throw new CameraRuntimeException("Camera is off");
		}
	}

	private void saveToFile(byte[] data) {
		try {
			String string = new SimpleDateFormat("HH-mm-ss-SSS").format(System.currentTimeMillis());
			File outputfile = new File(string + ".jpg");
			InputStream in = new ByteArrayInputStream(data);
			BufferedImage bufferedImage = ImageIO.read(in);
			ImageIO.write(bufferedImage, "jpg", outputfile);
		} catch (IOException e) {
			System.out.println("save to file: error");
		}
		System.out.println("save to file: " + System.currentTimeMillis());
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

	public int getNumberOfSentFrames() {
		return numberOfSentFrames;
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

	public void sendFrame() throws CameraRuntimeException {
		if (frame != null && frame.width() > 0 && frame.height() > 0) {
			long time = System.currentTimeMillis();
			System.out.println("start: " + System.currentTimeMillis());
			Mat frameCopy = frame.clone();
			System.out.println("clone: " + System.currentTimeMillis());
			if (needToRotate) {
				rotateImage(frameCopy);
				System.out.println("rotate: " + System.currentTimeMillis());
			}
			detectFace(frameCopy);
			System.out.println("detect face: " + System.currentTimeMillis());
			resizeImage(frameCopy, 320, 240);
			System.out.println("resize: " + System.currentTimeMillis());
//			convertToGray(frameCopy);
//			System.out.println("convert to gray: " + System.currentTimeMillis());
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", frameCopy, mob);
			System.out.println("convert to jpg: " + System.currentTimeMillis());
			byte[] imageByteArray = mob.toArray();
//			saveToFile(imageByteArray);
//			saveToFile(frameCopy);
			numberOfSentFrames++;
			HexapodConnection.sendImage(imageByteArray);
			System.out.println("summary: " + (System.currentTimeMillis() - time) + " / " + frameCopy.size() + " / " + imageByteArray.length);
			System.out.println();
		} else {
			System.out.println("Empty frame");
			throw new CameraRuntimeException("Frame is empty");
		}
	}

	private void saveToFile(Mat frame) {
		String string = new SimpleDateFormat("HH-mm-ss-SSS").format(System.currentTimeMillis());
		Imgcodecs.imwrite(string + ".jpg", frame);
		System.out.println("save to file: " + System.currentTimeMillis());
	}

	private Mat detectFace(Mat frame) {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(grayFrame, grayFrame);
		if (this.absoluteFaceSize == 0) {
			int height = grayFrame.rows();
			if (Math.round(height * 0.1f) > 0) {
				this.absoluteFaceSize = Math.round(height * 0.1f);
			}
		}
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
		return frame;
	}

	public void stop() throws CameraRuntimeException {
		if (timer != null && !timer.isShutdown()) {
			try {
				timer.shutdown();
				timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				throw new CameraRuntimeException("Exception in stopping the frame capture.", e);
			}
		}
	}

	public void close() throws CameraRuntimeException {
		try {
			stop();
			if (camera.isOpened()) {
				camera.release();
			}
		} catch (CameraRuntimeException e) {
			throw new CameraRuntimeException("Exception in stopping the frame capture, trying to release the camera now... ", e);
		}
	}

	private Thread openCameraThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				while (camera == null || !camera.isOpened()) {
					System.out.println("OpenCV version:" + Core.VERSION);
					camera = new VideoCapture(0);
					camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, frameWidth);
					camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, frameHeight);
				}
			}
		});
	}
}