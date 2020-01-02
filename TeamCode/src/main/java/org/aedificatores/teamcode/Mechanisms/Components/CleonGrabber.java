package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class CleonGrabber {
    private Servo pusherServo;
    private Servo grabberServo;
    private Servo rotationServo;
    // private CRServo extension;

    private static final String HARDWARE_MAP_NAME_PREFIX = "grab";
    private static final String HARDWARE_MAP_NAME_PUSHER = "pushb";
    private static final String HARDWARE_MAP_NAME_ROTATION = "grotate";
    private static final String HARDWARE_MAP_NAME_EXTENSION = "extend";
    private static final double GRABBER_CLOSED = 0;
    private static final double GRABBER_OPEN = 0;
    private static final double PUSHER_RETRACTED = 0;
    private static final double PUSHER_NORMAL = 0;
    private static final double ROTATION_FLIPPED = 0.85;

    public CleonGrabber(HardwareMap map) {
        pusherServo = map.servo.get(HARDWARE_MAP_NAME_PREFIX + HARDWARE_MAP_NAME_PUSHER);
        grabberServo = map.servo.get(HARDWARE_MAP_NAME_PREFIX);
        rotationServo = map.servo.get(HARDWARE_MAP_NAME_ROTATION);
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

    public void retractPusher() {
        setServoPosition(pusherServo, PUSHER_RETRACTED);
    }

    public void returnPusher() {
        setServoPosition(pusherServo, PUSHER_NORMAL);
    }

    public void rotateGrabber(double rotation) {
        setServoPosition(rotationServo, rotation);
    }

    public void flipGrabber() {
        setServoPosition(rotationServo, ROTATION_FLIPPED);
    }
}
