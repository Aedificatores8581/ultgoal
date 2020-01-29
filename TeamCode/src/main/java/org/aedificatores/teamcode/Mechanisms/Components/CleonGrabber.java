package org.aedificatores.teamcode.Mechanisms.Components;

import android.util.Log;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Sensors.MagneticLimitSwitch;

public class CleonGrabber {

    enum ExtendState {
        IDLE,
        RUN_WITHOUT_SWITCH,
        STOP_AT_SWITCH,
    }

    private ExtendState extendState = ExtendState.RUN_WITHOUT_SWITCH;

    private Servo pusherServo;
    private Servo grabberServo;
    private Servo rotationServo;
    private CRServo extension;
    private MagneticLimitSwitch limitSwitch;

    private long currentTime;
    private long resetTime;

    private static final String HARDWARE_MAP_NAME_EXTEND_SWTICH = "frontextension";
    private static final String HARDWARE_MAP_NAME_RETRACTED_SWTICH = "backextension";
    private static final String HARDWARE_MAP_NAME_GRAB = "grab";
    private static final String HARDWARE_MAP_NAME_PUSHER = "kickerservo";
    private static final String HARDWARE_MAP_NAME_ROTATION = "turndeposit";
    private static final String HARDWARE_MAP_NAME_EXTENSION = "extensionservo";
    private static final double GRABBER_CLOSED = 0.8;
    private static final double GRABBER_OPEN = .2;
    private static final double PUSHER_OPEN = .0;
    private static final double PUSHER_CLOSED = .6;
    private static final double ROTATION_FLIPPED = 0.85;
    private static final double EXTENSION_POWER = .75;

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
        setServoPosition(rotationServo, ROTATION_FLIPPED);
    }

    public boolean extend() {
        extension.setPower(EXTENSION_POWER);

        switch (extendState) {
            case IDLE:
                resetTimer();
                extendState = ExtendState.RUN_WITHOUT_SWITCH;
                break;
            case RUN_WITHOUT_SWITCH:
                if (currentTime > 100) {
                    extendState = ExtendState.STOP_AT_SWITCH;
                }
                break;
            case STOP_AT_SWITCH:
                Log.d("extendo", "Extended Switch is " + limitSwitch.toString());
                if (limitSwitch.isActive()) {
                    extension.setPower(0.0);
                    Log.d("extendo", "returning true");
                    extendState = ExtendState.IDLE;
                    return true;
                }
                break;
        }


        Log.d("extendo", "returning false");
        updateTimer();
        return false;
    }

    public boolean retract() {
        extension.setPower(-EXTENSION_POWER);

        switch (extendState) {
            case IDLE:
                resetTimer();
                break;
            case RUN_WITHOUT_SWITCH:
                if (currentTime > 100) {
                    extendState = ExtendState.STOP_AT_SWITCH;
                }
                break;
            case STOP_AT_SWITCH:
                Log.d("retracto", "Extended Switch is " + limitSwitch.toString());
                if (limitSwitch.isActive()) {
                    extension.setPower(0.0);
                    Log.d("retracto", "returning true");
                    extendState = ExtendState.IDLE;
                    return true;
                }
                break;
        }


        Log.d("retracto", "returning false");
        updateTimer();
        return false;
    }

}
