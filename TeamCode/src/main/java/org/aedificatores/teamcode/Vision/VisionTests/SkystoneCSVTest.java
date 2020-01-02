package org.aedificatores.teamcode.Vision.VisionTests;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Universal.TelemetryLogger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.io.IOException;
import java.util.ArrayList;

@Disabled
@TeleOp(name = "Skystone CSV Test")
public class SkystoneCSVTest extends OpMode {
    OpenCvInternalCamera phoneCam;
    SkystoneCSVPipeline pipe;

    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;
    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.openCameraDevice();
        pipe = new SkystoneCSVPipeline();
        phoneCam.setPipeline(pipe);

        phoneCam.startStreaming(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    public void loop() {

    }

    private class SkystoneCSVPipeline extends OpenCvPipeline {
        Mat hsvImage;
        Mat bgrImage;
        Mat threshold;
        Mat thresholdAfterROI;

        Mat colSum;
        Rect roi;


        private int H_MIN = 0,
                S_MIN = 185,
                V_MIN = 40,
                H_MAX = 70,
                S_MAX = 255,
                V_MAX = 255;

        public SkystoneCSVPipeline() {
            hsvImage = new Mat();
            bgrImage = new Mat();
            threshold = new Mat();
            thresholdAfterROI = new Mat();
            colSum = new Mat();
            roi = new Rect(0, 170, 240, 150);
        }

        @Override
        public Mat processFrame(Mat input) {
            // For whatever reason, OpenCV requires you to do some weird acrobatics to convert
            // RGBA to HSV, that's what is done here
            Imgproc.cvtColor(input, bgrImage, Imgproc.COLOR_RGBA2BGR);
            Imgproc.cvtColor(bgrImage, hsvImage, Imgproc.COLOR_BGR2HSV_FULL);

            // Threshold hsv values
            Core.inRange(hsvImage,
                    new Scalar(H_MIN, S_MIN, V_MIN),
                    new Scalar(H_MAX, S_MAX, V_MAX),
                    threshold);
            thresholdAfterROI = threshold.submat(roi);

            Imgproc.cvtColor(threshold, bgrImage, Imgproc.COLOR_GRAY2BGR);
            Imgproc.cvtColor(bgrImage, input, Imgproc.COLOR_BGR2RGBA);
            Imgproc.rectangle(input, roi, new Scalar(0,255,0), 4);

            Core.reduce(thresholdAfterROI, colSum, 0, Core.REDUCE_SUM, 4);

            return input;
        }

        @Override
        public void onViewportTapped() {
            TelemetryLogger graph;
            try {
                graph = new TelemetryLogger();

                ArrayList<Integer> logRow = new ArrayList<>();
                int[] temp = new int[1];

                for (int i = 0; i < colSum.cols(); ++i){
                    colSum.get(0, i, temp);
                    graph.writeToLogInCSV(i,temp[0]);
                }

                graph.close();
            } catch (IOException e) {
                Log.e("SkystoneDetetingTest",e.getMessage());
            }
        }
    }
}
