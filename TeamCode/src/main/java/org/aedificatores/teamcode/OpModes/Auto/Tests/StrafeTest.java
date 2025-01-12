package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.GandalfMecanum;
import org.aedificatores.teamcode.Mechanisms.Drivetrains.SawronMecanum;
import org.aedificatores.teamcode.Universal.OpModeGroups;

/*
 * This is a simple routine to test translational drive capabilities.
 */
@Config
@Autonomous(group = OpModeGroups.UNIVERSAL)
public class StrafeTest extends LinearOpMode {
    public static double DISTANCE = 48; // in

    @Override
    public void runOpMode() throws InterruptedException {
        GandalfMecanum drive = new GandalfMecanum(hardwareMap);

        Trajectory trajectory = drive.trajectoryBuilder(new Pose2d())
                .strafeRight(DISTANCE)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        drive.followTrajectory(trajectory);

        Pose2d poseEstimate = drive.getPoseEstimate();
        telemetry.addData("finalX", poseEstimate.getX());
        telemetry.addData("finalY", poseEstimate.getY());
        telemetry.addData("finalHeading", poseEstimate.getHeading());
        telemetry.update();

        while (!isStopRequested() && opModeIsActive()) ;
    }
}