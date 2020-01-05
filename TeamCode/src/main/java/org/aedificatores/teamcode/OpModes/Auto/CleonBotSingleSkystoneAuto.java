package org.aedificatores.teamcode.OpModes.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.PIDController;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Vision.SkystoneDetector;
import org.json.JSONException;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

import java.io.IOException;

@Autonomous(name = "Cleon Bot Single Skystone Auto")
public class CleonBotSingleSkystoneAuto extends OpMode {

    private static final String TAG = "CleonBotSingleSkystone";
    enum AutoState {
        DETECT,
        FORE,
        LINEUP_WITH_STONE,
        TURN_90,
        STRAFE_TO_STONE,
        INTAKE_STONE,
        WAIT_FOR_STONE_TO_INTAKE,
        STRAFE_OUT_OF_STONE_AREA,
        RECORRECT_TO_90,
        BACK_TO_BUILD_AREA,
        STRAFE_TO_FOUNDATION,
        GRAB_FOUNDATION,
        BACK_FROM_INIT_FOUNDATION_AREA,
        ROTATE_FOUNDATION,
        RELEASE_FOUNDATION,
        PUSH_FOUNDATION,
        BACK_FROM_FOUNDATION,
        TURN_90_TO_DROP_STONE,
        DROP_STONE,
        PARK,
        STOP
    }

    enum Alliance {BLUE, RED}

    private static final double SPEED = .7;

    // Public so that it can be used in Auto TeleOp.
    public enum TurnDirection {
        LEFT(-.33),
        RIGHT(.33);

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

    PIDController drivePID;
    static final double KP = .12;
    static final double KI = 0.0;
    static final double KD = .004;
    static final double DELTA_TIME = 0.05;

    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;
    OpenCvCamera phoneCam;
    SkystoneDetector detector;

    CleonBot bot;

    double distanceOfSkystone = 0;
    @Override
    public void init() {
        Log.i(TAG, "Initializing CleonBotSingleSkystoneAuto");
        // Initialize phone camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam  = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.openCameraDevice();

        detector = new SkystoneDetector(SCREEN_WIDTH, SCREEN_HEIGHT);
        phoneCam.setPipeline(detector);
        phoneCam.startStreaming(SCREEN_WIDTH, SCREEN_HEIGHT, OpenCvCameraRotation.SIDEWAYS_RIGHT);
        Log.i(TAG, "Phone Camera Initialized");

        autoState = AutoState.DETECT;
        alliance = Alliance.RED;

        try {
            bot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        drivePID = new PIDController(KP, KI, KD, DELTA_TIME);

        Log.i(TAG, "Init Finished");
    }

    @Override
    public void init_loop() {
        if (gamepad1.a) {
            alliance = Alliance.BLUE;
        } else if (gamepad1.b) {
            alliance = Alliance.RED;
        }
        telemetry.addData("Alliance (press 'a' for blue, 'b' for red)",alliance);
    }

    // This particular code is repeated a bunch, so it's put in a function
    @Override
    public void loop() {
        switch (autoState) {
            case DETECT:
                if (detector.dieRoll == 1 || detector.dieRoll == 4) {
                    distanceOfSkystone = 10;
                } else if (detector.dieRoll == 2 || detector.dieRoll == 5) {
                    distanceOfSkystone = 5;
                } else {
                    distanceOfSkystone = 0;
                }
                autoState = AutoState.FORE;
                Log.i(TAG,"Fore");
                break;
            case FORE:
                if (drive(new Vector2(0, -SPEED), 7)) {
                    autoState = AutoState.LINEUP_WITH_STONE;
                    Log.i(TAG,"Lineup");
                }
                break;
            case LINEUP_WITH_STONE:
                if (drive(new Vector2(-SPEED,0), distanceOfSkystone)) {
                    autoState = AutoState.TURN_90;
                    Log.i(TAG,"Turn 90");
                }
                break;
            case TURN_90:
                if (turn(Math.PI/2, TurnDirection.LEFT)) {
                    autoState = AutoState.STRAFE_TO_STONE;
                    Log.i(TAG,"Strafe to stone");
                }
                break;
            case STRAFE_TO_STONE:
                if (drive(new Vector2(SPEED, 0), 27)) {
                    autoState = AutoState.INTAKE_STONE;
                    Log.i(TAG,"Intake stone");
                }
                break;
            case INTAKE_STONE:
                bot.intake.setIntakePower(1.0);
                if (drive(new Vector2(0, -SPEED/1.2), 4)) {
                    autoState = AutoState.WAIT_FOR_STONE_TO_INTAKE;
                    resetStartTime();
                    Log.i(TAG,"strafe from stone");
                }
                break;
            case WAIT_FOR_STONE_TO_INTAKE:
                if (getRuntime() > 3.0) {
                    bot.intake.setIntakePower(0);
                    resetStartTime();
                    autoState = AutoState.STRAFE_OUT_OF_STONE_AREA;
                }
                break;
            case STRAFE_OUT_OF_STONE_AREA:
                if (drive(new Vector2(-SPEED, 0), 22)) {
                    autoState = AutoState.RECORRECT_TO_90;
                    Log.i(TAG,"recorrect to 90");
                }
                break;
            case RECORRECT_TO_90:
                if(turn(Math.PI/2, TurnDirection.RIGHT)) {
                    autoState = AutoState.BACK_TO_BUILD_AREA;
                    Log.i(TAG,"Back to build area");
                }
                break;
            case BACK_TO_BUILD_AREA:
                if (drive(new Vector2(0, SPEED), 90+distanceOfSkystone)) {
                    autoState = AutoState.STRAFE_TO_FOUNDATION;
                    Log.i(TAG,"strafe to foundation");
                }
                break;
            case STRAFE_TO_FOUNDATION:
                if (drive(new Vector2(SPEED, 0), 2)) {
                    autoState = AutoState.GRAB_FOUNDATION;
                    resetStartTime();
                    Log.i(TAG,"grab foundation");
                }
                break;
            case GRAB_FOUNDATION:
                bot.foundationGrabber.release();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.BACK_FROM_INIT_FOUNDATION_AREA;
                    Log.i(TAG,"back from init foundation area");
                }
                break;
            case BACK_FROM_INIT_FOUNDATION_AREA:
                if (drive(new Vector2(-SPEED, 0), 2)) {
                    autoState = AutoState.ROTATE_FOUNDATION;
                    Log.i(TAG,"rotate foundation");
                }
                break;
            case ROTATE_FOUNDATION:
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(-SPEED,0), new Vector2(SPEED,0));
                if (bot.robotAngle < 0) {
                    bot.drivetrain.setVelocity(new Vector2(0,0));
                    resetStartTime();
                    bot.drivetrain.resetMotorEncoders();
                    Log.i(TAG,"release foundation");
                }
                break;
            case RELEASE_FOUNDATION:
                bot.foundationGrabber.release();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.PUSH_FOUNDATION;
                    Log.i(TAG,"Push Foundation");
                }
                break;
            case PUSH_FOUNDATION:
                if (drive(new Vector2(-SPEED, 0), 12)) {
                    autoState = AutoState.BACK_FROM_FOUNDATION;
                    Log.i(TAG,"Back from foundation");
                }
                break;
            case BACK_FROM_FOUNDATION:
                if (drive(new Vector2(SPEED, 0), 6)) {
                    autoState = AutoState.TURN_90_TO_DROP_STONE;
                    Log.i(TAG,"Turn 90 to drop stone");
                }
                break;
            case TURN_90_TO_DROP_STONE:
                if (turn(Math.PI/2, TurnDirection.RIGHT)) {
                    autoState = AutoState.DROP_STONE;
                    Log.i(TAG,"drop stone");
                }
                break;
            case DROP_STONE:
                break;
            case PARK:
                if (drive(new Vector2(-SPEED, -SPEED), 1000)) {
                    autoState = AutoState.STOP;
                }
                break;
            case STOP:
                break;
        }

        bot.updateRobotPosition();
        bot.setRobotAngle();
        bot.drivetrain.refreshMotors();

        telemetry.addData("angle", bot.robotAngle);
        telemetry.addData("actual angle", bot.getGyroAngleZ());
        telemetry.addData("inches", Math.sqrt(Math.pow(bot.getForeDistanceInches(), 2) + Math.pow(bot.getStrafeDistanceInches(), 2)));
    }

    // Returns true if the bot has reached the desired encoder limit
    private boolean drive(Vector2 velocity, double inches) {
        Vector2 v = new Vector2(velocity);
        if (alliance == Alliance.BLUE) {
            v.x = -v.x;
        }

        double distance = Math.sqrt(Math.pow(bot.getForeDistanceInches(), 2) + Math.pow(bot.getStrafeDistanceInches(), 2));

        drivePID.error = inches - distance;
        drivePID.idealLoop();
        bot.drivetrain.setVelocity(v.scalarMultiply(drivePID.currentOutput));
        // bot.drivetrain.setVelocity(v);


        if (Math.abs(distance) > inches) {
            bot.drivetrain.setVelocity(new Vector2(0,0));
            bot.drivetrain.refreshMotors();
            resetStartTime();
            bot.drivetrain.resetMotorEncoders();
            return true;
        }
        return false;
    }

    private boolean turn(double angle, TurnDirection dir) {
        double turnSpeed = dir.getSpeed();

        if (alliance == Alliance.BLUE) {
            turnSpeed = -turnSpeed;
        }


        bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(), new Vector2(turnSpeed, 0.0));

        if (bot.robotAngle != angle) {
            bot.drivetrain.setVelocity(new Vector2(0,0));
            bot.drivetrain.refreshMotors();
            resetStartTime();
            bot.drivetrain.resetMotorEncoders();
            return true;
        }
        return false;
    }
}

