package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.Servo;

public class CleonSideGrabber {
    private String grabMapName;
    private String rotateMapName;

    private double upPosition;
    private double downPosition;
    private double openGrabberThresh; // TODO: Better variable name

    private double grabbedPosition;
    private double releasedPosition;

    private final double POSITION_SERVO_INC = .02;

    Servo grabberServo;
    Servo positionServo;


    public CleonSideGrabber(String grabMapName, String rotateMapName, double upPosition, double downPosition, double openGrabberThresh, double grabbedPosition, double releasedPosition) {
        this.grabMapName = grabMapName;
        this.rotateMapName = rotateMapName;
        this.upPosition = upPosition;
        this.downPosition = downPosition;
        this.openGrabberThresh = openGrabberThresh;
        this.grabbedPosition = grabbedPosition;
        this.releasedPosition = releasedPosition;
    }

    public void init() {
        closeGrabber();
        moveUp();
    }

    public boolean moveDown() {
        positionServo.setPosition(positionServo.getPosition() + POSITION_SERVO_INC);
        if (positionServo.getPosition() > openGrabberThresh) {
            grabberServo.setPosition(grabbedPosition);
        }
        return positionServo.getPosition() >= downPosition;
    }

    public void openGrabber() {
        grabberServo.setPosition(releasedPosition);
    }

    public void closeGrabber() {
        grabberServo.setPosition(grabbedPosition);
    }

    public void moveUp() {

    }

    public void moveDownNoGrabMovement() {
        positionServo.setPosition(downPosition);
    }
}
