package org.aedificatores.teamcode.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.HummingbirdBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Vector;

@Autonomous(name = "Hummingbird Foundation Auto")
public class HummingbirdFoundationAuto extends OpMode {
    // Used for determining how far a robot should run and in what direction
    static class VelocityEncoderPair {
        Vector2 velocity;
        int encoder;

        VelocityEncoderPair(Vector2 velocity, int encoder) {
            this.velocity = velocity;
            this.encoder = encoder;
        }
    }

    private static final double SPEED = .25;

    static HummingbirdBot bot;

    enum Alliance {
        BLUE,
        RED
    }

    // enum for red auto state machine,
    // to get blue auto, negate the x component of the velocity vector
    enum AutoState {
        STRAFE_TO_CORNER(new VelocityEncoderPair(new Vector2(-SPEED, 0.0), 1300)),
        FORE_TO_FOUNDATION(new VelocityEncoderPair(new Vector2(0.0, -SPEED), 1500)),
        GRAB(new VelocityEncoderPair(new Vector2(0.0, 0.0), 0)),
        BACK_TO_WALL(new VelocityEncoderPair(new Vector2(0.0, SPEED), 100)),
        // TURN state moves differently than states like STRAFE_TO_CORNER, so the
        // velocity vector isn't used. However, the encoder value is.
        TURN(new VelocityEncoderPair(new Vector2(0.0, 0.0), 2800)),
        PUSH(new VelocityEncoderPair(new Vector2(0.0, -SPEED), 140)),
        RELEASE(new VelocityEncoderPair(new Vector2(0.0, 0.0), 0)),
        STRAFE_LEFT_TO_STOP(new VelocityEncoderPair(new Vector2(SPEED, 0.0), 1000)),
        BACK_FROM_FOUNDATION(new VelocityEncoderPair(new Vector2(0.0, SPEED), 1800)),
        STOP(new VelocityEncoderPair(new Vector2(0.0, 0.0), 0));

        private VelocityEncoderPair vep;

        AutoState(VelocityEncoderPair vep) {
            this.vep = vep;
        }

        VelocityEncoderPair getVEP() {
            return vep;
        }
    }

    private AutoState state = AutoState.STRAFE_TO_CORNER;
    private Alliance alliance;

    @Override
    public void init() {
        bot = new HummingbirdBot(hardwareMap);
        bot.foundationGrabber.release();
        alliance = Alliance.RED;
    }

    @Override
    public void init_loop() {
        super.init_loop();
        if (gamepad1.a)
            alliance = Alliance.BLUE;
        else if (gamepad1.b)
            alliance = Alliance.RED;

        telemetry.addData("Alliance", alliance);
        updateTelemetry(telemetry);
    }

    @Override
    public void loop() {
        switch (state){
            case STRAFE_TO_CORNER:
                if (runToPosition(state)) state = AutoState.FORE_TO_FOUNDATION;
                break;

            case FORE_TO_FOUNDATION:
                if (runToPosition(state)) state = AutoState.GRAB;
                break;

            case GRAB:
                bot.foundationGrabber.grab();
                if (getRuntime() > 1.0) {
                    state = AutoState.BACK_TO_WALL;
                    bot.drivetrain.resetMotorEncoders();
                }
                break;

            case BACK_TO_WALL:
                if (runToPosition(state)) state = AutoState.TURN;
                break;

            case TURN:
                if (alliance == Alliance.RED) {
                    bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(0.0, SPEED),
                            new Vector2(SPEED, 0.0));
                    if (Math.abs(bot.drivetrain.getLeftForeEncoder()) > state.getVEP().encoder) {
                        bot.drivetrain.setVelocity(new Vector2(0,0));
                        resetStartTime();
                        bot.drivetrain.resetMotorEncoders();
                        state = AutoState.PUSH;
                    }
                } else if (alliance == Alliance.BLUE) {
                    bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(0.0, SPEED),
                            new Vector2(-SPEED, 0.0));
                    if (Math.abs(bot.drivetrain.getLeftForeEncoder()) > state.getVEP().encoder + 200) {
                        bot.drivetrain.setVelocity(new Vector2(0,0));
                        resetStartTime();
                        bot.drivetrain.resetMotorEncoders();
                        state = AutoState.PUSH;
                    }
                }
                break;
            case PUSH:
                if (runToPosition(state)) state = AutoState.RELEASE;
                break;
            case RELEASE:
                bot.foundationGrabber.release();
                if (getRuntime() > 1.0) {
                    state = AutoState.STRAFE_LEFT_TO_STOP;
                    bot.drivetrain.resetMotorEncoders();
                }
                break;
            case STRAFE_LEFT_TO_STOP:
                if (runToPosition(state)) state = AutoState.BACK_FROM_FOUNDATION;
                break;
            case BACK_FROM_FOUNDATION:
                if (runToPosition(state)) state = AutoState.STOP;
                break;
            case STOP:
                bot.drivetrain.setVelocity(state.getVEP().velocity);
                break;
        }

        bot.drivetrain.refreshMotors();

        telemetry.addData("State", state);
        telemetry.addData("\nGoal Velocity", state.getVEP().velocity);
        telemetry.addData("\nGoal Encoder", state.getVEP().encoder);
        telemetry.addData("Actual Encoder", bot.drivetrain.getLeftForeEncoder());
    }

    // This particular code is repeated a bunch, so it's put in a function
    // Returns true if the bot has reached the desired encoder limit
    private boolean runToPosition(AutoState state) {
        Vector2 v = new Vector2(state.getVEP().velocity);
        if (alliance == Alliance.BLUE) {
            v.x = -v.x;
        }

        bot.drivetrain.setVelocity(v);
        if (Math.abs(bot.drivetrain.getLeftForeEncoder()) > state.getVEP().encoder) {
            bot.drivetrain.setVelocity(new Vector2(0,0));
            resetStartTime();
            bot.drivetrain.resetMotorEncoders();
            return true;
        }
        return false;
    }


}
