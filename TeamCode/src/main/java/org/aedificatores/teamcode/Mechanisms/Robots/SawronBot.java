package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.SawronShooterSubsystem;
import org.aedificatores.teamcode.Mechanisms.Components.SawronWobbleGoal.SawronWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Drivetrains.SawronMecanum;

public class SawronBot {

    public SawronMecanum drivetrain;
    public SawronWobbleGrabber wobbleGrabber;
    public SawronShooterSubsystem shooter;

    public SawronBot(HardwareMap map) {
        this(map, false);
    }

    public SawronBot(HardwareMap map, boolean isAuto) {
        drivetrain = new SawronMecanum(map);
        wobbleGrabber = new SawronWobbleGrabber(map);
        shooter = new SawronShooterSubsystem(map, isAuto);
    }

    public void update() {
        drivetrain.update();
        wobbleGrabber.update();
        shooter.update();
    }

}
