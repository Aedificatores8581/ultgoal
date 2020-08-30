package org.aedificatores.teamcode.OpModes.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.PIDController;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon Far Park Auto")
public class CleonFarPark extends OpMode {

    enum AutoState {
        WAIT,
        FORE,
        STRAFE,
        STOP,
    }

    enum Alliance {
        BLUE(-1.0),
        RED(1.0);

        private double strafeMultiplier;

        Alliance(double strafeMultiplier) {
            this.strafeMultiplier = strafeMultiplier;
        }

        public double getStrafeMultiplier() {
            return strafeMultiplier;
        }
    }

    enum ParkPosition {
        NEAR(0.0),
        FAR(25.0);

        private double foreDistance;


        ParkPosition(double foreDistance) {
            this.foreDistance = foreDistance;
        }

        public double getForeDistance() {
            return foreDistance;
        }
    }

    enum StartPosition {
        BUILD_AREA(-1.0),
        LOAD_AREA(1.0);

        private double strafeMultiplier;

        StartPosition(double strafeMultiplier) {
            this.strafeMultiplier = strafeMultiplier;
        }

        public double getStrafeMultiplier() {
            return strafeMultiplier;
        }

    }

    private static final double SPEED = .7;

    AutoState autoState;
    Alliance alliance;
    StartPosition startPosition;
    ParkPosition parkPosition;

    private double strafeMult;

    private double wait;

    PIDController drivePID;
    static final double KP = .14;
    static final double KI = 0.0;
    static final double KD = .003;
    static final double DELTA_TIME = 0.05;

    CleonBot bot;

    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, true);
        } catch (IOException | JSONException e) {
            Log.e("Cleon FAR PARK",e.getMessage());
            requestOpModeStop();
        }
        autoState = AutoState.WAIT;
        alliance = Alliance.RED;
        startPosition = StartPosition.LOAD_AREA;
        parkPosition = ParkPosition.NEAR;

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
            startPosition = StartPosition.LOAD_AREA;
        }
        if (gamepad1.y) {
            startPosition = StartPosition.BUILD_AREA;
        }
        if (gamepad1.dpad_up) {
            parkPosition = ParkPosition.FAR;
        }
        if (gamepad1.dpad_down) {
            parkPosition = ParkPosition.NEAR;
        }

        wait = UniversalFunctions.clamp(0.0, wait + .2 * gamepad1.left_stick_y, 30.0);

        telemetry.addData("Alliance (a/b)", alliance);
        telemetry.addData("Start Position (x/y)", startPosition);
        telemetry.addData("Park Position (bumpers)", parkPosition);

        telemetry.addData("Wait",wait);
    }

    @Override
    public void start() {
        bot.drivetrain.resetMotorEncoders();
        resetStartTime();
        strafeMult = startPosition.getStrafeMultiplier() * alliance.getStrafeMultiplier();
        bot.frontSideGrabber.holdBlockPos();
        bot.backSideGrabber.holdBlockPos();
    }

    @Override
    public void loop() {
        switch (autoState) {
            case WAIT:
                if (getRuntime() > wait) {
                    autoState = AutoState.FORE;
                }
                break;
            case FORE:
                if (bot.driveForePID(parkPosition.getForeDistance(), 0)) {
                    autoState = AutoState.STRAFE;
                }
                break;
            case STRAFE:
                if (bot.driveStrafePID(strafeMult * 12, 0)) {
                    autoState = AutoState.STOP;
                }
                break;
            case STOP:
                bot.frontSideGrabber.moveUp();
                bot.backSideGrabber.moveUp();
                break;
        }

        bot.updateRobotPosition2d();
        bot.setRobotAngle();
        bot.drivetrain.refreshMotors();
    }
}

