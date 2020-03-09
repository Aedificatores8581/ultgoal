package org.aedificatores.teamcode.OpModes.Auto.Tests;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.json.JSONException;

import java.io.IOException;

//@Disabled
@Autonomous(name = "Cleon Side Grabber Test")
public class CleonSideGrabberTest extends OpMode {
    private static final String TAG = "CleonSideGrabberTest";


    CleonBot bot;
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


    }

    @Override
    public void loop() {
        // Front servo
        if (gamepad1.dpad_up) {
            bot.frontSideGrabber.grabberServo.setPosition(bot.frontSideGrabber.grabberServo.getPosition() + .001);
        } else if (gamepad1.dpad_down) {
            bot.frontSideGrabber.grabberServo.setPosition(bot.frontSideGrabber.grabberServo.getPosition() - .001);
        }

        if (gamepad1.right_bumper) {
            bot.frontSideGrabber.rotateServo.setPosition(bot.frontSideGrabber.rotateServo.getPosition() + .001);
        } else if (gamepad1.left_bumper) {
            bot.frontSideGrabber.rotateServo.setPosition(bot.frontSideGrabber.rotateServo.getPosition() - .001);
        }

        // Back Servo
        if (gamepad2.dpad_up) {
            bot.backSideGrabber.grabberServo.setPosition(bot.backSideGrabber.grabberServo.getPosition() + .001);
        } else if (gamepad2.dpad_down) {
            bot.backSideGrabber.grabberServo.setPosition(bot.backSideGrabber.grabberServo.getPosition() - .001);
        }

        if (gamepad2.right_bumper) {
            bot.backSideGrabber.rotateServo.setPosition(bot.backSideGrabber.rotateServo.getPosition() + .001);
        } else if (gamepad2.left_bumper) {
            bot.backSideGrabber.rotateServo.setPosition(bot.backSideGrabber.rotateServo.getPosition() - .001);
        }

        telemetry.addLine("Front Servo Rotate:\t" + bot.frontSideGrabber.rotateServo.getPosition());
        telemetry.addLine("Front Servo Grabber:\t" + bot.frontSideGrabber.grabberServo.getPosition());
        telemetry.addLine("\nBack Servo Rotate:\t" + bot.backSideGrabber.rotateServo.getPosition());
        telemetry.addLine("Back Servo Grabber:\t" + bot.backSideGrabber.grabberServo.getPosition());
    }
}
