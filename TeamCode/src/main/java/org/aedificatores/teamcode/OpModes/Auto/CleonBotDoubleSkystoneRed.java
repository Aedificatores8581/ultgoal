package org.aedificatores.teamcode.OpModes.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.TelemetryLogger;
import org.aedificatores.teamcode.Vision.SkystoneDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.json.JSONException;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.io.IOException;

@Autonomous(name = "Cleon Bot Double Skystone Red")
public class CleonBotDoubleSkystoneRed extends OpMode {

    private static final String TAG = "Cleon2SkystoneV1Red";

    // Autonomous state machine enum
    // Yes I know, this is a very poor implementation of a state machine (lots of repeated states)
    enum AutoState {
        STRAFE_FROM_INIT_WALL("strafe from init wall"),
        LINEUP_WITH_STONE("line up with stone"),
        DRIVE_TO_STONE("drive to stone"),
        GET_STONE("intake Stone"),
        LIFT_STONE("lift stone"),
        STRAFE_OUT_OF_STONE_AREA("strafe out of stone area"),
        RECORRECT_TO_90("recorrect to 90"),
        BACK_TO_BUILD_AREA("back to build area"),
        STRAFE_TO_FOUNDATION("strafe to foundation"),
        DROP_STONE("drop stone"),
        BACK_UP_FROM_FOUNDATION("back up from foundation"),
        BACK_TO_LOAD_AREA("back to load area"),
        LOWER_SIDE_GRABBER("lower side grabber"),
        STRAFE_TO_SECOND_STONE("strafe to second stone"),
        GET_SECOND_STONE("get second stone"),
        LIFT_STONE_AGAIN("lift stone again"),
        STRAFE_OUT_OF_STONE_AREA_AGAIN("strafe out of stone area again"),
        BACK_TO_BUILD_AREA_AGAIN("back to build area again"),
        STRAFE_TO_FOUNDATION_AGAIN("strafe to foundation again"),
        DROP_STONE_AND_GET_FOUNDATION("drop stone again"),
        DRAG_FOUNDATION("drag foundation"),
        TURN_FOUNDATION("turn foundation"),
        RELEASE_FOUNDATION("release foundation"),
        FORE_AROUND_FOUNDATION("fore around foundation"),
        STRAFE_AROUND_FOUNDATION("strafe around foundation"),
        BUMP_FOUNDATION("bump foundation"),
        PARK("park"),
        STOP("stop");

        String msg;
        AutoState(String s) {
            msg = s;
        }

        @Override
        public String toString() {
            return msg;
        }
    }


    enum ParkPosition {
        NEAR_CENTER(30, "NEAR_CENTER"),
        NEAR_WALL(0, "NEAR_WALL");

        private double dist;
        private String desc;
        ParkPosition(double dist, String desc) {
            this.dist = dist;
            this.desc = desc;
        }

        public double getDist() {
            return dist;
        }

        public String toString() {
            return desc;
        }
    }

    private static final int secondSkystoneOffset[] = {12, 6, 12};

    private static ParkPosition parkPosition;
    private static AutoState autoState;

    private static CleonBot bot;
    TelemetryLogger logger;

    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;
    private static OpenCvCamera cam;
    private static SkystoneDetector detector;

    private static Vector2 skystoneVector;
    private static Vector2 skystoneDriveVec;

    int dieRoll;
    @Override
    public void init() {
        Log.i(TAG, "Initializing CleonBotSingleSkystoneAuto");
        // Initialize phone camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        cam.openCameraDevice();

        detector = new SkystoneDetector(SCREEN_WIDTH, SCREEN_HEIGHT);
        cam.setPipeline(detector);
        cam.startStreaming(SCREEN_WIDTH, SCREEN_HEIGHT, OpenCvCameraRotation.UPRIGHT);

        Log.i(TAG, "Phone Camera Initialized");

        autoState = AutoState.STRAFE_FROM_INIT_WALL;
        parkPosition = ParkPosition.NEAR_CENTER;

        try {
            logger = new TelemetryLogger();
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
            telemetry.addLine(e.getMessage());

            Log.e(TAG,"Stack Trace: ");
            telemetry.addLine("Stack Trace: ");

            for (StackTraceElement i : e.getStackTrace()) {
                Log.e(TAG,"\t" + i.toString());
                telemetry.addLine("\t" + i.toString());
            }
        }

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

        skystoneVector = new Vector2();
        skystoneDriveVec = new Vector2();
        bot.foundationGrabber.open();
        Log.i(TAG, "Init Finished");
    }

    @Override
    public void init_loop() {
        if (gamepad1.a) parkPosition = ParkPosition.NEAR_CENTER;
        if (gamepad1.b) parkPosition = ParkPosition.NEAR_WALL;

        telemetry.addData("Park Position", parkPosition.toString());
    }

    @Override
    public void start() {
        super.start();


        // TODO: Edit distance of Skystone components
        switch (detector.dieRoll) {
            case 1:
                skystoneVector.x = 29.5;
                skystoneVector.y = 0;
                skystoneDriveVec = CleonBot.DriveVecConstants.FORE;
                break;
            case 2:
                skystoneVector.x = 29.5;
                skystoneVector.y = 0.2;
                skystoneDriveVec = CleonBot.DriveVecConstants.BACK;
                break;
            case 3:
                skystoneVector.x = 29.5;
                skystoneVector.y = -5;
                skystoneDriveVec = CleonBot.DriveVecConstants.BACK;
                break;

        }

        dieRoll = detector.dieRoll;
        cam.stopStreaming();
        detector.close();
        bot.drivetrain.resetMotorEncoders();
        resetStartTime();
        bot.resetTimer();
        if (dieRoll == 1) {
            bot.frontSideGrabber.init();
            bot.backSideGrabber.holdBlockPos();
        }
        else if (dieRoll == 3 || dieRoll == 2) {
            bot.backSideGrabber.init();
            bot.frontSideGrabber.holdBlockPos();
        }
        try {
            logger.writeToLogInCSV("state","RFore","LFore","Strafe","angle");
        } catch (IOException e) {
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
        switch (autoState) {
            case STRAFE_FROM_INIT_WALL:
                if (bot.driveStrafePID( 12, 0)) {
                    autoState = AutoState.LINEUP_WITH_STONE;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case LINEUP_WITH_STONE:
                if (bot.driveForePID(skystoneVector.y, 0)) {
                    autoState = AutoState.DRIVE_TO_STONE;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case DRIVE_TO_STONE:
                if (bot.driveStrafePID(skystoneVector.x - 16, 0)) {
                    autoState = AutoState.GET_STONE;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case GET_STONE:
                if (dieRoll == 1) {
                    bot.frontSideGrabber.closeGrabber();
                    bot.frontSideGrabber.moveDownGrab();
                } else if (dieRoll == 3 || dieRoll == 2) {
                    bot.backSideGrabber.closeGrabber();
                    bot.backSideGrabber.moveDownGrab();
                }
                if (getRuntime() > .5){
                    autoState = AutoState.LIFT_STONE;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case LIFT_STONE:
                bot.backSideGrabber.holdBlockPos();
                bot.frontSideGrabber.holdBlockPos();
                if (getRuntime() > .25){
                    autoState = AutoState.STRAFE_OUT_OF_STONE_AREA;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case STRAFE_OUT_OF_STONE_AREA:
                if (bot.driveStrafePID(-8, 0)) {
                    autoState = AutoState.RECORRECT_TO_90;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case RECORRECT_TO_90:
                if (bot.turnPID(0, CleonBot.TurnDirection.LEFT, 4000)) {
                    autoState = AutoState.BACK_TO_BUILD_AREA;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case BACK_TO_BUILD_AREA:
                if (bot.driveForePID(-(67 - skystoneVector.y), 0)) {
                    autoState = AutoState.STRAFE_TO_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    bot.resetTimer();
                    resetStartTime();
                }
                break;
            case STRAFE_TO_FOUNDATION:
                if (bot.driveStrafePID(11,0)) {
                    autoState = AutoState.DROP_STONE;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case DROP_STONE:
                bot.backSideGrabber.openGrabber();
                bot.frontSideGrabber.openGrabber();
                if (getRuntime() > .5) {
                    autoState = AutoState.BACK_UP_FROM_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case BACK_UP_FROM_FOUNDATION:
                if (bot.driveStrafePID(-9, 0)) {
                    autoState = AutoState.BACK_TO_LOAD_AREA;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case BACK_TO_LOAD_AREA:
                if (bot.driveForePID(67 - skystoneVector.y + secondSkystoneOffset[dieRoll - 1], 0)) {
                    autoState = AutoState.LOWER_SIDE_GRABBER;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case LOWER_SIDE_GRABBER:
                if (dieRoll == 3) {
                    bot.backSideGrabber.moveDownPush();
                } else {
                    bot.frontSideGrabber.moveDownPush();
                }
                if (getRuntime() > .75){
                    autoState = AutoState.STRAFE_TO_SECOND_STONE;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STRAFE_TO_SECOND_STONE:
                if (bot.driveStrafePID(12, 0)) {
                    autoState = AutoState.GET_SECOND_STONE;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case GET_SECOND_STONE:
                if (dieRoll == 3) {
                    bot.backSideGrabber.closeGrabber();
                    bot.backSideGrabber.moveDownGrab();
                } else {
                    bot.frontSideGrabber.closeGrabber();
                    bot.frontSideGrabber.moveDownGrab();
                }
                if (getRuntime() > .5){
                    autoState = AutoState.LIFT_STONE_AGAIN;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case LIFT_STONE_AGAIN:
                bot.backSideGrabber.holdBlockPos();
                bot.frontSideGrabber.holdBlockPos();
                if (getRuntime() > .25){
                    autoState = AutoState.STRAFE_OUT_OF_STONE_AREA_AGAIN;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case STRAFE_OUT_OF_STONE_AREA_AGAIN:
                if (bot.driveStrafePID(-14, 0)) {
                    autoState = AutoState.BACK_TO_BUILD_AREA_AGAIN;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case BACK_TO_BUILD_AREA_AGAIN:
                if (bot.driveForePID(-(76 - skystoneVector.y + secondSkystoneOffset[dieRoll - 1]), 0)) {
                    autoState = AutoState.STRAFE_TO_FOUNDATION_AGAIN;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STRAFE_TO_FOUNDATION_AGAIN:
                if (bot.driveStrafePID(20, 0)) {
                    autoState = AutoState.DROP_STONE_AND_GET_FOUNDATION;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case DROP_STONE_AND_GET_FOUNDATION:
                bot.backSideGrabber.openGrabber();
                bot.frontSideGrabber.openGrabber();
                bot.foundationGrabber.close();
                if (getRuntime() > .5) {
                    autoState = AutoState.DRAG_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case DRAG_FOUNDATION:
                if (bot.drivePID(CleonBot.DriveVecConstants.STRAFE_LEFT,0,100, 4000)) {
                    autoState = AutoState.TURN_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case TURN_FOUNDATION:
                if (bot.turnPID(-Math.PI/2, CleonBot.TurnDirection.RIGHT, 700)) {
                    autoState = AutoState.RELEASE_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case RELEASE_FOUNDATION:
                bot.foundationGrabber.open();
                autoState = AutoState.FORE_AROUND_FOUNDATION;
                Log.i(TAG, String.valueOf(autoState));
                resetStartTime();
                bot.resetTimer();
                break;
            case FORE_AROUND_FOUNDATION:
                if (bot.drivePID(CleonBot.DriveVecConstants.STRAFE_LEFT,-Math.PI/2, 20, 3000)) {
                    autoState = AutoState.STRAFE_AROUND_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STRAFE_AROUND_FOUNDATION:
                if (bot.drivePID(CleonBot.DriveVecConstants.FORE,-Math.PI/2, parkPosition.getDist(), 1500)) {
                    autoState = AutoState.BUMP_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case BUMP_FOUNDATION:
                if (bot.drivePID(CleonBot.DriveVecConstants.STRAFE_RIGHT,-Math.PI/2, 100, 7500)) {
                    autoState = AutoState.PARK;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case PARK:
                if (bot.driveStrafePID(30, -Math.PI/2)) {
                    autoState = AutoState.STOP;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STOP:
                bot.grabber.openPusher();
                break;
        }

        telemetry.addData("state",autoState.toString());
        telemetry.addData("die",dieRoll);
        telemetry.addData("sky inch", skystoneVector.y);
        telemetry.addData("angle (degrees)",bot.getGyroAngleZ());
        telemetry.addData("dist", Math.sqrt(Math.pow(bot.getRightForeDistanceInches(), 2) + Math.pow(bot.getStrafeDistanceInches(), 2)));
        telemetry.addData("fore", bot.getRightForeDistanceInches());
        telemetry.addData("strafe", bot.getStrafeDistanceInches());
        telemetry.addData("timer", getRuntime());
        bot.drivetrain.refreshMotors();
        bot.updateRobotPosition2d();
        bot.updateTimer();


        try {
            logger.writeToLogInCSV(autoState.toString(),
                    bot.getRightForeDistanceInches(),
                    bot.getLeftForeDistanceInches(),
                    bot.getStrafeDistanceInches(),
                    bot.robotAngle);
        } catch (IOException e) {
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

}
