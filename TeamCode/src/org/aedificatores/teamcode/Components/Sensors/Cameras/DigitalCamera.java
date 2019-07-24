package org.aedificatores.teamcode.Components.Sensors.Cameras;

import org.aedificatores.teamcode.Universal.Math.Pose3;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Size;


public class DigitalCamera {
    public CameraSensor cameraSensor;
    public double focalLength, x, y, z, xAng, yAng, zAng = 0;

    public class CameraSensor {
		public static final double WIDTH, HEIGHT;

        public CameraSensor(double width, double height) {
            this.WIDTH = width;
            this.HEIGHT = height;
        }

        public CameraSensor(double pixelSize, double numPixelsX, double numPixelsY){
            this.WIDTH = pixelSize * numPixelsX;
            this.HEIGHT = pixelSize * numPixelsY;
        }

    }

    public DigitalCamera(double focalLength, double pixelSize, double resolutionX, double resolutionY) {
        this.focalLength = focalLength;
        cameraSensor = new CameraSensor(pixelSize, resolutionX, resolutionY);
    }

	public DigitalCamera(double focalLength, double pixelSize, double resolutionX, double resolutionY, double mag) {
		this.focalLength = focalLength;
		cameraSensor = new CameraSensor(pixelSize, resolutionX * mag, resolutionY * mag)
	}

    public DigitalCamera(double focalLength, double width, double height) {
        this.focalLength = focalLength;
        cameraSensor = new CameraSensor(width, height);
    }

    public void setLocation(Point3 location) {
        x = location.x;
        y = location.y;
        z = location.z;
    }

    public void setLocation(DigitalCamera aCamera) {
        setLocation(new Point3(aCamera.x, aCamera.y, aCamera.z));
    }

    public void setOrientation(Point3 orientation) {
        xAng = orientation.x;
        yAng = orientation.y;
        zAng = orientation.z;
    }

    public void setOrientation(DigitalCamera aCamera) {
        setOrientation(new Point3(aCamera.xAng, aCamera.yAng, aCamera.zAng));
    }

    public Point getObjectLocation(Point pointOnImage, Size imageSize, double objectHeight) {
        Vector2 temp = new Vector2(-pointOnImage.x, pointOnImage.y);
        double width = Math.max(imageSize.height, imageSize.width);
        double height = Math.min(imageSize.height, imageSize.width);

        double vertAng = (height / 2) / height * horizontalAngleOfView();
        double horiAng = (width / 2) / width * verticalAngleOfView();

        double newY = (z - objectHeight / 2) / Math.tan(-vertAng + xAng);
        double newX = newY * Math.tan(horiAng);
        newY *= -1;
        return new Point(newX, newY);
    }

    public void updateLocation(double xChange, double yChange, double zChange) {
        x += xChange;
        y += yChange;
        z += zChange;
    }

    public void updateLocation(Point3 differentialPosition) {
        updateLocation(differentialPosition.x, differentialPosition.y, differentialPosition.z);
    }

    public void updateOrientation(double xChange, double yChange, double zChange) {
        xAng += xChange;
        yAng += yChange;
        zAng += zChange;
        normalizeAngles();
    }

    public void updateOrientation(Point3 differentialOrientation) {
        updateOrientation(differentialOrientation.x, differentialOrientation.y, differentialOrientation.z);
    }

    public double horizontalAngleOfView(double widthRatio) {
        return 2 * Math.atan2(cameraSensor.width * widthRatio, 2 * focalLength);
    }

    public double horizontalAngleOfView() {
        return horizontalAngleOfView(1);
    }

    public double verticalAngleOfView(double heightRatio) {
        return 2 * Math.atan2(cameraSensor.height * heightRatio, 2 * focalLength);
    }

    public double verticalAngleOfView() {
        return verticalAngleOfView(1);
    }

    public void normalizeAngles() {
        UniversalFunctions.normalizeAngleRadians(xAng);
        UniversalFunctions.normalizeAngleRadians(yAng);
        UniversalFunctions.normalizeAngleRadians(zAng);
    }
}
