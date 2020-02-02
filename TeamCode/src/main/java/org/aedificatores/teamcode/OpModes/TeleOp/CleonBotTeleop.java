package org.aedificatores.teamcode.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Components.CleonLift;
import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.OpModes.Auto.CleonBotSingleSkystoneAuto;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.json.JSONException;

import java.io.IOException;

@TeleOp(name = "Cleon Teleop")
public class CleonBotTeleop extends OpMode {
	private static boolean foundationGrabberClosed = false;

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

        robot.intake.setIntakePower(Math.abs(gamepad1.left_trigger));

        if (gamepad2.x && !foundationGrabberClosed) {
        	robot.foundationGrabber.close();
        	foundationGrabberClosed = true;
		}

        if (gamepad2.x && foundationGrabberClosed) {
        	robot.foundationGrabber.open();
        	foundationGrabberClosed = false;
		}

        robot.drivetrain.refreshMotors();
    }
}
