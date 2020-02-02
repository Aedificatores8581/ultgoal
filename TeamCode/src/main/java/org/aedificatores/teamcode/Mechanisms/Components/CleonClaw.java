package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.aedificatores.teamcode.Components.Sensors.TouchSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class CleonClaw {

    private Servo finger;
    private Servo pivot;
    private DistanceSensor stoneDetector;

    private final double ZERO_DEGREE_POSITION = 0;
    private final double NINTY_DEGREE_POSITION = 0.5;
    private final double ONE_HUNDRED_EIGHTY_DEGREE_POSITION = 1;

    private final double FINGER_OPEN_POSITION = 0.2;
    private final double FINGER_CLOSED_POSITION = 0.8;

    private final double DISTANCE_TO_DETECT_STONE = 65;

    public void init(HardwareMap map){
        finger = map.servo.get("grab");
        pivot = map.servo.get("turndeposit");
        stoneDetector = map.get(DistanceSensor.class, "stonedetector");
    }

    public void closeFinger(){
        finger.setPosition(FINGER_CLOSED_POSITION);
    }

    public void openFinger(){
        finger.setPosition(FINGER_OPEN_POSITION);
    }

    public void rotate0Degrees(){
        pivot.setPosition(ZERO_DEGREE_POSITION);
    }

    public void rotate90Degrees(){
        pivot.setPosition(NINTY_DEGREE_POSITION);
    }

    public void rotate180Degrees(){
        pivot.setPosition(ONE_HUNDRED_EIGHTY_DEGREE_POSITION);
    }

    public boolean hasStone (){
        return stoneDetector.getDistance(DistanceUnit.MM) < DISTANCE_TO_DETECT_STONE;
    }
}
