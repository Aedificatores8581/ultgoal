package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Components.Sensors.TouchSensor;

public class CleonDispenser {

    private CRServo extensionMotor;
    private TouchSensor limitSwitch;
    public CleonClaw claw;

    public void init(HardwareMap map){
        extensionMotor = map.crservo.get("extensionservo");
        limitSwitch.init(map, "frontextension");

    }
}
