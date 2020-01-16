package org.aedificatores.teamcode.Vision.VisionTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;


@Autonomous(name = "Vision Mask Test")
public class VisionMaskTest extends OpMode {
    OpenCvInternalCamera phoneCam;
    OpenCvCamera webCam;
    MaskPipeline pipe;

    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 240;

    int widthAdjust;
    int heightAdjust;

    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        // phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        webCam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class,"Webcam 1"), cameraMonitorViewId);

        // phoneCam.openCameraDevice();
        webCam.openCameraDevice();
        pipe = new MaskPipeline(320,240);
        // phoneCam.setPipeline(pipe);
        webCam.setPipeline(pipe);
        // phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
        webCam.startStreaming(320,240, OpenCvCameraRotation.UPRIGHT);
    }

    public void init_loop() {

    }

    @Override
    public void loop() {

        if(gamepad1.dpad_right) {
            widthAdjust = 1;
        } else if(gamepad1.dpad_left) {
            widthAdjust = -1;
        } else {
            widthAdjust = 0;
        }

        if(gamepad1.dpad_down) {
            heightAdjust = 1;
        } else if(gamepad1.dpad_up) {
            heightAdjust = -1;
        } else {
            heightAdjust = 0;
        }

        pipe.adjustRect((int)(gamepad1.left_stick_x * 2),
                        (int)(gamepad1.left_stick_y * 2),
                        widthAdjust,
                        heightAdjust);

        telemetry.addData("width",pipe.roi.width);
        telemetry.addData("height",pipe.roi.height);
        telemetry.addData("x",pipe.roi.x);
        telemetry.addData("y",pipe.roi.y);

    }

    public void stop() {
        pipe.close();
    }

    private class MaskPipeline extends OpenCvPipeline {
        public Rect roi;
        Mat inputRotate;
        Mat retMat;
        Mat roiMat;
        Mat blackMat;

        int inputWidth, inputHeight;
        MaskPipeline(int width, int height) {
            roi = new Rect(0,0,width,height);
            inputWidth = width;
            inputHeight = height;

            roiMat = new Mat();
            retMat = new Mat();
            blackMat = new Mat(height, width, CvType.CV_8UC4, new Scalar(0,0,0,0));
        }

        void adjustRect(int x, int y, int width, int height) {
            if (!(roi.x + x < 0 || roi.x + x + roi.width + width > inputWidth || roi.width + width <= 0)) {
                roi.x += x;
                roi.width += width;
            }
            if (!(roi.y + y < 0 || roi.y + y + roi.height + height > inputHeight || roi.height + height <= 0)) {
                roi.y += y;
                roi.height += height;
            }
        }

        @Override
        public Mat processFrame(Mat input) {
            Imgproc.rectangle(input, roi, new Scalar(0, 255, 0), 4);
            roiMat = input.submat(roi);
            blackMat.copyTo(retMat);
            roiMat.copyTo(retMat.rowRange(roi.y, roi.y + roi.height).colRange(roi.x, roi.x + roi.width));
            return retMat;
        }

        void close() {
            retMat.release();
            blackMat.release();
            roiMat.release();
        }
    }
}
