package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.ShooterSubsystem;
import org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal.WobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mecanum;

public class SawronBot {

    public Mecanum drivetrain;
    public WobbleGrabber wobbleGrabber;
    public ShooterSubsystem shooter;

    public SawronBot(HardwareMap map) {
        this(map, false);
    }

    public SawronBot(HardwareMap map, boolean isAuto) {
        drivetrain = new Mecanum(map);
        wobbleGrabber = new WobbleGrabber(map);
        shooter = new ShooterSubsystem(map, isAuto);
    }

    public void update() {
        drivetrain.update();
        wobbleGrabber.update();
        shooter.update();
    }

}
