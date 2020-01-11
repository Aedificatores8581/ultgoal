package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon Manual Odom Test")
public class CleonManualOdomTest extends OpMode {
    CleonBot bot;

    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }
    }

    @Override
    public void loop() {
        telemetry.addData("strafe inch", bot.getStrafeDistanceInches());
        telemetry.addData("strafe enc", bot.drivetrain.getRightRearEncoder());
        telemetry.addData("\nfore inch", bot.getForeDistanceInches());
        telemetry.addData("fore enc", bot.drivetrain.getRightForeEncoder());
        telemetry.addData("\n\nlf", bot.drivetrain.getLeftForeEncoder());
        telemetry.addData("rf", bot.drivetrain.getRightForeEncoder());
        telemetry.addData("la", bot.drivetrain.getLeftRearEncoder());
        telemetry.addData("ra", bot.drivetrain.getRightRearEncoder());
    }
}
