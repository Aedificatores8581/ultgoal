package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

// Robot drives in a square

@Autonomous(name = "Odom Pod Test")
public class OdometryPodsTest extends OpMode {
    enum State {FORWARD, LEFT, DOWN, RIGHT, STOP}

    State state;
    CleonBot bot;
    private static final int TICKS = 2000;

    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, true);
        } catch (IOException | JSONException e) {
            telemetry.addLine(e.getMessage());
        }
        state = State.FORWARD;
    }

    @Override
    public void init_loop() {
        telemetry.addData("fore\tinches",bot.getLeftForeDistanceInches());
        telemetry.addData("strafe\tinches",bot.getStrafeDistanceInches());
        telemetry.addData("angle x",bot.getGyroAngleX());
        telemetry.addData("angle y",bot.getGyroAngleY());
        telemetry.addData("angle z",bot.getGyroAngleZ());
    }


    @Override
    public void loop() {
        telemetry.addData("up enc",bot.drivetrain.getRightForeEncoder());
        telemetry.addData("side enc",bot.drivetrain.getRightRearEncoder());

        switch (state) {
            case FORWARD:
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(0,-.3),new Vector2(0,0));
                if (bot.drivetrain.getRightForeEncoder() > TICKS){
                    state = State.LEFT;
                }
                break;

            case LEFT:
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(-.3,0),new Vector2(0,0));
                if (bot.drivetrain.getRightRearEncoder() < -TICKS){
                    state = State.DOWN;
                }
                break;

            case DOWN:
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(0,.3),new Vector2(0,0));
                if (bot.drivetrain.getRightForeEncoder() < 0){
                    state = State.RIGHT;
                }
                break;

            case RIGHT:
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(.3,0),new Vector2(0,0));
                if (bot.drivetrain.getRightRearEncoder() > 0){
                    state = State.STOP;
                }
                break;

            case STOP:
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(0,0),new Vector2(0,0));
                break;
            default:
                telemetry.addLine("Something wrong the current state");
                break;
        }

        bot.drivetrain.refreshMotors();

    }
}
