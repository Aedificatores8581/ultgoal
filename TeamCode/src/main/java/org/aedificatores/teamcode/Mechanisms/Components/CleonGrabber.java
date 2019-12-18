package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class CleonGrabber {
    private Servo servo;

    private static final String HARDWARE_MAP_NAME = "grab";
    private final double CLOSED_POSITION = -1.0;
    private final double OPEN_POSITION = 1.0;

    public CleonGrabber(HardwareMap map) {
        servo = map.servo.get(HARDWARE_MAP_NAME);
    }

    public CleonGrabber(HardwareMap map, String mapName) {
        servo = map.servo.get(mapName);
    }

    public void setServoPosition(double pos) {
        servo.setPosition(pos);
    }

    public void closeGrabber() {
        setServoPosition(CLOSED_POSITION);
    }

    public void openGrabber() {
        setServoPosition(OPEN_POSITION);
    }

}
