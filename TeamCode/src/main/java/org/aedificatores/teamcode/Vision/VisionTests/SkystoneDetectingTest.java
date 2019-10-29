package org.aedificatores.teamcode.Vision.VisionTests;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Universal.TelemetryLogger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.io.IOException;
import java.util.ArrayList;

@TeleOp(name = "Skystone Detecting Test")
public class SkystoneDetectingTest extends OpMode {
    OpenCvInternalCamera phoneCam;
    SkystoneDetectingPipeline pipe;

    private static final int SCREEN_WIDTH = 240;
    private static final int SCREEN_HEIGHT = 320;

    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = new OpenCvInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.openCameraDevice();
        pipe = new SkystoneDetectingPipeline();
        phoneCam.setPipeline(pipe);

        phoneCam.startStreaming(320, 240);
    }

    @Override
    public void loop() {
        telemetry.addData("Average",pipe.average);
        for (int i : pipe.blockPixelPositions) {
            telemetry.addData("Block Pose",i);
        }

    }

    class SkystoneDetectingPipeline extends OpenCvPipeline {
        Mat hsvImage;
        Mat bgrImage;
        Mat threshold;
        Mat thresholdAfterROI;
        Rect roi;

        Mat colSum;
        int[] colSumArray;
        int[] colSumRunningAverage;
        int[] colSumRunningAverageDeriv;
        int derivMax = 0, derivMin = 0;

        final int RUNNING_AVG_WINDOW_SIZE = 6;
        int average = 0;



        int[] blockPixelPositions;

        private int H_MIN = 0,
                S_MIN = 185,
                V_MIN = 40,
                H_MAX = 70,
                S_MAX = 255,
                V_MAX = 255;

        SkystoneDetectingPipeline() {
            hsvImage = new Mat();
            bgrImage = new Mat();
            threshold = new Mat();
            thresholdAfterROI = new Mat();

            colSum = new Mat();
            colSumArray = new int[SCREEN_WIDTH];
            colSumRunningAverage = new int[colSumArray.length- RUNNING_AVG_WINDOW_SIZE];
            colSumRunningAverageDeriv = new int[colSumRunningAverage.length - 1];
            roi = new Rect(0, 170, 240, 150);

            blockPixelPositions = new int[2];
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

            Core.reduce(thresholdAfterROI, colSum, 0, Core.REDUCE_SUM, CvType.CV_32S);

            colSum.get(0,0,colSumArray);

            for (int i = 0; i < colSumRunningAverage.length; ++i) {
                int sum = 0;

                for (int j = 0; j < RUNNING_AVG_WINDOW_SIZE; ++j){
                    sum += colSumArray[i+j];
                }
                colSumRunningAverage[i] = sum / RUNNING_AVG_WINDOW_SIZE;
            }

            for (int i = 0; i < colSumRunningAverageDeriv.length; ++i) {
                colSumRunningAverageDeriv[i] = colSumRunningAverage[i+1] - colSumRunningAverage[i];
            }

            for (int i=0; i < colSumRunningAverageDeriv.length; ++i) {
                if (colSumRunningAverageDeriv[i] < derivMin) {
                    derivMin = colSumRunningAverageDeriv[i];
                    blockPixelPositions[0] = i + SCREEN_WIDTH / 12;
                }
            }

            blockPixelPositions[1] = (blockPixelPositions[0] + SCREEN_WIDTH/2) % SCREEN_WIDTH;

            for (int i : colSumRunningAverage) {
                average += i;
            }

            average /= colSumRunningAverage.length;

            /*{
                int i=0;
                while (colSumRunningAverage[i] < average){
                    ++i;
                    if (i < colSumRunningAverage.length){
                        break;
                    }
                }

                // adds the xpos of the center of the first detected block
                blockPixelPositions[0] = (i+SCREEN_WIDTH/12);

                // if we know one we know the other
                blockPixelPositions[0] = ((i+SCREEN_WIDTH/2+SCREEN_WIDTH/12) % SCREEN_WIDTH);
            }*/
            return input;


        }

        @Override
        public void onViewportTapped() {
            TelemetryLogger graph;
            try {
                graph = new TelemetryLogger();

                for (int i = 0; i < colSumRunningAverageDeriv.length; ++i) {
                    graph.writeToLogInCSV(colSumRunningAverage[i],colSumRunningAverageDeriv[i]);
                }

                for (int blockPixelPosition : blockPixelPositions) {
                    graph.writeToLogInCSV(blockPixelPosition);
                }

                graph.close();
            } catch (IOException e) {
                Log.e("SkystoneDetetingTest",e.getMessage());
            }
        }
    }
}
