package org.aedificatores.teamcode.Vision.VisionTests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

@Autonomous(group = OpModeGroups.UNIVERSAL)
public class BlankPipelineTest extends OpMode {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;

    OpenCvCamera cam;
    BlankPipeline pipe;
    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"), cameraMonitorViewId);

        pipe = new BlankPipeline();
        cam.setPipeline(pipe);

        cam.openCameraDeviceAsync(() -> cam.startStreaming(WIDTH, HEIGHT, OpenCvCameraRotation.UPRIGHT));
    }

    @Override
    public void loop() {

    }
}

class BlankPipeline extends OpenCvPipeline {

    @Override
    public Mat processFrame(Mat input) {
        return input;
    }
}