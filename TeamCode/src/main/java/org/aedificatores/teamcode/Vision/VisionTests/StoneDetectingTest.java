package org.aedificatores.teamcode.Vision.VisionTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

/**
* Class for testing the detection of stones
*
* Created by: Hunter Seachrist
* */

// TODO: Cleanup This code
@TeleOp(name = "Stone Detecting Test")
public class StoneDetectingTest extends OpMode {

    Gamepad prev1;
    enum ValueEditState {H_MIN, S_MIN, V_MIN, H_MAX, S_MAX, V_MAX}
    ValueEditState veState = ValueEditState.H_MIN;

    private OpenCvCamera phoneCam;
    private StonePipeline pipe;
    private int hMin = 0,
                sMin = 0,
                vMin = 0,
                hMax = 255,
                sMax = 255,
                vMax = 255;
    public void init() {

        // Initialize monitor view on rc phone, as well as phone camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = new OpenCvInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.openCameraDevice();
        pipe = new StonePipeline();
        phoneCam.setPipeline(pipe);

        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

        prev1 = new Gamepad();

        try {
            prev1.copy(gamepad1);
        } catch (RobotCoreException e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        hMin = UniversalFunctions.clamp(0, hMin, 255);
        sMin = UniversalFunctions.clamp(0, sMin, 255);
        vMin = UniversalFunctions.clamp(0, vMin, 255);
        hMax = UniversalFunctions.clamp(0, hMax, 255);
        sMax = UniversalFunctions.clamp(0, sMax, 255);
        vMax = UniversalFunctions.clamp(0, vMax, 255);

        switch (veState) {
            case H_MIN:
                hMin += (int) (gamepad1.left_stick_y);
                break;
            case S_MIN:
                sMin += (int) (gamepad1.left_stick_y);
                break;
            case V_MIN:
                vMin += (int) (gamepad1.left_stick_y);
                break;
            case H_MAX:
                hMax += (int) (gamepad1.left_stick_y);
                break;
            case S_MAX:
                sMax += (int) (gamepad1.left_stick_y);
                break;
            case V_MAX:
                vMax += (int) (gamepad1.left_stick_y);
                break;
        }

        if (gamepad1.a && !prev1.a){
            switch (veState){
                case H_MIN: veState = ValueEditState.S_MIN; break;
                case S_MIN: veState = ValueEditState.V_MIN; break;
                case V_MIN: veState = ValueEditState.H_MAX; break;
                case H_MAX: veState = ValueEditState.S_MAX; break;
                case S_MAX: veState = ValueEditState.V_MAX; break;
                case V_MAX: veState = ValueEditState.H_MIN; break;

            }
        }

        try {
            prev1.copy(gamepad1);
        } catch (RobotCoreException e) {
            e.printStackTrace();
        }

        telemetry.addData("h_min", hMin);
        telemetry.addData("s_min", sMin);
        telemetry.addData("v_min", vMin);
        telemetry.addData("h_max", hMax);
        telemetry.addData("s_max", sMax);
        telemetry.addData("v_max", vMax);
    }

    public void stop() {
        phoneCam.stopStreaming();
        pipe.close();
    }


    class StonePipeline extends OpenCvPipeline {
        private int H_MIN = 40,
                S_MIN = 185,
                V_MIN = 0,
                H_MAX = 70,
                S_MAX = 255,
                V_MAX = 255;

        private Mat bgrImage;
        private Mat hsvImage;
        private Mat threshold;

        StonePipeline () {
            bgrImage = new Mat();
            hsvImage = new Mat();
            threshold = new Mat();
        }

        // Pipeline which thresholds the image
        @Override
        public Mat processFrame(Mat input) {
            this.H_MIN = hMin;
            this.S_MIN = sMin;
            this.V_MIN = vMin;
            this.H_MAX = hMax;
            this.S_MAX = sMax;
            this.V_MAX = vMax;

            // For whatever reason, OpenCV requires you to do some weird acrobatics to convert
            // RGBA to HSV, that's what is done here
            Imgproc.cvtColor(input, bgrImage, Imgproc.COLOR_RGBA2BGR);
            Imgproc.cvtColor(bgrImage, hsvImage, Imgproc.COLOR_BGR2HSV_FULL);

            // Threshold hsv values
            Core.inRange(hsvImage,
                    new Scalar(H_MIN, S_MIN, V_MIN),
                    new Scalar(H_MAX, S_MAX, V_MAX),
                    threshold);

            // TODO: Not sure what this does. Should figure it out
            Mat colSum = new Mat();
            Core.reduce(threshold, colSum, 0, Core.REDUCE_SUM, 4);
            return threshold;
        }

        void close() {
            threshold.release();
            bgrImage.release();
            hsvImage.release();
        }
    }

}

