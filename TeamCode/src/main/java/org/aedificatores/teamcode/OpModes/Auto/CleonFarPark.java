package org.aedificatores.teamcode.OpModes.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.PIDController;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon Far Park Auto")
public class CleonFarPark extends OpMode {

    enum AutoState {
        FORE,
        STRAFE,
        STOP,
    }

    enum Alliance {BLUE, RED}
    enum Position {BUILD_AREA, LOAD_AREA}

    private static final double SPEED = .7;

    enum TurnDirection {
        LEFT(-.3),
        RIGHT(.3);

        private double speed;
        TurnDirection(double s) {
            speed = s;
        }

        public double getSpeed() {
            return speed;
        }
    }

    AutoState autoState;
    Alliance alliance;
    Position position;

    PIDController drivePID;
    static final double KP = .14;
    static final double KI = 0.0;
    static final double KD = .003;
    static final double DELTA_TIME = 0.05;

    CleonBot bot;

    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            Log.e("Cleon FAR PARK",e.getMessage());
            requestOpModeStop();
        }
        autoState = AutoState.FORE;
        alliance = Alliance.RED;
        position = Position.LOAD_AREA;
        drivePID = new PIDController(KP, KI, KD, DELTA_TIME);

        bot.foundationGrabber.open();

    }

    @Override
    public void init_loop() {
        if (gamepad1.a) {
            alliance = Alliance.BLUE;
        }
        if (gamepad1.b) {
            alliance = Alliance.RED;
        }
        if (gamepad1.x) {
            position = Position.LOAD_AREA;
        }
        if (gamepad1.y) {
            position = Position.BUILD_AREA;
        }

        telemetry.addData("Alliance", alliance);
        telemetry.addData("Position", position);
    }

    @Override
    public void start() {
        bot.grabber.init();
        bot.drivetrain.resetMotorEncoders();
        resetStartTime();
    }

    @Override
    public void loop() {
        switch (autoState) {
            case FORE:
                if (drive(new Vector2(0, -SPEED), 24, 2)) {
                    autoState = AutoState.STRAFE;
                }
                break;
            case STRAFE:
                if (drive(new Vector2(SPEED,0.0), 20, 2)) {
                    autoState = AutoState.STOP;
                }
                break;
            case STOP:
                break;
        }

        bot.updateRobotPosition2d();
        bot.setRobotAngle();
        bot.drivetrain.refreshMotors();
    }

    private boolean drive(Vector2 velocity, double inches, double timer) {
        Vector2 v = new Vector2(velocity);
        if (alliance == Alliance.BLUE) {
            v.x = -v.x;
        }

        if (position == Position.BUILD_AREA) {
            v.x = -v.x;
        }

        double distance = Math.sqrt(Math.pow(bot.getLeftForeDistanceInches(), 2) + Math.pow(bot.getStrafeDistanceInches(), 2));

        drivePID.error = inches - distance;
        drivePID.idealLoop();
        v.scalarMultiply(drivePID.currentOutput);
        v.x = (Math.abs(v.x) > SPEED) ? SPEED * Math.signum(v.x) : v.x;
        v.y = (Math.abs(v.y) > SPEED) ? SPEED * Math.signum(v.y) : v.y;
        bot.drivetrain.setVelocity(v);
        // bot.drivetrain.setVelocity(v);


        if (Math.abs(distance) > inches || getRuntime() > timer) {
            bot.drivetrain.setVelocity(new Vector2(0,0));
            bot.drivetrain.refreshMotors();
            resetStartTime();
            bot.drivetrain.resetMotorEncoders();
            return true;
        }
        return false;
    }
}

