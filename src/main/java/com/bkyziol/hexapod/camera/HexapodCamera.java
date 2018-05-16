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

import com.bkyziol.hexapod.Status;
import com.bkyziol.hexapod.utils.Constants;

import static com.bkyziol.hexapod.utils.Constants.*;

public final class HexapodCamera {

	private Mat frame = new Mat();
	private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	private int frameWidth = FRAME_WIDTH;
	private int frameHeight = FRAME_HEIGTH;
	private VideoCapture camera;

	private CascadeClassifier faceCascade  = new CascadeClassifier();
	private int absoluteFaceSize = 0;

	private final boolean needToRotate;

	private final Thread cameraThread = initCameraThread();

	static {
		System.load(new File(Constants.OPENCV_LIB_FILE).getAbsolutePath());
	}

	public HexapodCamera(String opencvLibFile, String haarcascadesPath) {
		this.faceCascade.load(haarcascadesPath + "/haarcascade_frontalface_alt.xml");
		if (System.getProperty("os.arch").equals("arm")) {
			needToRotate = true;
		} else {
			needToRotate = false;
		}
	}

	public void startCapture() throws InterruptedException {
		cameraThread.start();
	}

	public byte[] getCompressedFrame() throws CameraRuntimeException {
		if (frame != null && frame.width() > 0 && frame.height() > 0) {
			Mat frameCopy = frame.clone();
			if (needToRotate) {
				rotateImage(frameCopy);
			}
			CameraSettings cameraStettings = Status.getCameraSettings();
			if (cameraStettings.isFaceDetectionEnabled()) {
				detectFace(frameCopy);
			}
			resizeImage(frameCopy, cameraStettings.getFrameWidth(), cameraStettings.getFrameHeight());
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", frameCopy, mob);
			byte[] imageByteArray = mob.toArray();
			return imageByteArray;
		} else {
			System.out.println("Empty frame");
			throw new CameraRuntimeException("Frame is empty");
		}
	}

	public void stopCapture() throws CameraRuntimeException {
		if (timer != null && !timer.isShutdown()) {
			try {
				timer.shutdown();
				timer.awaitTermination(50, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				throw new CameraRuntimeException("Exception in stopping the frame capture.", e);
			}
		}
		camera.release();
	}

	public boolean isCameraOpen() {
		return (camera != null && camera.isOpened())? true : false;
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
					System.out.println("Camera connected");
				}
				Runnable frameGrabber = new Runnable() {
					@Override
					public void run() {
						camera.read(frame);
					}
				};
				timer.scheduleAtFixedRate(frameGrabber, 0, 50, TimeUnit.MILLISECONDS);
			}
		});
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

	private Mat resizeImage(Mat frame, int newWidth, int newHeigth) {
		System.out.println("resize");
		Size size = new Size(newWidth, newHeigth);
		Imgproc.resize(frame, frame, size);
		return frame;
	}

	private Mat rotateImage(Mat frame) {
		Core.rotate(frame, frame, Core.ROTATE_180);
		return frame;
	}
}