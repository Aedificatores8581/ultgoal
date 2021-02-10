package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.SawronMecanum;
import org.aedificatores.teamcode.Universal.OpModeGroups;


/*
 * This is a simple routine to test turning capabilities.
 */
@Config
@Autonomous(group = OpModeGroups.UNIVERSAL)
public class TurnTest extends LinearOpMode {
    public static double ANGLE = 90; // deg

    @Override
    public void runOpMode() throws InterruptedException {
        SawronMecanum drive = new SawronMecanum(hardwareMap);

        waitForStart();

        if (isStopRequested()) return;

        drive.turn(Math.toRadians(ANGLE));
    }
}