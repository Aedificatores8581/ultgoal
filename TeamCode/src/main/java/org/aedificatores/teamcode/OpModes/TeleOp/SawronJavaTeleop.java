package org.aedificatores.teamcode.OpModes.TeleOp;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.profile.SimpleMotionConstraints;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryConstraints;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal.SawronWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.jetbrains.annotations.NotNull;

@TeleOp(name = "SawronJavaTeleop", group = OpModeGroups.SAWRON)
public class SawronJavaTeleop extends OpMode {
    SawronBot bot;

    // Power Shot Related Positions and Contstraints
    final Pose2d START_POSE = new Pose2d(6 - 17.5/2.0, -(72 - 17.0/2.0), 0);
    final Vector2d FIRST_SHOT = new Vector2d(3 - 17.5/2.0, -18);
    final Vector2d SECOND_SHOT = new Vector2d(2 - 17.5/2.0, -12);
    final Vector2d THIRD_SHOT = new Vector2d(1 - 17.5/2.0, 0);

    Trajectory trajShoot;

    TrajectoryConstraints slowConstraints = new TrajectoryConstraints() {
        @NotNull
        @Override
        public SimpleMotionConstraints get(double v, @NotNull Pose2d pose2d, @NotNull Pose2d pose2d1, @NotNull Pose2d pose2d2) {
            return new SimpleMotionConstraints(10,10);
        }
    };
    TrajectoryConstraints medConstraints = new TrajectoryConstraints() {
        @NotNull
        @Override
        public SimpleMotionConstraints get(double v, @NotNull Pose2d pose2d, @NotNull Pose2d pose2d1, @NotNull Pose2d pose2d2) {
            return new SimpleMotionConstraints(10,10);
        }
    };

    Gamepad prev1 = new Gamepad(), prev2 = new Gamepad();

    enum DriveMode {
        TRIGGER_BASED,
        STICK_BASED,
    }

    enum MovementMode {
        TELEOP,
        POWER_SHOTS
    }

    DriveMode driveMode;
    MovementMode movementMode = MovementMode.TELEOP;

    @Override
    public void init() {
        bot = new SawronBot(hardwareMap, false);
        bot.wobbleGrabber.setMode(SawronWobbleGrabber.Mode.TELEOP);
        driveMode = DriveMode.TRIGGER_BASED;
    }

    @Override
    public void init_loop() {
        if (gamepad1.a) {
            driveMode = DriveMode.TRIGGER_BASED;
        }
        if (gamepad1.b) {
            driveMode = DriveMode.STICK_BASED;
        }

        telemetry.addLine("Press A for Trigger based Driving (i.e. Gwen Mode)");
        telemetry.addLine("Press B for Stick based Driving (i.e. Non-Gwen Mode)");
        telemetry.addData("Current Mode: ", driveMode);
    }

    @Override
    public void start() {
        try {
            prev1.copy(gamepad1);
            prev2.copy(gamepad2);
        } catch (RobotCoreException e) {
            telemetry.addLine("Tried to Copy gamepad.");
            telemetry.addLine(e.getMessage());
            requestOpModeStop();
        }

        bot.shooter.runShooter();
    }

    @Override
    public void loop() {
        switch (movementMode) {
            case TELEOP:
                if (driveMode == DriveMode.TRIGGER_BASED) {
                    bot.drivetrain.setWeightedDrivePower(
                            new Pose2d(
                                    -(gamepad1.left_trigger - gamepad1.right_trigger),
                                    -gamepad1.left_stick_x,
                                    -gamepad1.right_stick_x
                            )
                    );
                } else {
                    bot.drivetrain.setWeightedDrivePower(
                            new Pose2d(
                                    -gamepad1.left_stick_y,
                                    -gamepad1.left_stick_x,
                                    -gamepad1.right_stick_x
                            )
                    );
                }

                if (gamepad1.x && !prev1.x) {
                    bot.shooter.toggleShooter();
                }

                if (gamepad1.a && !prev1.a) {
                    bot.shooter.advance();
                }

                if (gamepad1.left_bumper && !prev1.left_bumper) {
                    bot.shooter.toggleOuttake();
                } else if (gamepad1.right_bumper && !prev1.right_bumper) {
                    bot.shooter.toggleIntake();
                }

                try {
                    prev1.copy(gamepad1);
                } catch (RobotCoreException e) {
                    telemetry.addLine("Tried to Copy gamepad 1.");
                    telemetry.addLine(e.getMessage());
                    requestOpModeStop();
                }

                if (gamepad1.y && gamepad1.b && false) {
                    bot.drivetrain.setPoseEstimate(START_POSE);
                    movementMode = MovementMode.POWER_SHOTS;
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
                    bot.shooter.intakeOff();
                }

                if (gamepad2.dpad_down) {
                    bot.shooter.maxLowerLift();
                }

                if (gamepad2.dpad_up) {
                    bot.shooter.maxRaiseLift();
                }

                if (gamepad2.dpad_left || gamepad2.dpad_right) {
                    bot.shooter.kick();
                }

                if (gamepad2.a || gamepad2.left_bumper) {
                    bot.shooter.advance();
                }

                if (gamepad2.y) {
                    bot.shooter.setLiftPosShootTopRing();
                }

                if (gamepad2.b) {
                    bot.shooter.setLiftPosShootMiddleRing();
                }

                bot.wobbleGrabber.setPower(-gamepad2.right_stick_y);

                if (gamepad2.x && !prev2.x) {
                    if (bot.wobbleGrabber.isGrabberClosed()) {
                        bot.wobbleGrabber.openGrabber();
                    } else {
                        bot.wobbleGrabber.closeGrabber();
                    }
                }

                try {
                    prev2.copy(gamepad1);
                } catch (RobotCoreException e) {
                    telemetry.addLine("Tried to Copy gamepad 2.");
                    telemetry.addLine(e.getMessage());
                    requestOpModeStop();
                }
                bot.update();

                break;

            case POWER_SHOTS:
                if ((gamepad1.b && !prev1.b) || !bot.drivetrain.isBusy()) {
                    movementMode = MovementMode.TELEOP;
                    bot.drivetrain.setIdle();
                    bot.drivetrain.setWeightedDrivePower(new Pose2d(0,0,0));
                    bot.shooter.setSpeedMax();
                }
                break;
        }

    }
}
