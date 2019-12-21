package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class HummingbirdBlockGrabber {
    public Servo    pitchServo, // Controls the flip up motion of the grabber;
                    rollServo,  // controls how the grabber is angled when grabbing blocks
                    grabber;

    public HummingbirdBlockGrabber(HardwareMap map, String pitchServoName, String rollServoName, String grabServoName) {
        pitchServo = map.servo.get(pitchServoName);
        rollServo = map.servo.get(rollServoName);
        grabber = map.servo.get(grabServoName);
    }
}
