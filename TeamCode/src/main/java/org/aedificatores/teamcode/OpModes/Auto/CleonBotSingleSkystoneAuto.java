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
        RETURN_90,
        BLUE_TURN,
        STRAFE_TO_STONE,
        INTAKE_STONE,
        WAIT_FOR_STONE_TO_INTAKE,
        STRAFE_OUT_OF_STONE_AREA,
        RECORRECT_TO_90,
        BACK_TO_BUILD_AREA,
        BLUE_180_TO_GRAB,
        STRAFE_TO_FOUNDATION,
        GRAB_FOUNDATION,
        DRAG_FOUNDATION,
        ROTATE_FOUNDATION,
        RELEASE_FOUNDATION,
        FORE_AROUND_FOUNDATION,
        STRAFE_AROUND_FOUNDATION,
        BLUE_180_TO_PUSH,
        PUSH_FOUNDATION,
        BACK_FROM_FOUNDATION,
        TURN_90_TO_DROP_STONE,
        EXTEND_GRABBER,
        DROP_STONE,
        RECORRECT_90_PARK,
        PARK,
        STOP
    }

    enum Alliance {BLUE, RED}

    private static final double SPEED = .7;

<<<<<<< HEAD
    // Public so that it can be used in Auto TeleOp.
    public enum TurnDirection {
        LEFT(-.33),
        RIGHT(.33);
=======
    enum TurnDirection {
        LEFT(-.3),
        RIGHT(.3);
>>>>>>> f1c2c1185ad2f13b1ee8bce26733ccbc47963afe

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
    static final double KP = .14;
    static final double KI = 0.0;
    static final double KD = .003;
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

        bot.foundationGrabber.open();
        Log.i(TAG, "Init Finished");
    }

    @Override
    public void init_loop() {
        // bot.grabber.retract();
        if (gamepad1.a) {
            alliance = Alliance.BLUE;
        } else if (gamepad1.b) {
            alliance = Alliance.RED;
        }

        telemetry.addLine("WARNING: This auto doesn't work since the PID function has changed since this was last edited");
        telemetry.addData("Alliance (press 'a' for blue, 'b' for red)",alliance);
    }

    @Override
    public void start() {
        super.start();
        bot.grabber.init();
    }

    // This particular code is repeated a bunch, so it's put in a function
    @Override
    public void loop() {
        switch (autoState) {
            case DETECT:
                if (detector.dieRoll == 1 || detector.dieRoll == 4) {
                    distanceOfSkystone = 7;
                } else if (detector.dieRoll == 2 || detector.dieRoll == 5) {
                    distanceOfSkystone = 3;
                } else {
                    distanceOfSkystone = 0;
                }
                autoState = AutoState.FORE;
                Log.i(TAG,"Fore");
                resetStartTime();
                break;
            case FORE:
                if (alliance == Alliance.RED) {
                    if (drive(new Vector2(0, -SPEED), 7, 2)) {
                        autoState = AutoState.LINEUP_WITH_STONE;
                        Log.i(TAG, "Lineup");
                    }
                } else {
                    if (drive(new Vector2(0, -SPEED), 18, 2)) {
                        autoState = AutoState.BLUE_TURN;
                        Log.i(TAG, "Lineup");
                    }
                }
                break;
            case LINEUP_WITH_STONE:
                if (drive(new Vector2(-SPEED,0), distanceOfSkystone, 2)) {
                    autoState = AutoState.TURN_90;
                    Log.i(TAG,"Turn 90");
                }
                break;
            case TURN_90:
                if (turn(Math.PI/2, TurnDirection.LEFT)) {
                    autoState = AutoState.RETURN_90;
                    Log.i(TAG,"Retrun 90");
                }
                break;
            case RETURN_90:
                if (turn(Math.PI/2, TurnDirection.RIGHT)) {
                    autoState = AutoState.STRAFE_TO_STONE;
                    Log.i(TAG,"Strafe to stone");
                }
                break;
            case BLUE_TURN:
                if (turn(-Math.PI/2, TurnDirection.RIGHT)) {
                    autoState = AutoState.BACK_TO_BUILD_AREA;
                    Log.i(TAG,"Intake stone");
                }
                break;
            case STRAFE_TO_STONE:
                if (drive(new Vector2(SPEED, 0), 32, 2)) {
                    autoState = AutoState.INTAKE_STONE;
                    Log.i(TAG,"Intake stone");
                }
                break;
            case INTAKE_STONE:
                bot.intake.setIntakePower(1.0);
                if (drive(new Vector2(0, -SPEED/1.2), 7, 2)) {
                    autoState = AutoState.WAIT_FOR_STONE_TO_INTAKE;
                    resetStartTime();
                    Log.i(TAG,"strafe from stone");
                }
                break;
            case WAIT_FOR_STONE_TO_INTAKE:
                if (getRuntime() > 0.0) {
                    resetStartTime();
                    autoState = AutoState.STRAFE_OUT_OF_STONE_AREA;
                }
                break;
            case STRAFE_OUT_OF_STONE_AREA:
                if (drive(new Vector2(-SPEED, 0), 18, 2)) {
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
                bot.intake.setIntakePower(0.0);
                bot.grabber.closePusher();
                if (alliance == Alliance.RED) {
                    if (drive(new Vector2(0, SPEED), 72 + distanceOfSkystone, 4.5)) {
                        autoState = AutoState.BLUE_180_TO_GRAB;
                        Log.i(TAG, "strafe to foundation");
                    }
                } else {
                    if (drive(new Vector2(0, -SPEED), 68, 4.5)) {
                        autoState = AutoState.BLUE_180_TO_GRAB;
                        Log.i(TAG, "strafe to foundation");
                    }
                }
                break;
            case BLUE_180_TO_GRAB:
                if (alliance == Alliance.BLUE) {
                    if(turn(-Math.PI/2, TurnDirection.RIGHT)) {
                        autoState = AutoState.STRAFE_TO_FOUNDATION;
                        Log.i(TAG,"Back to build area");
                    }
                } else {
                    autoState = AutoState.STRAFE_TO_FOUNDATION;
                }
                break;
            case STRAFE_TO_FOUNDATION:
                if (alliance == Alliance.RED) {
                    if (drive(new Vector2(SPEED, 0), 24, 2)) {
                        autoState = AutoState.GRAB_FOUNDATION;
                        resetStartTime();
                        Log.i(TAG, "grab foundation");
                    }
                } else {
                    if (drive(new Vector2(-SPEED, 0), 24, 2)) {
                        autoState = AutoState.GRAB_FOUNDATION;
                        resetStartTime();
                        Log.i(TAG, "grab foundation");
                    }
                }
                break;
            case GRAB_FOUNDATION:
                bot.foundationGrabber.close();
                bot.grabber.closeGrabber();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.DRAG_FOUNDATION;
                    Log.i(TAG,"drag foundation");
                }
                break;
            case DRAG_FOUNDATION:
                if (alliance == Alliance.RED) {
                    if (drive(new Vector2(-SPEED, 0), 70, 4)) {
                        autoState = AutoState.RELEASE_FOUNDATION;
                        Log.i(TAG, "release foundation");
                    }
                } else {
                    if (drive(new Vector2(SPEED, 0), 70, 4)) {
                        autoState = AutoState.RELEASE_FOUNDATION;
                        Log.i(TAG, "release foundation");
                    }
                }
                break;
            case ROTATE_FOUNDATION: // Unused State
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(-SPEED/2,0), new Vector2(SPEED/2,0));
                if (bot.robotAngle < 0) {
                    bot.drivetrain.setVelocity(new Vector2(0,0));
                    resetStartTime();
                    bot.drivetrain.resetMotorEncoders();
                    Log.i(TAG,"release foundation");
                    autoState = AutoState.RELEASE_FOUNDATION;
                }
                break;
            case RELEASE_FOUNDATION:
                bot.foundationGrabber.open();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.FORE_AROUND_FOUNDATION;
                    Log.i(TAG,"fore around foundation");
                }
                break;
            case FORE_AROUND_FOUNDATION:
                if (alliance == Alliance.RED) {
                    bot.grabber.extend();
                    if (drive(new Vector2(0, -SPEED), 24, 2.5)) {
                        autoState = AutoState.STRAFE_AROUND_FOUNDATION;
                        Log.i(TAG, "strafe around foundation");
                    }
                } else {
                    if (drive(new Vector2(0, SPEED), 24, 2.5)) {
                        autoState = AutoState.STRAFE_AROUND_FOUNDATION;
                        Log.i(TAG,"strafe around foundation");
                    }
                }
                break;
            case STRAFE_AROUND_FOUNDATION:
                if (alliance == Alliance.RED) {
                    bot.grabber.extend();
                    if (drive(new Vector2(SPEED, 0), 23, 2.5)) {
                        autoState = AutoState.BLUE_180_TO_PUSH;
                        resetStartTime();
                        Log.i(TAG, "push foundation");
                    }
                } else {
                    if (drive(new Vector2(-SPEED, 0), 23, 2.5)) {
                        autoState = AutoState.BLUE_180_TO_PUSH;
                        resetStartTime();
                        Log.i(TAG, "push foundation");
                    }
                }
                break;
            case BLUE_180_TO_PUSH:
                if (alliance == Alliance.BLUE) {
                    if(turn(-Math.PI/2, TurnDirection.LEFT)) {
                        autoState = AutoState.PUSH_FOUNDATION;
                        Log.i(TAG,"Back to build area");
                    }
                } else {
                    autoState = AutoState.PUSH_FOUNDATION;
                }
                break;
            case PUSH_FOUNDATION:
                if (drive(new Vector2(0, SPEED), 10, 1.5)) {
                    autoState = AutoState.EXTEND_GRABBER;
                    Log.i(TAG,"extend grabber");
                }
                break;
            case BACK_FROM_FOUNDATION:
                if (drive(new Vector2(-SPEED, 0), 12, 2)) {
                    autoState = AutoState.TURN_90_TO_DROP_STONE;
                    Log.i(TAG,"Turn 90 to drop stone");
                }
                break;
            case TURN_90_TO_DROP_STONE:
                if (turn(Math.PI/2, TurnDirection.RIGHT)) {
                    autoState = AutoState.EXTEND_GRABBER;
                    resetStartTime();
                    Log.i(TAG,"extend grabber");
                }
                break;
            case EXTEND_GRABBER:
                autoState = AutoState.DROP_STONE;
                break;
            case DROP_STONE:
                bot.grabber.openGrabber();
                if (getRuntime() > 1.5) {
                    autoState = AutoState.RECORRECT_90_PARK;
                    resetStartTime();

                }
                break;
            case RECORRECT_90_PARK:
                if(turn(Math.PI/2, TurnDirection.LEFT)) {
                    autoState = AutoState.STOP;
                    Log.i(TAG,"STOP");
                }
                break;
            case PARK:
                if (drive(new Vector2(0.0, -SPEED), 20, 2)) {
                    autoState = AutoState.STOP;
                    Log.i(TAG,"stop");
                }
                break;
            case STOP:
                break;
        }

        bot.updateRobotPosition();
        bot.setRobotAngle();
        bot.drivetrain.refreshMotors();

        telemetry.addData("actual angle", bot.getGyroAngleZ());
        telemetry.addData("inches", Math.sqrt(Math.pow(bot.getLeftForeDistanceInches(), 2) + Math.pow(bot.getStrafeDistanceInches(), 2)));
        telemetry.addData("\nmotor power", bot.drivetrain.leftFore.getPower());
        telemetry.addData("fore inch", bot.getLeftForeDistanceInches());
        telemetry.addData("strafe inch", bot.getStrafeDistanceInches());
    }

    @Override
    public void stop() {
        detector.close();
    }

    // Returns true if the bot has reached the desired encoder limit
    private boolean drive(Vector2 velocity, double inches, double timer) {
        Vector2 v = new Vector2(velocity);
        if (alliance == Alliance.BLUE) {
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

    private boolean turn(double angle, TurnDirection dir) {
        double turnSpeed = dir.getSpeed();

        if (alliance == Alliance.BLUE) {
            turnSpeed = -turnSpeed;
            angle = -angle;
        }

        bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(), new Vector2(turnSpeed, 0.0));

<<<<<<< HEAD
        if (bot.robotAngle != angle) {
            bot.drivetrain.setVelocity(new Vector2(0,0));
            bot.drivetrain.refreshMotors();
            resetStartTime();
            bot.drivetrain.resetMotorEncoders();
            return true;
=======
        if (dir == TurnDirection.LEFT) {
            if (bot.robotAngle > angle && alliance == Alliance.RED) {
                bot.drivetrain.setVelocity(new Vector2(0,0));
                bot.drivetrain.refreshMotors();
                resetStartTime();
                bot.drivetrain.resetMotorEncoders();
                return true;
            }
            if (bot.robotAngle < angle && alliance == Alliance.BLUE) {
                bot.drivetrain.setVelocity(new Vector2(0,0));
                bot.drivetrain.refreshMotors();
                resetStartTime();
                bot.drivetrain.resetMotorEncoders();
                return true;
            }
        } else if (dir == TurnDirection.RIGHT) {
            if (bot.robotAngle < angle && alliance == Alliance.RED) {
                bot.drivetrain.setVelocity(new Vector2(0,0));
                bot.drivetrain.refreshMotors();
                resetStartTime();
                bot.drivetrain.resetMotorEncoders();
                return true;
            }
            if (bot.robotAngle > angle && alliance == Alliance.BLUE) {
                bot.drivetrain.setVelocity(new Vector2(0,0));
                bot.drivetrain.refreshMotors();
                resetStartTime();
                bot.drivetrain.resetMotorEncoders();
                return true;
            }
>>>>>>> f1c2c1185ad2f13b1ee8bce26733ccbc47963afe
        }
        return false;
    }
}

