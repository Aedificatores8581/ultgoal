package org.aedificatores.teamcode.OpModes.Auto.Tests;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.json.JSONException;

import java.io.IOException;

//@Disabled
@Autonomous(name = "Cleon SIde Grabber Test")
public class CleonSideGrabberTest extends OpMode {
    private static final String TAG = "CleonSideGrabberTest";


    CleonBot bot;
    boolean taskComplete;
    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            Log.e(TAG,e.getMessage());
            telemetry.addLine(e.getMessage());

            Log.e(TAG,"Stack Trace: ");
            telemetry.addLine("Stack Trace: ");

            for (StackTraceElement i : e.getStackTrace()) {
                Log.e(TAG,"\t" + i.toString());
                telemetry.addLine("\t" + i.toString());
            }
        }

        taskComplete = false;
    }

    @Override
    public void loop() {

        if (gamepad1.dpad_down) {
            if (!taskComplete) {
                taskComplete = bot.backSideGrabber.moveDownAndRelease();
                telemetry.addLine("Current Function: moveDownAndRelease");
            }
        }
        if (gamepad1.dpad_up) {
            bot.backSideGrabber.moveUp();
            telemetry.addLine("Current Function: moveUp");
        }
        if (gamepad1.dpad_right) {
            bot.backSideGrabber.moveDown();
            telemetry.addLine("Current Function: moveDown");
        }
        if (gamepad1.left_bumper) {
            bot.backSideGrabber.openGrabber();
            telemetry.addLine("Current Function: openGrabber");
        }
        if (gamepad1.right_bumper) {
            bot.backSideGrabber.closeGrabber();
            telemetry.addLine("Current Function: closeGrabber");
        }
    }
}
