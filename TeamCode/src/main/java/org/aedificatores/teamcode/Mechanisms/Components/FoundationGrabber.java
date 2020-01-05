package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class FoundationGrabber {
    private double grabbed;
    private double released;

    private Servo servo;

    public FoundationGrabber(HardwareMap map, String name, double grabbed, double released) {
        servo = map.servo.get(name);
        this.grabbed = grabbed;
        this.released = released;
    }

    public void release() {
        servo.setPosition(released);
    }

    public void grab() {
        servo.setPosition(grabbed);
    }
}
