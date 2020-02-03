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

    public final static double SLOW_STRAFE_SPEED = 0.5;
    public final static double SLOW_FORWARD_SPEED = 0.375;

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

        //intake code goes here

        if(gamepad1.y)
            robot.grabber.openGrabber();
        else if(gamepad2.y)
            robot.grabber.closeGrabber();

        if(gamepad2.left_bumper)
            robot.lift.idle();
        else if (gamepad2.left_stick_button)
            robot.lift.setLiftPower(-1);
        else
            robot.lift.setNormalizedLiftPower(gamepad2.left_stick_y);


    }

    public void updateGamepadValues(){
        leftStick1 = new Vector2(gamepad1.left_stick_x, gamepad1.left_trigger - gamepad1.right_trigger);
        rightStick1 = new Vector2(gamepad1.right_stick_x, gamepad1.right_stick_y);
        leftStick2 = new Vector2(gamepad1.left_stick_x, gamepad1.left_stick_y);

        leftStick1.x = gamepad1.dpad_left ?  -SLOW_STRAFE_SPEED : leftStick1.x;
        leftStick1.x = gamepad1.dpad_right ?  SLOW_STRAFE_SPEED : leftStick1.x;
        leftStick1.y = gamepad1.dpad_up ?  SLOW_FORWARD_SPEED : leftStick1.y;
        leftStick1.y = gamepad1.dpad_down ?  -SLOW_FORWARD_SPEED : leftStick1.y;
    }
}
