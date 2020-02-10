package org.aedificatores.teamcode.OpModes.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon Bot Foundation Red")
public class CleonBotFoundationRed extends OpMode {
    private static final String TAG = "CleonFoundationRed";

    enum AutoState {
        BACK("back"),
        STRAFE_TO_FOUNDATION("strafe to foundation"),
        GRAB_FOUNDATION("grab foundation"),
        DRAG_FOUNDATION("drag foundation"),
        RELEASE_FOUNDATION("release foundation"),
        FORE_AROUND_FOUNDATION("fore around foundation"),
        STRAFE_AROUND_FOUNDATION("strafe around foundation"),
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

    private static AutoState autoState;
    private static ParkPosition parkPosition;

    private static CleonBot bot;

    @Override
    public void init() {
        autoState = AutoState.BACK;
        parkPosition = ParkPosition.NEAR_CENTER;

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
        bot.resetTimer();
    }

    @Override
    public void loop() {
        switch (autoState){
            case BACK:
                if (bot.drivePID(CleonBot.DriveVecConstants.BACK,0, 13, 3000)) {
                    autoState = AutoState.STRAFE_TO_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STRAFE_TO_FOUNDATION:
                if (bot.drivePID(CleonBot.DriveVecConstants.STRAFE_RIGHT, 0, 40, 3000)) {
                    autoState = AutoState.GRAB_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
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
                if (bot.drivePID(CleonBot.DriveVecConstants.STRAFE_LEFT,0,50, 5000)) {
                    autoState = AutoState.RELEASE_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case RELEASE_FOUNDATION:
                bot.foundationGrabber.open();
                if (getRuntime() > 1.0) {
                    autoState = AutoState.FORE_AROUND_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case FORE_AROUND_FOUNDATION:
                if (bot.drivePID(CleonBot.DriveVecConstants.FORE,0, 29, 3000)) {
                    autoState = AutoState.STRAFE_AROUND_FOUNDATION;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STRAFE_AROUND_FOUNDATION:
                if (bot.drivePID(CleonBot.DriveVecConstants.STRAFE_RIGHT,0, parkPosition.getDist(), 3000)) {
                    autoState = AutoState.PARK;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case PARK:
                if (bot.drivePID(CleonBot.DriveVecConstants.FORE,0, 20, 3000)) {
                    autoState = AutoState.STOP;
                    Log.i(TAG, String.valueOf(autoState));
                    resetStartTime();
                }
                break;
            case STOP:
                break;
        }

        bot.updateRobotPosition2d();
        bot.drivetrain.refreshMotors();
        bot.updateTimer();
    }
}
