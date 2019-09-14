package org.aedificatores.teamcode.Sensors.Cameras;

import org.aedificatores.teamcode.Components.Sensors.Cameras.DigitalCamera;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import java.lang.Math;

public class MotoG4 {
	public DigitalCamera rearCamera, frontCamera;
	private final static DigitalCamera FRONT_CAMERA_RR2 = new DigitalCamera(3.584 / 2 / Math.tan(14 * Math.PI / 15), 1.4 * 10E-3, 1920, 2560);
	private final static DigitalCamera REAR_CAMERA_RR2 = new DigitalCamera(3.6, 1.2E-3, 3120, 4208);

	public MotoG4() {
		frontCamera = FRONT_CAMERA_RR2;
		rearCamera = REAR_CAMERA_RR2;
	}

	public void setLocation(Point3 rear_location, Point3 front_location) {
		frontCamera.setLocation(front_location);
		rearCamera.setLocation(rear_location);
	}

	public Point3 getLocation() {
		return new Point3(rearCamera.x, rearCamera.y, rearCamera.z);
	}

	public void setOrientation(Point3 orientation) {
		frontCamera.setLocation(orientation);
		rearCamera.setLocation(orientation);
	}
}
