package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

@TeleOp(name = "Cleon Bot Test")
public class CleonBotTest extends OpMode {
    CleonBot bot;
    Vector2 leftStick, rightStick;


    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, true);
        } catch (IOException | JSONException e) {
            telemetry.addLine(e.getMessage());
        }
        leftStick = new Vector2();
        rightStick = new Vector2();
    }

    @Override
    public void loop() {
        leftStick.x = gamepad1.left_stick_x;
        leftStick.y = gamepad1.left_stick_y;
        rightStick.x = gamepad1.right_stick_x;
        rightStick.y = gamepad1.right_stick_y;
        bot.drivetrain.setVelocityBasedOnGamePad(leftStick, rightStick);
        bot.drivetrain.refreshMotors();

        bot.setRobotAngle();
        bot.updateRobotPosition();

        telemetry.addData("x",bot.robotPosition.x);
        telemetry.addData("y",bot.robotPosition.y);
        telemetry.addData("fore inch",bot.getLeftForeDistanceInches());
        telemetry.addData("strafe inch",bot.getStrafeDistanceInches());
        telemetry.addData("angle",bot.getGyroAngleZ());

        telemetry.addData("LF", bot.drivetrain.getLeftForeEncoder());
        telemetry.addData("RF", bot.drivetrain.getRightForeEncoder());
        telemetry.addData("LR", bot.drivetrain.getLeftForeEncoder());
        telemetry.addData("RR", bot.drivetrain.getRightRearEncoder());
        telemetry.addData("deltaTheta", bot.deltaRobotAngle);
        telemetry.addData("Fore Move after turn", bot.deltaForeMovementAfterTurn);
        telemetry.addData("Power", bot.drivetrain.leftFore.getPower());
    }
}
