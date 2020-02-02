package org.aedificatores.teamcode.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

@TeleOp(name = "Cleon Teleop")
public class CleonBotTeleop extends OpMode {
	CleonBot robot;
    Vector2 leftStick1, rightStick1, leftStick2;

    @Override
    public void init() {
        try {
            robot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            telemetry.addLine("Exception Caught: " + e.getMessage());
            telemetry.addLine("\n\nStopping OpMode");
            requestOpModeStop();
        }

        leftStick1 = new Vector2();
        rightStick1 = new Vector2();
        leftStick2 = new Vector2();
    }

    @Override
    public void loop() {
        updateGamepadValues();

        robot.drivetrain.setVelocityBasedOnGamePad(leftStick1, rightStick1);

        

    }

    public void updateGamepadValues(){
        leftStick1 = new Vector2(gamepad1.left_stick_x, gamepad1.left_trigger - gamepad1.right_trigger);
        rightStick1 = new Vector2(gamepad1.right_stick_x, gamepad1.right_stick_y);
        leftStick2 = new Vector2(gamepad1.left_stick_x, gamepad1.left_stick_y);
    }
}
