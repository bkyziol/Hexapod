package com.bkyziol.hexapod.camera;

import org.opencv.core.Rect;

import com.bkyziol.hexapod.movement.HeadMovement;
import com.bkyziol.hexapod.movement.HeadMovementType;

import static com.bkyziol.hexapod.movement.ServoControllerValues.HEAD_HORIZONTAL;
import static com.bkyziol.hexapod.movement.ServoControllerValues.HEAD_VERTICAL;
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

	public void lookAt() {
		FacePosition closestFace = getClosestFaceCoordinates();
		if (closestFace != null) {
			double leftMargin = closestFace.getX1();
			double rightMargin = FRAME_WIDTH - closestFace.getX2();
			if (leftMargin - 100 > rightMargin) {
				int value = (int) (leftMargin - rightMargin) * 4;
				HEAD_HORIZONTAL.increaseAngle(value);
//				System.out.println("turn right: " + (leftMargin - rightMargin));
//				System.out.println("increaseAngle: " + value);
			} else if (rightMargin - 100 > leftMargin) {
				int value = (int) (rightMargin - leftMargin) * 4;
				HEAD_HORIZONTAL.decreaseAngle(value);
//				System.out.println("turn left: " + (rightMargin - leftMargin));
//				System.out.println("decreaseAngle: " + value);
			} else {
				System.out.println("is centered: " + (rightMargin - leftMargin));
				HeadMovement.setMovement(HeadMovementType.STAND_BY);
			}
		} else {
			System.out.println("no face detected");
		}
	}
}
