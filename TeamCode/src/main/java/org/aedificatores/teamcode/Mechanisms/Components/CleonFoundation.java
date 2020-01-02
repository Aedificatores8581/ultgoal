package org.aedificatores.teamcode.Mechanisms.Components;;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class CleonFoundation {
    private Servo[] servos;

    private static final String HARDWARE_MAP_NAME = "foundation";
    private final double CLOSED_POSITION = -1.0;
    private final double OPEN_POSITION = 1.0;

    public CleonFoundation(HardwareMap map) {
        servos[0] = map.servo.get(HARDWARE_MAP_NAME + "1");
        servos[1] = map.servo.get(HARDWARE_MAP_NAME + "2");
    }

    public void setServoPosition(double pos, int which) { servos[which].setPosition(pos); }

    public void close() {
        setServoPosition(CLOSED_POSITION, 0);
        setServoPosition(CLOSED_POSITION, 1);
    }

    public void open() {
        setServoPosition(OPEN_POSITION, 0);
        setServoPosition(OPEN_POSITION, 1);
    }
}