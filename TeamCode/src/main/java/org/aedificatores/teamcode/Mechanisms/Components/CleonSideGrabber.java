package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class CleonSideGrabber {

    private String grabMapName;
    private String rotateMapName;

    private double upPosition;
    private double downPushPosition;
    private double downGrabPosition;
    private double holdPosition;

    private double grabbedPosition;
    private double releasedPosition;

    private final double POSITION_SERVO_INC = .02;

    private long resetTime;
    private long currentTime;
    public Servo grabberServo;
    public Servo rotateServo;

    public CleonSideGrabber(HardwareMap map,
                            String grabMapName,
                            String rotateMapName,
                            double upPosition,
                            double downPushPosition,
                            double downGrabPosition,
                            double holdPosition,
                            double grabbedPosition,
                            double releasedPosition) {
        this.grabMapName = grabMapName;
        this.rotateMapName = rotateMapName;
        this.upPosition = upPosition;
        this.downPushPosition = downPushPosition;
        this.downGrabPosition = downGrabPosition;
        this.holdPosition = holdPosition;
        this.grabbedPosition = grabbedPosition;
        this.releasedPosition = releasedPosition;

        grabberServo = map.servo.get(this.grabMapName);
        rotateServo = map.servo.get(this.rotateMapName);

        resetTime = System.currentTimeMillis();
        currentTime = 0;
    }

    public void resetTimer() {
        resetTime = System.currentTimeMillis();
        currentTime = 0;
    }

    public void updateTimer() {
        currentTime = System.currentTimeMillis() - resetTime;
    }

    public void init() {
        moveDownPush();
        openGrabber();
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

    public void moveDownPush() {
        rotateServo.setPosition(downPushPosition);
    }
    public void moveDownGrab() {
        rotateServo.setPosition(downGrabPosition);
    }
    public void holdBlockPos() {
        rotateServo.setPosition(holdPosition);
    }
}
