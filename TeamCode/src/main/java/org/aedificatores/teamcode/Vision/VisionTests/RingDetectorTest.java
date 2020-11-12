package org.aedificatores.teamcode.Vision.VisionTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Vision.RingDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

@Autonomous(name = "Ring Detector Test")
public class RingDetectorTest extends OpMode {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;

    OpenCvCamera cam;
    RingDetector pipe;

    @Override
    public void init() {
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"));

        cam.openCameraDevice();
        pipe = new RingDetector(WIDTH, HEIGHT);
        cam.setPipeline(pipe);

        cam.startStreaming(WIDTH, HEIGHT, OpenCvCameraRotation.UPRIGHT);
    }

    @Override
    public void loop() {
        telemetry.addData("Ring Stack Type", pipe.getRingStackType());
        telemetry.addData("Sum",pipe.getImageSum());

    }

    @Override
    public void stop() {
        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
    }
}