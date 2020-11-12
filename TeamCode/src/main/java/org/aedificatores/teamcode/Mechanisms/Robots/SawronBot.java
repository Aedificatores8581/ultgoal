package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mecanum;
import org.aedificatores.teamcode.Mechanisms.OdometryWheels;
import org.aedificatores.teamcode.Universal.Math.Pose;

public class SawronBot {

    public Mecanum drivetrain;

    public SawronBot(HardwareMap map) {
        drivetrain = new Mecanum(map);
    }

}
