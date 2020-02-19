package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class CleonSideGrabber {
    enum AutoGetBlockState{
        REACH_DOWN,
        GRAB,
        HOLD_BLOCK_POS
    }

    private String grabMapName;
    private String rotateMapName;

    private double upPosition;
    private double downPosition;
    private double holdPosition;
    private double openGrabberThresh;

    private double grabbedPosition;
    private double releasedPosition;

    private final double POSITION_SERVO_INC = .02;

    private AutoGetBlockState autoGetBlockState;
    private long resetTime;
    private long currentTime;
    public Servo grabberServo;
    public Servo rotateServo;


    public CleonSideGrabber(HardwareMap map, String grabMapName, String rotateMapName, double upPosition, double downPosition, double openGrabberThresh, double holdPosition, double grabbedPosition, double releasedPosition) {
        this.grabMapName = grabMapName;
        this.rotateMapName = rotateMapName;
        this.upPosition = upPosition;
        this.downPosition = downPosition;
        this.openGrabberThresh = openGrabberThresh;
        this.holdPosition = holdPosition;
        this.grabbedPosition = grabbedPosition;
        this.releasedPosition = releasedPosition;

        grabberServo = map.servo.get(this.grabMapName);
        rotateServo = map.servo.get(this.rotateMapName);
        autoGetBlockState = AutoGetBlockState.REACH_DOWN;

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
        closeGrabber();
        moveUp();
    }

    public boolean moveDownAndRelease() {
        rotateServo.setPosition(rotateServo.getPosition() - POSITION_SERVO_INC);
        if (rotateServo.getPosition() < openGrabberThresh) {
            openGrabber();
        }
        return rotateServo.getPosition() <= downPosition;
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

    public void holdBlockPos() {
        rotateServo.setPosition(holdPosition);
    }

    public boolean autoGetBlock() {
        switch (autoGetBlockState) {
            case REACH_DOWN:
                if (moveDownAndRelease()){
                    autoGetBlockState = AutoGetBlockState.GRAB;
                    resetTimer();
                }
                break;
            case GRAB:
                closeGrabber();
                if (currentTime > .3) {
                    autoGetBlockState = AutoGetBlockState.HOLD_BLOCK_POS;
                    resetTimer();
                }
                break;
            case HOLD_BLOCK_POS:
                holdBlockPos();
                if (currentTime > .3) {
                    autoGetBlockState = AutoGetBlockState.REACH_DOWN;
                    resetTimer();
                    return true;
                }
                break;
        }
        updateTimer();
        return false;
    }
}
