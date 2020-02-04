package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Components.Sensors.TouchSensor;

public class CleonDispenser {

    private CRServo extensionMotor;
    private TouchSensor limitSwitch;

    public boolean extended = false;
    public boolean retracted = false;

    private final double SPEED = 0.9;

    public void init(HardwareMap map){
        extensionMotor = map.crservo.get("extensionservo");
        limitSwitch.init(map, "frontextension");
    }

    public void extend(){
        if(!limitSwitch.isPressed() || !retracted) {
            retracted = false;
            extended = limitSwitch.isPressed();
        }
        if(extended)
            extensionMotor.setPower(0);
        else
            extensionMotor.setPower(SPEED);
    }
    public void retract(){
        if(!limitSwitch.isPressed() || !extended) {
            extended = false;
            retracted = limitSwitch.isPressed();
        }
        if(retracted)
            extensionMotor.setPower(0);
        else
            extensionMotor.setPower(-SPEED);
    }
}
