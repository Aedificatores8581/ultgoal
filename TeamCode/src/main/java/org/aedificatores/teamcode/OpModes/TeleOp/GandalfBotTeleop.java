package org.aedificatores.teamcode.OpModes.TeleOp;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBot;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@TeleOp(group = OpModeGroups.GANDALF)
public class GandalfBotTeleop extends OpMode {
    GandalfBot bot;

    Gamepad prev1 = new Gamepad(), prev2 = new Gamepad();

    enum DriveMode {
        TRIGGER_BASED,
        STICK_BASED,
    }

    public static final double SHOOTER_SPEED = 239.6;


    DriveMode driveMode;

    @Override
    public void init() {
        bot = new GandalfBot(hardwareMap, false);
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

    public void start() {
        try {
            prev1.copy(gamepad1);
            prev2.copy(gamepad2);
        } catch (RobotCoreException e) {
            telemetry.addLine("Tried to Copy gamepad.");
            telemetry.addLine(e.getMessage());
            requestOpModeStop();
        }

        bot.shooter.setSpeed(SHOOTER_SPEED);
    }

    @Override
    public void loop() {
        telemetry.addLine("GAMEPAD 1:");
        telemetry.addLine("----------------------");
        if (driveMode == DriveMode.TRIGGER_BASED) {
            bot.drivetrain.setWeightedDrivePower(
                    new Pose2d(
                            -(gamepad1.left_trigger - gamepad1.right_trigger),
                            -gamepad1.left_stick_x,
                            -gamepad1.right_stick_x
                    )
            );
            telemetry.addLine("Left and right trigger for moving back/foreward");
            telemetry.addLine("left and right stick for strafe/turn");
        } else {
            bot.drivetrain.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y,
                            -gamepad1.left_stick_x,
                            -gamepad1.right_stick_x
                    )
            );
            telemetry.addLine("left and right stick for omnidrectional movement/turn");
        }

        if (gamepad1.a) {
            bot.intake.transfer.setPower(.75);
        }

        if (gamepad1.b && !prev1.b) {
            bot.shootRings(SHOOTER_SPEED, 3);
        }

        if (gamepad1.left_bumper && !prev1.left_bumper) {
            bot.intake.toggleOuttake();
        } else if (gamepad1.right_bumper && !prev1.right_bumper) {
            bot.intake.toggleIntake();
        }

        if (gamepad1.x && !prev1.x) {
            bot.wobbleGrabber.toggleGrabber();
        }

        try {
            prev1.copy(gamepad1);
        } catch (RobotCoreException e) {
            telemetry.addLine("Tried to Copy gamepad 1.");
            telemetry.addLine(e.getMessage());
            requestOpModeStop();
        }

        bot.wobbleGrabber.setPower(-gamepad2.right_stick_y);

        if (gamepad2.x && !prev2.x) {
            bot.wobbleGrabber.toggleGrabber();
        }

        try {
            prev2.copy(gamepad1);
        } catch (RobotCoreException e) {
            telemetry.addLine("Tried to Copy gamepad 2.");
            telemetry.addLine(e.getMessage());
            requestOpModeStop();
        }

        bot.update();

        telemetry.addLine("Hold 'A' to force transfer to go on");
        telemetry.addLine("'B' to shoot 3 rings thru shooter automation (not recommended");
        telemetry.addLine("'X' to open/close wobble grabber");
        telemetry.addLine("Left and right bumper for outtake/intake");
        telemetry.addLine("\nGAMEPAD 2:");
        telemetry.addLine("----------------------");
        telemetry.addLine("Right stick for wobble grabber position");
        telemetry.addLine("'X' to open/close wobble grabber");
    }
}
