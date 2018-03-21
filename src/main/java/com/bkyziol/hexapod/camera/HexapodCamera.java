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

public final class HexapodCamera {

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

	private final Thread openCameraThread = initCameraThread();

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

	public void captureAndSendFrame() throws CameraRuntimeException {
		if (camera != null && camera.isOpened()) {
			Mat newFrame = new Mat();
			camera.read(newFrame);
			if (needToRotate) {
				rotateImage(newFrame);
			}
			detectFace(newFrame);
			resizeImage(newFrame, 320 ,240);
			numberOfCapturedFrames++;
			numberOfSentFrames++;
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", newFrame, mob);
			byte[] imageByteArray = mob.toArray();
			HexapodConnection.sendImage(imageByteArray);
		} else {
			System.out.println("Camera is off");
			throw new CameraRuntimeException("Camera is off");
		}
	}

//	private void saveToFile(byte[] data) {
//		try {
//			String string = new SimpleDateFormat("HH-mm-ss-SSS").format(System.currentTimeMillis());
//			File outputfile = new File(string + ".jpg");
//			InputStream in = new ByteArrayInputStream(data);
//			BufferedImage bufferedImage = ImageIO.read(in);
//			ImageIO.write(bufferedImage, "jpg", outputfile);
//		} catch (IOException e) {
//			System.out.println("save to file: error");
//		}
//		System.out.println("save to file: " + System.currentTimeMillis());
//	}

//	private void saveToFile(Mat frame) {
//		String string = new SimpleDateFormat("HH-mm-ss-SSS").format(System.currentTimeMillis());
//		Imgcodecs.imwrite(string + ".jpg", frame);
//		System.out.println("save to file: " + System.currentTimeMillis());
//	}

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
		Size size = new Size(newWidth, newHeigth);
		Imgproc.resize(frame, frame, size);
		return frame;
	}

	private Mat rotateImage(Mat frame) {
		Core.rotate(frame, frame, Core.ROTATE_180);
		return frame;
	}

	public void sendFrame() throws CameraRuntimeException {
		if (frame != null && frame.width() > 0 && frame.height() > 0) {
			Mat frameCopy = frame.clone();
			if (needToRotate) {
				rotateImage(frameCopy);
			}
			detectFace(frameCopy);
			resizeImage(frameCopy, 320, 240);
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", frameCopy, mob);
			byte[] imageByteArray = mob.toArray();
			numberOfSentFrames++;
			HexapodConnection.sendImage(imageByteArray);
		} else {
			System.out.println("Empty frame");
			throw new CameraRuntimeException("Frame is empty");
		}
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

	private final Thread initCameraThread() {
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