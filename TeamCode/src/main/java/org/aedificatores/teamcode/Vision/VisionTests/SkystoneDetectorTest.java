package org.aedificatores.teamcode.Vision.VisionTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Vision.SkystoneDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

@Autonomous(name = "Skystone Detector Test")
public class SkystoneDetectorTest extends OpMode {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;

    OpenCvCamera cam;
    SkystoneDetector pipe;

    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        cam.openCameraDevice();
        pipe = new SkystoneDetector(WIDTH, HEIGHT);
        cam.setPipeline(pipe);

        cam.startStreaming(WIDTH, HEIGHT, OpenCvCameraRotation.UPRIGHT);
    }

    @Override
    public void loop() {
        telemetry.addData("die roll", pipe.dieRoll);
        telemetry.addData("blockpos", pipe.blockPixelPosition);

    }

    @Override
    public void stop() {
        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
    }
}
