package org.aedificatores.teamcode.Mechanisms.Components;

import android.util.Log;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Sensors.MagneticLimitSwitch;

public class CleonGrabber {

    enum GrabberState {
        KICKING,
        GRABBING,
        EXTENDING,
        RELEASING,
        RETRACTING,
        UNKICKING,
        STOP,
    }

    private GrabberState grabberState;

    private Servo pusherServo;
    private Servo grabberServo;
    private Servo rotationServo;
    private CRServo extension;
    private MagneticLimitSwitch extendedSwitch;
    private MagneticLimitSwitch retractedSwitch;

    private static final String HARDWARE_MAP_NAME_EXTEND_SWTICH = "frontextension";
    private static final String HARDWARE_MAP_NAME_RETRACTED_SWTICH = "backextension";
    private static final String HARDWARE_MAP_NAME_GRAB = "grab";
    private static final String HARDWARE_MAP_NAME_PUSHER = "kickerservo";
    private static final String HARDWARE_MAP_NAME_ROTATION = "turndeposit";
    private static final String HARDWARE_MAP_NAME_EXTENSION = "extensionservo";
    private static final double GRABBER_CLOSED = 0.2;
    private static final double GRABBER_OPEN = .65;
    private static final double PUSHER_OPEN = .5;
    private static final double PUSHER_CLOSED = 0;
    private static final double ROTATION_FLIPPED = 0.85;
    private static final double EXTENSION_POWER = .75;

    public CleonGrabber(HardwareMap map) {
        extendedSwitch = new MagneticLimitSwitch();
        retractedSwitch = new MagneticLimitSwitch();
        extendedSwitch.init(map, HARDWARE_MAP_NAME_EXTEND_SWTICH);
        retractedSwitch.init(map, HARDWARE_MAP_NAME_RETRACTED_SWTICH );
        pusherServo = map.servo.get(HARDWARE_MAP_NAME_PUSHER);
        grabberServo = map.servo.get(HARDWARE_MAP_NAME_GRAB);
        rotationServo = map.servo.get(HARDWARE_MAP_NAME_ROTATION);
        extension = map.crservo.get(HARDWARE_MAP_NAME_EXTENSION);
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

        Log.i("extendo", "Extended Switch is " + retractedSwitch.toString());
        if (extendedSwitch.isActive()) {
            extension.setPower(0.0);
            Log.i("extendo", "returning true");
            return true;
        }
        Log.i("extendo", "returning false");
        return false;
    }

    public boolean retract() {
        extension.setPower(-EXTENSION_POWER);

        Log.i("retracto", "Retracted Switch is " + retractedSwitch.toString());
        if (retractedSwitch.isActive()) {
            extension.setPower(0.0);
            Log.i("retracto", "returning true");
            return true;
        }
        Log.i("retracto", "returning false");
        return false;
    }

    public void setGrabberState(GrabberState state) {
        grabberState = state;
    }
}
