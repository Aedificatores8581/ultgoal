package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class HummingbirdFoundationGrabber {
    public static double GRABBED = 0.0;
    public static double RELEASED = 1.0;

    private Servo servo;

    public HummingbirdFoundationGrabber(HardwareMap map, String name) {
        servo = map.servo.get(name);
    }

    public void release() {
        servo.setPosition(RELEASED);
    }

    public void grab() {
        servo.setPosition(GRABBED);
    }
}
