package org.aedificatores.teamcode.Mechanisms.Components;

import android.util.Log;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Sensors.MagneticLimitSwitch;

public class CleonGrabber {

    public enum ExtendState {
        MOVE,
        STOP
    }

    public ExtendState extendState = ExtendState.MOVE;

    private Servo pusherServo;
    private Servo grabberServo;
    private Servo rotationServo;
    private CRServo extension;
    private MagneticLimitSwitch limitSwitch;

    public boolean isExtended = false;
    public boolean isRetracted = true;
    public boolean extending = false;

    private long currentTime;
    private long resetTime;

    private static final String HARDWARE_MAP_NAME_EXTEND_SWTICH = "frontextension";
    private static final String HARDWARE_MAP_NAME_RETRACTED_SWTICH = "backextension";
    private static final String HARDWARE_MAP_NAME_GRAB = "grab";
    private static final String HARDWARE_MAP_NAME_PUSHER = "kickerservo";
    private static final String HARDWARE_MAP_NAME_ROTATION = "turndeposit";
    private static final String HARDWARE_MAP_NAME_EXTENSION = "extensionservo";
    private static final double GRABBER_CLOSED = 0.15;
    private static final double GRABBER_OPEN = .4;
    private static final double PUSHER_OPEN = .0;
    private static final double PUSHER_CLOSED = .45;
    private static final double ROTATION_FLIPPED_90 = 0.85;
    private static final double ROTATION_FLIPPED_180 = 0.85;
    private static final double ROTATION_NORMAL = 0.2;
    private static final double EXTENSION_POWER = .85;



    public CleonGrabber(HardwareMap map) {
        limitSwitch = new MagneticLimitSwitch();
        limitSwitch.init(map, HARDWARE_MAP_NAME_EXTEND_SWTICH);
        pusherServo = map.servo.get(HARDWARE_MAP_NAME_PUSHER);
        grabberServo = map.servo.get(HARDWARE_MAP_NAME_GRAB);
        rotationServo = map.servo.get(HARDWARE_MAP_NAME_ROTATION);
        extension = map.crservo.get(HARDWARE_MAP_NAME_EXTENSION);
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
        openGrabber();
        openPusher();
    }

    public void setServoPosition(Servo servo, double pos) {
        servo.setPosition(pos);
    }

    public void closeGrabber() {
        setServoPosition(grabberServo, GRABBER_CLOSED);
    }

    public void openGrabber() {
        setServoPosition(grabberServo, GRABBER_OPEN);
    }

    public void openPusher() {
        setServoPosition(pusherServo, PUSHER_OPEN);
    }

    public void closePusher() {
        setServoPosition(pusherServo, PUSHER_CLOSED);
    }

    public void rotateGrabber(double rotation) {
        setServoPosition(rotationServo, rotation);
    }

    public void flipGrabber() {
        rotateGrabber(ROTATION_FLIPPED_90);
    }
    public void unflipGrabber() {
        rotateGrabber(ROTATION_NORMAL);
    }

    public void extend(){
        if(!limitSwitch.isActive() && !extending) {
            extending = true;
            isRetracted = false;
        }
        switch (extendState) {
            case MOVE:
                extension.setPower(0.75);

                if(limitSwitch.isActive() && extending)
                    extendState = ExtendState.STOP;

                break;
            case STOP:
                isExtended = true;
                extension.setPower(0);
                if(!limitSwitch.isActive())
                    extendState = ExtendState.MOVE;
                break;
        }
    }

    public void retract(){
        if(!limitSwitch.isActive() && extending) {
            extending = false;
            isExtended = false;
        }
        switch (extendState) {
            case MOVE:
                extension.setPower(-0.75);

                if(limitSwitch.isActive() && !extending)
                    extendState = ExtendState.STOP;

                break;
            case STOP:
                isRetracted = true;
                extension.setPower(0);
                if(!limitSwitch.isActive())
                    extendState = ExtendState.MOVE;
                break;
        }
    }

    public void orientGrabberToFoundation180(double robotAngleRelativeToFoudnation){
        setServoPosition(grabberServo, ROTATION_FLIPPED_180 - (ROTATION_FLIPPED_180 - ROTATION_NORMAL) * robotAngleRelativeToFoudnation / Math.PI);
    }

    public void orientGrabberRelativeToFoundation90(double robotAngleRelativeToFoundation){
        orientGrabberToFoundation180(robotAngleRelativeToFoundation + Math.PI/2);
    }

}
