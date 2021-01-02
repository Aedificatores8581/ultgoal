package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.ShooterSubsystem;
import org.aedificatores.teamcode.Mechanisms.Components.WobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mecanum;
import org.aedificatores.teamcode.Mechanisms.OdometryWheels;
import org.aedificatores.teamcode.Universal.Math.Pose;

public class SawronBot {

    public Mecanum drivetrain;
    public WobbleGrabber wobbleGrabber;
    public ShooterSubsystem shooter;

    public SawronBot(HardwareMap map) {
        drivetrain = new Mecanum(map);
        wobbleGrabber = new WobbleGrabber(map);
        shooter = new ShooterSubsystem(map);
    }

    public void update() {
        drivetrain.update();
        wobbleGrabber.update();
        shooter.update();
    }

}
