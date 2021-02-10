package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.profile.SimpleMotionConstraints;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.jetbrains.annotations.NotNull;

@Autonomous(name = "SawronPowerShotTest",group = OpModeGroups.SAWRON)
public class SawronPowerShotTest extends OpMode {
    final Pose2d START_POSE = new Pose2d(6 - 17.5/2.0, -(72 - 18.0/2.0), 0);
    final Vector2d FIRST_SHOT = new Vector2d(3 - 17.5/2.0, -18);
    final Vector2d SECOND_SHOT = new Vector2d(4 - 17.5/2.0, -12);
    final Vector2d THIRD_SHOT = new Vector2d(4 - 17.5/2.0, 0);

    Trajectory trajShoot;

    TrajectoryConstraints slowConstraints = new TrajectoryConstraints() {
        @NotNull
        @Override
        public SimpleMotionConstraints get(double v, @NotNull Pose2d pose2d, @NotNull Pose2d pose2d1, @NotNull Pose2d pose2d2) {
            return new SimpleMotionConstraints(5,5);
        }
    };
    TrajectoryConstraints medConstraints = new TrajectoryConstraints() {
        @NotNull
        @Override
        public SimpleMotionConstraints get(double v, @NotNull Pose2d pose2d, @NotNull Pose2d pose2d1, @NotNull Pose2d pose2d2) {
            return new SimpleMotionConstraints(10,10);
        }
    };

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
                .splineToConstantHeading(SECOND_SHOT, Math.PI/2, slowConstraints)
                .addDisplacementMarker(() -> bot.shooter.queueAdvance())
                .splineToConstantHeading(THIRD_SHOT, Math.PI/2, medConstraints)
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
