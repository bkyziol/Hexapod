package com.bkyziol.hexapod.camera;

import org.opencv.core.Rect;

import com.bkyziol.hexapod.movement.BodyMovement;
import com.bkyziol.hexapod.movement.Head;
import com.bkyziol.hexapod.movement.HeadServo;
import com.bkyziol.hexapod.movement.Status;

import static com.bkyziol.hexapod.utils.Constants.*;

public class FaceTracking {

	private final HexapodCamera camera;

	public FaceTracking(HexapodCamera camera) {
		this.camera = camera;
	}

	private FacePosition getClosestFaceCoordinates() {
		Rect[] facesArray = camera.detectFaces();
		if (facesArray != null && facesArray.length > 0) {
			FacePosition facePosition = new FacePosition();
			double currentBigestSize = 0;
			for (int i = 0; i < facesArray.length; i++) {
				double size = (facesArray[i].br().x - facesArray[i].tl().x)
						* (facesArray[i].br().y - facesArray[i].tl().y);
				if (size > currentBigestSize) {
					currentBigestSize = size;
					facePosition.setX1(facesArray[i].tl().x);
					facePosition.setX2(facesArray[i].br().x);
					facePosition.setY1(facesArray[i].tl().y);
					facePosition.setY2(facesArray[i].br().y);
				}
			}
			return facePosition;
		} else {
			return null;
		}
	}

	public void lookAt() throws InterruptedException {
		FacePosition closestFace = getClosestFaceCoordinates();

		HeadServo horizontalServo = Head.getHorizontalServo();
		HeadServo verticalServo = Head.getVerticalServo();

		if (closestFace != null) {
			Status.setLastFaceDetectedTimestamp(System.currentTimeMillis());
			double leftMargin = closestFace.getX1();
			double rightMargin = FRAME_WIDTH - closestFace.getX2();
			double upMargin = closestFace.getY1();
			double downMargin = FRAME_HEIGTH - closestFace.getY2();

			if (leftMargin - 100 > rightMargin) {
				int value = (int) (leftMargin - rightMargin) * 4;
				horizontalServo.increaseAngle(value);
			} else if (rightMargin - 100 > leftMargin) {
				int value = (int) (rightMargin - leftMargin) * 4;
				horizontalServo.decreaseAngle(value);
			}
			if (upMargin - 100 > downMargin) {
				int value = (int) (upMargin - downMargin) * 4;
				verticalServo.increaseAngle(value);
			} else if (downMargin - 100 > upMargin) {
				int value = (int) (downMargin - upMargin) * 4;
				verticalServo.decreaseAngle(value);
			}
			if (horizontalServo.getCurrent() < horizontalServo.getMin() + 100) {
				BodyMovement.executeMove(BodyMovement.lookLeft);
			}
			if (horizontalServo.getCurrent() > horizontalServo.getMax() - 100) {
				BodyMovement.executeMove(BodyMovement.lookRight);
			}
		} else {
			if (Status.getLastFaceDetectedTimestamp() + 2000 < System.currentTimeMillis()) {
				verticalServo.setValue(5000);
				horizontalServo.setValue(horizontalServo.getCenter());
			}
		}
	}
}
