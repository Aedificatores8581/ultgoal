package org.aedificatores.teamcode.OpModes.Auto.Tests;

import android.util.Log;

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
    boolean reached;

    private static final String TAG = "CleonDrivePIDTest";

    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, true);
        }  catch (IOException | JSONException e) {
            Log.e(TAG,e.getMessage());
            telemetry.addLine(e.getMessage());

            Log.e(TAG,"Stack Trace: ");
            telemetry.addLine("Stack Trace: ");

            for (StackTraceElement i : e.getStackTrace()) {
                Log.e(TAG,"\t" + i.toString());
                telemetry.addLine("\t" + i.toString());
            }
        }
        try {
            logger = new TelemetryLogger();
            logger.writeToLogInCSV("inches");
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
            telemetry.addLine(e.getMessage());

            Log.e(TAG,"Stack Trace: ");
            telemetry.addLine("Stack Trace: ");

            for (StackTraceElement i : e.getStackTrace()) {
                Log.e(TAG,"\t" + i.toString());
                telemetry.addLine("\t" + i.toString());
            }
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

        if (!reached) {
            reached = bot.driveForePID(goal, 0);
        }
        bot.drivetrain.refreshMotors();
        bot.updateRobotPosition2d();
        telemetry.addData("inches",dist);
        telemetry.addData("Strafe", bot.getStrafeDistanceInches());
        telemetry.addData("RFore", bot.getRightForeDistanceInches());
        telemetry.addData("LFore", bot.getLeftForeDistanceInches());
        telemetry.addData("goal",goal);
        telemetry.addData("goal reached",reached);

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
