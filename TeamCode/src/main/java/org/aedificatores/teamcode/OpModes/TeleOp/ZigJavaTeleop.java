package org.aedificatores.teamcode.OpModes.TeleOp;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal.WobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;

@TeleOp(name = "ZigJavaTeleop")
public class ZigJavaTeleop extends OpMode {
    SawronBot bot;

    Gamepad prev1 = new Gamepad(), prev2 = new Gamepad();

    enum DriveMode {
        TRIGGER_BASED,
        STICK_BASED,
    }

    DriveMode driveMode;

    @Override
    public void init() {
        bot = new SawronBot(hardwareMap, false);
        bot.wobbleGrabber.setMode(WobbleGrabber.Mode.TELEOP);
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
    }

    @Override
    public void loop() {
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

        if (gamepad1.x) {
            bot.shooter.stopShooter();
        }

        if (gamepad1.a && !prev1.a) {
            if (!bot.shooter.runningShooterMotor()) {
                bot.shooter.runShooter();
            } else {
                bot.shooter.advance();
            }
        }

        if (gamepad1.left_bumper && !prev1.left_bumper) {
            bot.shooter.toggleOuttake();
        } else if (gamepad1.right_bumper && !prev2.right_bumper) {
            bot.shooter.toggleIntake();
        }

        try {
            prev1.copy(gamepad1);
        } catch (RobotCoreException e) {
            telemetry.addLine("Tried to Copy gamepad 1.");
            telemetry.addLine(e.getMessage());
            requestOpModeStop();
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

        bot.wobbleGrabber.setPower(gamepad2.right_stick_y);

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

    }
}
