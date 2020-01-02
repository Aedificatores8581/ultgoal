package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class CleonGrabber {
    private Servo pusherServo;
    private Servo grabberServoFront;
    private Servo grabberServoBack;
    private Servo rotationServo;

    private static final String HARDWARE_MAP_NAME_PREFIX = "grab";
    private static final String HARDWARE_MAP_NAME_PUSHER = "pushb";
    private static final String HARDWARE_MAP_NAME_FRONT = "front";
    private static final String HARDWARE_MAP_NAME_BACK = "back";
    private static final String HARDWARE_MAP_NAME_ROTATION = "grotate";
    private static final double FRONT_CLOSED = 0.3;
    private static final double FRONT_OPEN = 0;
    private static final double BACK_CLOSED = 0.3;
    private static final double BACK_OPEN = 0;
    private static final double PUSHER_UP = 0.00;
    private static final double PUSHER_MIN = 0.45;
    private static final double ROTATION_RESET = 0.50;
    private static final double ROTATION_ALT = 0.85;

    public CleonGrabber(HardwareMap map) {
        pusherServo = map.servo.get(HARDWARE_MAP_NAME_PREFIX + HARDWARE_MAP_NAME_PUSHER);
        grabberServoFront = map.servo.get(HARDWARE_MAP_NAME_PREFIX + HARDWARE_MAP_NAME_FRONT);
        grabberServoBack = map.servo.get(HARDWARE_MAP_NAME_PREFIX + HARDWARE_MAP_NAME_BACK);
        rotationServo = map.servo.get(HARDWARE_MAP_NAME_ROTATION);
    }

    public void setServoPosition(Servo servo, double pos) {
        servo.setPosition(pos);
    }

    public void close() {
        setServoPosition(grabberServoBack, BACK_CLOSED);
        setServoPosition(grabberServoFront, FRONT_CLOSED);
    }

    public void open() {
        setServoPosition(grabberServoBack, BACK_OPEN);
        setServoPosition(grabberServoFront, FRONT_OPEN);
    }

    public void pusherUp() {
        setServoPosition(pusherServo, PUSHER_UP);
    }

    public void pusherDown(double pos) {
        if (pos > PUSHER_MIN) {
            setServoPosition(pusherServo, pos);
        } else {
            setServoPosition(pusherServo, PUSHER_MIN);
        }
    }

    public void pusherDown() {
        setServoPosition(pusherServo, PUSHER_MIN);
    }

    public void rotateBack() {
        setServoPosition(rotationServo, ROTATION_ALT);
    }

    public void rotateFront() {
        setServoPosition(rotationServo, ROTATION_RESET);
    }
}
