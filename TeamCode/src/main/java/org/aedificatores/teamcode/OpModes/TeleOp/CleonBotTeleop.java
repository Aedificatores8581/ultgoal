package org.aedificatores.teamcode.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Components.CleonLift;
import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.OpModes.Auto.CleonBotSingleSkystoneAuto;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

@TeleOp(name = "Cleon Bot Teleop")
public class CleonBotTeleop extends OpMode {
    CleonBot robot;
    Vector2 leftStick, rightStick;

    private static final double SPEED_MOD = 0.7;

    @Override
    public void init() {
        try {
            robot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            telemetry.addLine("Exception Caught: " + e.getMessage());
            telemetry.addLine("\n\nStopping OpMode");
            requestOpModeStop();
        }

        leftStick = new Vector2();
        rightStick = new Vector2();
    }

    @Override
    public void loop() {
        robot.intake.setIntakePower(gamepad1.right_trigger - gamepad1.left_trigger);

        leftStick.setComponents(gamepad1.left_stick_x, gamepad1.left_stick_y);
        rightStick.setComponents(gamepad1.right_stick_x, gamepad1.right_stick_y);

        // Slow movement down when the left bumper is pressed.
        if (gamepad1.left_bumper) {
            robot.drivetrain.setVelocityBasedOnGamePad(leftStick.scalarMultiply(SPEED_MOD), rightStick.scalarMultiply(SPEED_MOD));
        } else {
            robot.drivetrain.setVelocityBasedOnGamePad(leftStick, rightStick);
        }

        if (gamepad2.left_bumper) {
            recorrect(CleonBotSingleSkystoneAuto.TurnDirection.LEFT);
        } else if (gamepad2.right_bumper) {
            recorrect(CleonBotSingleSkystoneAuto.TurnDirection.RIGHT);
        }

        if (gamepad1.right_bumper) {

        }

        robot.drivetrain.refreshMotors();
    }

    private void recorrect(CleonBotSingleSkystoneAuto.TurnDirection dir) {
        double turnSpeed = dir.getSpeed();
        robot.drivetrain.setVelocityBasedOnGamePad(new Vector2(), new Vector2(turnSpeed, 0.00));
        if (robot.robotAngle != Math.PI / 2) {
            robot.drivetrain.setVelocity(new Vector2(0,0));
            robot.drivetrain.refreshMotors();
            resetStartTime();
            robot.drivetrain.resetMotorEncoders();
        }
    }
}
