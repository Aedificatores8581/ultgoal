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

    private static final double SPEED = .7;

    static HummingbirdBot bot;

    enum AutoState {
        STRAFE_TO_CORNER(new VelocityEncoderPair(new Vector2(-SPEED, 0.0), 1000)),
        FORE_TO_FOUNDATION(new VelocityEncoderPair(new Vector2(0.0, SPEED), 1000)),
        GRAB(new VelocityEncoderPair(new Vector2(0.0, 0.0), 0)),
        BACK_TO_WALL(new VelocityEncoderPair(new Vector2(0.0, -SPEED), 500)),
        // TURN state moves differently than states like STRAFE_TO_CORNER, so the
        // velocity vector isn't used. However, the encoder value is.
        TURN(new VelocityEncoderPair(new Vector2(0.0, 0.0), 600)),
        PUSH(new VelocityEncoderPair(new Vector2(0.0, SPEED), 2000)),
        RELEASE(new VelocityEncoderPair(new Vector2(0.0, 0.0), 0)),
        BACK_FROM_FOUNDATION(new VelocityEncoderPair(new Vector2(0.0, -SPEED), 1000)),
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
    @Override
    public void init() {
        bot = new HummingbirdBot(hardwareMap);
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
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(0.0, -SPEED),
                                                         new Vector2(-.4,0.0));
                if (bot.drivetrain.getLeftForeEncoder() > state.getVEP().encoder) {
                    bot.drivetrain.setVelocity(new Vector2(0,0));
                    resetStartTime();
                    bot.drivetrain.resetMotorEncoders();
                    state = AutoState.RELEASE;
                }
                break;
            case PUSH:
                if (runToPosition(state)) state = AutoState.RELEASE;
                break;
            case RELEASE:
                bot.foundationGrabber.release();
                if (getRuntime() > 1.0) {
                    state = AutoState.BACK_FROM_FOUNDATION;
                    bot.drivetrain.resetMotorEncoders();
                }
                break;
            case BACK_FROM_FOUNDATION:
                if (runToPosition(state)) state = AutoState.STOP;
                break;
            case STOP:
                bot.drivetrain.setVelocity(state.getVEP().velocity);
                break;
        }

        bot.drivetrain.refreshMotors();
    }

    // This particular code is repeated a bunch, so it's put in a function
    // Returns true if the bot has reached the desired encoder limit
    private boolean runToPosition(AutoState state) {
        bot.drivetrain.setVelocity(state.getVEP().velocity);
        if (Math.abs(bot.drivetrain.getLeftForeEncoder()) > state.getVEP().encoder) {
            bot.drivetrain.setVelocity(new Vector2(0,0));
            resetStartTime();
            bot.drivetrain.resetMotorEncoders();
            return true;
        }
        return false;
    }


}
