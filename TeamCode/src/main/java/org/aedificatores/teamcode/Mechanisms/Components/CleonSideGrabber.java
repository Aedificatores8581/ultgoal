package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
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
    Servo rotateServo;


    public CleonSideGrabber(HardwareMap map, String grabMapName, String rotateMapName, double upPosition, double downPosition, double openGrabberThresh, double grabbedPosition, double releasedPosition) {
        this.grabMapName = grabMapName;
        this.rotateMapName = rotateMapName;
        this.upPosition = upPosition;
        this.downPosition = downPosition;
        this.openGrabberThresh = openGrabberThresh;
        this.grabbedPosition = grabbedPosition;
        this.releasedPosition = releasedPosition;

        grabberServo = map.servo.get(this.grabMapName);
        rotateServo = map.servo.get(this.rotateMapName);
    }

    public void init() {
        closeGrabber();
        moveUp();
    }

    public boolean moveDownAndRelease() {
        rotateServo.setPosition(rotateServo.getPosition() + POSITION_SERVO_INC);
        if (rotateServo.getPosition() > openGrabberThresh) {
            grabberServo.setPosition(grabbedPosition);
        }
        return rotateServo.getPosition() >= downPosition;
    }

    public void openGrabber() {
        grabberServo.setPosition(releasedPosition);
    }

    public void closeGrabber() {
        grabberServo.setPosition(grabbedPosition);
    }

    public void moveUp() {
        rotateServo.setPosition(upPosition);
    }

    public void moveDown() {
        rotateServo.setPosition(downPosition);
    }
}
