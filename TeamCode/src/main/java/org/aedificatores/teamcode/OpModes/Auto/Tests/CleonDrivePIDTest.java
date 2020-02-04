package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.TelemetryLogger;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon Drive PID Test")
public class CleonDrivePIDTest extends OpMode {
    CleonBot bot;
    TelemetryLogger logger;
    double goal = 24;


    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, true);
        } catch (IOException | JSONException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }
        try {
            logger = new TelemetryLogger();
            logger.writeToLogInCSV("inches");
        } catch (IOException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }
    }

    @Override
    public void init_loop() {
        goal += gamepad1.left_stick_y * .1;
        telemetry.addData("goal",goal);
    }

    @Override
    public void loop() {
        double dist = Math.sqrt(Math.pow(bot.getRightForeDistanceInches(),2) + Math.pow(bot.getStrafeDistanceInches(),2));
        goal += gamepad1.left_stick_y * .6;
        try {
            logger.writeToLogInCSV(dist);
        } catch (IOException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }

        boolean reached = bot.drivePID(new Vector2(0.0,-1.0), 0, goal, 300 * 1000);
        bot.drivetrain.refreshMotors();
        bot.updateRobotPosition();
        telemetry.addData("inches",dist);
        telemetry.addData("strafe", bot.getStrafeDistanceInches());
        telemetry.addData("goal",goal);
        telemetry.addData("goal reached",reached);
        telemetry.addData("integral", bot.robotPosPID.integral);

    }

    @Override
    public void stop() {
        try {
            logger.close();
        } catch (IOException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }
    }
}
