package org.aedificatores.teamcode.OpModes.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.DriveConstants;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Vision.RingDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(name = "OneWobbleAuto")
@Config
public class OneWobbleAuto extends OpMode {

    enum AutoState {
        DRIVE_TO_DEPOSIT,
        DROP_WOBBLE,
        DRIVE_BACK_SECOND_WOBBLE,
        DRIVE_TO_DEPOSIT_SECOND_WOBBLE,
        DROP_SECOND_WOBBLE
    }

    public static Vector2d SIDE_NEAR_POS = new Vector2d(-36.0, 0.0);
    public static Vector2d SIDE_FAR_POS = new Vector2d(-36.0, -48.0);
    public static Vector2d MIDDLE_POS = new Vector2d(-12.0, -20.0);

    enum WobblePosition {
        SIDE_NEAR(new Vector2d(-36.0, 0.0)),
        SIDE_FAR(new Vector2d(-36.0, -48.0)),
        MIDDLE(new Vector2d(-12.0, -20.0));

        Vector2d pos;
        WobblePosition(Vector2d pos) {
            this.pos = pos;
        }
        public Vector2d getPos() {
            return pos;
        }
    }

    SawronBot bot;
    Trajectory traj;
    Pose2d startPose = new Pose2d(0.0, 72.0 - 17.0/2.0, Math.PI/2);
    WobblePosition wobblePosition;

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    OpenCvCamera cam;
    RingDetector pipe;

    @Override
    public void init() {
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"));

        pipe = new RingDetector(WIDTH, HEIGHT);
        cam.setPipeline(pipe);

        cam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                cam.startStreaming(WIDTH, HEIGHT, OpenCvCameraRotation.UPRIGHT);
            }
        });

        bot = new SawronBot(hardwareMap);
        bot.drivetrain.setPoseEstimate(startPose);

    }

    @Override
    public void init_loop() {
        telemetry.addData("Ring Stack", pipe.getRingStackType());
    }

    @Override
    public void start() {

        switch (pipe.getRingStackType()) {
            case ONE:
                wobblePosition = WobblePosition.MIDDLE;
                break;
            case QUAD:
                wobblePosition = WobblePosition.SIDE_FAR;
                break;
            default:
                wobblePosition = WobblePosition.SIDE_NEAR;
        }



        traj = bot.drivetrain.trajectoryBuilder(startPose)
                .splineToConstantHeading(new Vector2d(6.0, 0.0), Math.PI/2)
                .splineToConstantHeading(wobblePosition.getPos(),Math.PI/2)
                .build();
        bot.drivetrain.followTrajectoryAsync(traj);
    }

    @Override
    public void loop() {
        bot.drivetrain.update();
        telemetry.addData("Wobble Position", wobblePosition);
    }

    @Override
    public void stop() {
        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
    }
}
