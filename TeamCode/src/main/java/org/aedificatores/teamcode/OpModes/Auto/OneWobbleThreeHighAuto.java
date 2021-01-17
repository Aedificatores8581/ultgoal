package org.aedificatores.teamcode.OpModes.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal.WobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Vision.RingDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(name = "OneWobbleThreeHighAuto")
@Config
public class OneWobbleThreeHighAuto extends OpMode {

    enum AutoState {
        DRIVE_TO_DEPOSIT,
        DROP_WOBBLE,
        DRIVE_TO_SHOOT,
        SHOOT,
        PARK,
        END
    }

    AutoState state = AutoState.DRIVE_TO_DEPOSIT;
    // FtcDashboard dashboard;

    public static Vector2d SIDE_NEAR_POS = new Vector2d(-36.0, 9.0);
    public static Vector2d SIDE_FAR_POS = new Vector2d(-42.0, -36.0);
    public static Vector2d MIDDLE_POS = new Vector2d(-12.0, -20.0);
    public static Pose2d SHOOT_POS = new Pose2d(-0.0, 6.0, Math.toRadians(-100));
    public static Vector2d PARK_POS = new Vector2d(-0.0, -12.0);

    enum WobblePosition {
        SIDE_NEAR(SIDE_NEAR_POS),
        SIDE_FAR(SIDE_FAR_POS),
        MIDDLE(MIDDLE_POS);

        Vector2d pos;
        WobblePosition(Vector2d pos) {
            this.pos = pos;
        }
        public Vector2d getPos() {
            return pos;
        }
    }

    SawronBot bot;
    Trajectory trajDeposit;
    Trajectory trajShoot;
    Trajectory trajPark;
    Pose2d startPose = new Pose2d(0.0, 72.0 - 17.0/2.0, Math.PI/2);
    WobblePosition wobblePosition;

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    OpenCvCamera cam;
    RingDetector pipe;
    boolean cameraStreaming = false;

    @Override
    public void init() {
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"));

        pipe = new RingDetector(WIDTH, HEIGHT);
        cam.setPipeline(pipe);

        cam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                cam.startStreaming(WIDTH, HEIGHT, OpenCvCameraRotation.UPRIGHT);
                cameraStreaming = true;
            }
        });

    //    dashboard = FtcDashboard.getInstance();
    //    dashboard.setTelemetryTransmissionInterval(25);

        bot = new SawronBot(hardwareMap);
        bot.drivetrain.setPoseEstimate(startPose);
        bot.wobbleGrabber.setMode(WobbleGrabber.Mode.TELEOP);
    }

    // Used to Account for the fact that we don't know the state of the wobble
    private boolean alreadyLifted = false;

    @Override
    public void init_loop() {
        telemetry.addData("Ring Stack", pipe.getRingStackType());
        bot.update();

        if (gamepad1.a && !alreadyLifted) {
            bot.wobbleGrabber.reset();
            alreadyLifted = true;
        }
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

        trajDeposit = bot.drivetrain.trajectoryBuilder(startPose)
                .splineToConstantHeading(new Vector2d(6.0, 0.0), Math.PI/2)
                .splineToConstantHeading(wobblePosition.getPos(),Math.PI/2)
                .build();
        trajShoot = bot.drivetrain.trajectoryBuilder(trajDeposit.end())
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(0.0, 6)), Math.PI/2)
                .splineToSplineHeading(SHOOT_POS, 0)
                .build();
        trajPark = bot.drivetrain.trajectoryBuilder(trajShoot.end())
                .splineToConstantHeading(PARK_POS, 0)
                .build();
        bot.drivetrain.followTrajectoryAsync(trajDeposit);

        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
        cameraStreaming = false;

        bot.shooter.runShooter();
        bot.wobbleGrabber.setMode(WobbleGrabber.Mode.AUTO);
    }

    @Override
    public void loop() {
        switch (state) {
            case DRIVE_TO_DEPOSIT:
                if (!bot.drivetrain.isBusy()) {
                    bot.wobbleGrabber.drop();
                    state = AutoState.DROP_WOBBLE;
                }
                break;
            case DROP_WOBBLE:
                if (bot.wobbleGrabber.isBusy()) {
                    bot.drivetrain.followTrajectoryAsync(trajShoot);
                    state = AutoState.DRIVE_TO_SHOOT;
                }
                break;
            case DRIVE_TO_SHOOT:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.SHOOT;
                }
                break;
            case SHOOT:
                if (bot.shooter.shotAllThree()) {
                    bot.drivetrain.followTrajectoryAsync(trajPark);
                    state = AutoState.PARK;
                }
                bot.shooter.advance();
                break;
            case PARK:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.END;
                }
                break;
            case END:
                bot.shooter.stopShooter();
                requestOpModeStop();
                break;
        }
        bot.update();
        telemetry.addData("Wobble Position", wobblePosition);

        // TelemetryPacket packet = new TelemetryPacket();
        // packet.put("target", bot.shooter.getTargetShooterVelocity());
        // packet.put("actual", bot.shooter.getActualShooterVelocity());
        // dashboard.sendTelemetryPacket(packet);
    }

    @Override
    public void stop() {
        if (cameraStreaming) {
            cam.stopStreaming();
            pipe.close();
            cam.closeCameraDevice();
        }
    }
}