package org.aedificatores.teamcode.OpModes.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Vision.SkystoneDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.json.JSONException;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.io.IOException;

@Autonomous(name = "Cleon Bot Single Skystone Version 2 Red")
public class CleonBotSingleSkystoneVersion2Red extends OpMode {

    private static final String TAG = "Cleon1SkystoneV2Red";

    enum AutoState {
        STRAFE_FROM_INIT_WALL("strafe from init wall"),
        LINEUP_WITH_STONE("line up with stone"),
        DRIVE_TO_STONE("drive to stone"),
        GET_STONE("intake Stone"),
        STRAFE_OUT_OF_STONE_AREA("strafe out of stone area"),
        RECORRECT_TO_90("recorrect to 90"),
        BACK_TO_BUILD_AREA("back to build area"),
        STRAFE_TO_FOUNDATION("strafe to foundation"),
        GRAB_FOUNDATION("grab foundation"),
        DRAG_FOUNDATION("drag foundation"),
        TURN_FOUNDATION("turn with foundation"),
        RELEASE_FOUNDATION("release foundation"),
        PUSH_FOUNDATION("push foundation"),
        DROP_STONE("drop stone"),
        DIAGONAL_TO_PARK("diagonal to park"),
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

    private static AutoState autoState;

    private static CleonBot bot;

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
    public void start() {
        super.start();


        // TODO: Edit distance of Skystone components
        switch (detector.dieRoll) {
            case 1:
                skystoneVector.x = 28;
                skystoneVector.y = 8;
                skystoneDriveVec = CleonBot.DriveVecConstants.FORE;
                break;
            case 2:
                skystoneVector.x = 28;
                skystoneVector.y = 0;
                skystoneDriveVec = CleonBot.DriveVecConstants.BACK;
                break;
            case 3:
                skystoneVector.x = 28;
                skystoneVector.y = -5;
                skystoneDriveVec = CleonBot.DriveVecConstants.BACK;
                break;

        }

        dieRoll= detector.dieRoll;
        cam.stopStreaming();
        detector.close();
        bot.drivetrain.resetMotorEncoders();
        resetStartTime();
        bot.resetTimer();
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
                if (bot.backSideGrabber.autoGetBlock()){
                    autoState = AutoState.STRAFE_OUT_OF_STONE_AREA;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case STRAFE_OUT_OF_STONE_AREA:
                if (bot.driveStrafePID(-10, 0)) {
                    autoState = AutoState.RECORRECT_TO_90;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case RECORRECT_TO_90:
                if (bot.turnPID(0, CleonBot.TurnDirection.LEFT)) {
                    autoState = AutoState.BACK_TO_BUILD_AREA;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case BACK_TO_BUILD_AREA:
                if (bot.driveForePID(-70 - skystoneVector.y, 0)) {
                    autoState = AutoState.STRAFE_TO_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STRAFE_TO_FOUNDATION:
                if (bot.driveStrafePID(14, 0)) {
                    autoState = AutoState.GRAB_FOUNDATION;
                    resetStartTime();
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case GRAB_FOUNDATION:
                bot.foundationGrabber.close();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.DRAG_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case DRAG_FOUNDATION:
                if (bot.driveStrafePID(-20, 0)) {
                    autoState = AutoState.TURN_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case TURN_FOUNDATION:
                if (bot.turnPID(-Math.PI / 2, CleonBot.TurnDirection.LEFT)) {
                    autoState = AutoState.RELEASE_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case RELEASE_FOUNDATION:
                bot.foundationGrabber.open();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.PUSH_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;

            case PUSH_FOUNDATION:
                if (bot.driveForePID(-9, 0)) {
                    autoState = AutoState.DROP_STONE;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case DROP_STONE:
                bot.backSideGrabber.openGrabber();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.DIAGONAL_TO_PARK;
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case DIAGONAL_TO_PARK:
                if (bot.drivePID(new Vector2(1,-1),0,36, 6000)) {
                    autoState = AutoState.PARK;
                    Log.i(TAG, String.valueOf(autoState));
                }
                break;
            case PARK:
                if (bot.driveForePID(24, 0)) {
                    autoState = AutoState.STOP;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STOP:
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
    }
}
