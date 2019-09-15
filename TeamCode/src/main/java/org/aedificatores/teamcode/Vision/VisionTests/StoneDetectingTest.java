package org.aedificatores.teamcode.Vision.VisionTests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

@TeleOp(name = "Stone Detecting Test")
public class StoneDetectingTest extends OpMode {

    OpenCvCamera phoneCam;
    StonePipeline pipe;

    public void init() {
        // Initialize monitor view on rc phone, as well as phone camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = new OpenCvInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.openCameraDevice();
        pipe = new StonePipeline();
        phoneCam.setPipeline(pipe);

        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
    }

    public void loop() {

    }

    public void stop() {
        phoneCam.stopStreaming();
        pipe.close();
    }
}

class StonePipeline extends OpenCvPipeline {
    private int H_MIN = 0,
            S_MIN = 185,
            V_MIN = 0,
            H_MAX = 44,
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

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, bgrImage, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(bgrImage, hsvImage, Imgproc.COLOR_BGR2HSV_FULL);

        Core.inRange(hsvImage,
                new Scalar(H_MIN, S_MIN, V_MIN),
                new Scalar(H_MAX, S_MAX, S_MAX),
                threshold);

        Mat erosionFactor = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Mat dilationFactor = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));

        Imgproc.erode(threshold, threshold, erosionFactor);
        Imgproc.erode(threshold, threshold, erosionFactor);

        Imgproc.dilate(threshold, threshold, dilationFactor);
        Imgproc.dilate(threshold, threshold, dilationFactor);

        Mat colSum = new Mat();
        Core.reduce(threshold, colSum, 0, Core.REDUCE_SUM, 4);
        return threshold;
    }

    public void close() {
        threshold.release();
        bgrImage.release();
        hsvImage.release();
    }
}