package org.aedificatores.teamcode.OpModes.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon 2D Drive To Point Test")
public class Cleon2dDriveToPointTest extends OpMode {


    CleonBot bot;
    Vector2 destination;
    Vector2 velocity;

    Gamepad prev;

    private static final String TAG = "Cleon2dDriveToPointTest";

    @Override
    public void init() {
        destination = new Vector2();
        velocity = new Vector2();

        try {
            bot = new CleonBot(hardwareMap, true);
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
    public void init_loop() {
        destination.x += gamepad1.left_stick_x;
        destination.y += gamepad1.left_stick_y;

        velocity.x = gamepad1.right_stick_x;
        velocity.y = gamepad1.right_stick_y;

        telemetry.addLine("Edit Destination Vec with left stick, the velocity vec with right stick");
        telemetry.addData("\ndestination x", destination.x);
        telemetry.addData("destination y", destination.y);

        telemetry.addData("\nvelocity x", velocity.x);
        telemetry.addData("velocity y", velocity.y);

    }

    @Override
    public void loop() {
        bot.driveToPoint2d(destination, velocity);

        telemetry.addData("Robot Pos x",bot.robotPosition.x);
        telemetry.addData("Robot Pos y",bot.robotPosition.y);
        telemetry.addData("Angle",bot.robotAngle);
        telemetry.addData("Angle (degrees)",bot.getGyroAngleZ());

        bot.drivetrain.refreshMotors();
        bot.setRobotAngle();
        bot.updateRobotPosition2d();
    }
}
