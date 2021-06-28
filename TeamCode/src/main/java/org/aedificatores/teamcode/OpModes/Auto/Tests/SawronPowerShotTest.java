package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.SawronDriveConstants;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Autonomous(name = "SawronPowerShotTest",group = OpModeGroups.SAWRON)
public class SawronPowerShotTest extends OpMode {
    final Pose2d START_POSE = new Pose2d(6 - 17.5/2.0, -(72 - 18.0/2.0), 0);
    final Vector2d FIRST_SHOT = new Vector2d(3 - 17.5/2.0, -18);
    final Vector2d SECOND_SHOT = new Vector2d(4 - 17.5/2.0, -12);
    final Vector2d THIRD_SHOT = new Vector2d(4 - 17.5/2.0, 0);

    Trajectory trajShoot;

    MinVelocityConstraint slowVelConstraints = new MinVelocityConstraint(
            Arrays.asList(
                new AngularVelocityConstraint(5),
                new MecanumVelocityConstraint(5, SawronDriveConstants.TRACK_WIDTH)
            )
    );
    MinVelocityConstraint medVelConstraints = new MinVelocityConstraint(
            Arrays.asList(
                    new AngularVelocityConstraint(10),
                    new MecanumVelocityConstraint(10, SawronDriveConstants.TRACK_WIDTH)
            )
    );

    SawronBot bot;

    @Override
    public void init() {
        bot = new SawronBot(hardwareMap, true);
        bot.drivetrain.setPoseEstimate(START_POSE);
    }

    public void start() {
        trajShoot = bot.drivetrain.trajectoryBuilder(START_POSE)
                .splineToConstantHeading(FIRST_SHOT, Math.PI/2)
                .addDisplacementMarker(() -> bot.shooter.queueAdvance())
                .splineToConstantHeading(SECOND_SHOT, Math.PI/2, slowVelConstraints, new ProfileAccelerationConstraint(SawronDriveConstants.MAX_ACCEL))
                .addDisplacementMarker(() -> bot.shooter.queueAdvance())
                .splineToConstantHeading(THIRD_SHOT, Math.PI/2, medVelConstraints, new ProfileAccelerationConstraint(SawronDriveConstants.MAX_ACCEL))
                .addDisplacementMarker(() -> bot.shooter.queueAdvance())
                .build();
        bot.drivetrain.followTrajectoryAsync(trajShoot);
        bot.shooter.setSpeed(3900);
        bot.shooter.runShooter();
        bot.shooter.setLiftPosShootTopRing();
    }

    @Override
    public void loop() {
        bot.update();
    }
}
