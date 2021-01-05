package org.aedificatores.teamcode.OpModes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.profile.SimpleMotionConstraints;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Drivetrains.DriveConstants;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Vision.RingDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.jetbrains.annotations.NotNull;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.Arrays;

@Autonomous(name = "TwoWobbleThreeHighAuto")
public class TwoWobbleThreeHigh extends OpMode {
    enum AutoState {
        DRIVE_TO_DEPOSIT,
        DROP_WOBBLE,
        DRIVE_BACK,
        GET_SECOND_WOBBLE,
        DRIVE_TO_SECOND_DEPOSIT,
        DROP_SECOND_WOBBLE,
        DRIVE_TO_SHOOT_ONE,
        SHOOT_ONE,
        DRIVE_TO_SHOOT_TWO,
        SHOOT_TWO,
        DRIVE_TO_SHOOT_THREE,
        SHOOT_THREE,
        PARK,
        END
    }

    AutoState state = AutoState.DRIVE_TO_DEPOSIT;
    private static final Pose2d START_POSE = new Pose2d(-(72.0 - 17.0/2.0), -24, Math.PI);
    private static final Vector2d POINT_AVOID_RINGS = new Vector2d(-24, -24);
    private static Vector2d SIDE_NEAR_POS = new Vector2d(0.0,-60.0);
    private static Vector2d SIDE_FAR_POS = new Vector2d(48, -60.0);
    private static Vector2d MIDDLE_POS = new Vector2d(20.0, -36);
    private static  Pose2d SECOND_WOBBLE_LINEUP = new Pose2d(-12, -51, 0);
    private static Pose2d SECOND_WOBBLE = new Pose2d(-34, -51, 0);
    private static Pose2d POINT_AVOID_RINGS_AGAIN = new Pose2d(-24, -48, Math.PI);
    private static Pose2d SHOOT_POS_START = new Pose2d(-12, -48, Math.toRadians(17.3));
    private static Vector2d SHOOT_POS_MIDDLE = new Vector2d(-12-5.7, -48+5.7);
    private static Vector2d SHOOT_POS_END = new Vector2d(-12-11, -48+11);
    private static Pose2d PARK_POS = new Pose2d(12.0, -36, 0);

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
    Trajectory trajSecondWobble;
    Trajectory trajSecondDeposit;
    Trajectory trajShootAndPark;
    WobblePosition wobblePosition;

    TrajectoryConstraints slowConstraints = new TrajectoryConstraints() {
        @NotNull
        @Override
        public SimpleMotionConstraints get(double v, @NotNull Pose2d pose2d, @NotNull Pose2d pose2d1, @NotNull Pose2d pose2d2) {
            return new SimpleMotionConstraints(5,5);
        }
    };

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

        bot = new SawronBot(hardwareMap);
        bot.drivetrain.setPoseEstimate(START_POSE);

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

        trajDeposit = bot.drivetrain.trajectoryBuilder(START_POSE)
                .splineToConstantHeading(POINT_AVOID_RINGS, 0)
                .splineToConstantHeading(wobblePosition.getPos(),Math.PI/6)
                .build();
        trajSecondWobble = bot.drivetrain.trajectoryBuilder(trajDeposit.end())
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-6, 0)), Math.toRadians(120))
                .splineToSplineHeading(SECOND_WOBBLE_LINEUP, Math.PI)
                .splineToSplineHeading(SECOND_WOBBLE, Math.PI)
                .build();
        trajSecondDeposit = bot.drivetrain.trajectoryBuilder(trajSecondWobble.end())
                .splineToSplineHeading(POINT_AVOID_RINGS_AGAIN, 0)
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-6, 0)), 0)
                .build();
        trajShootAndPark = bot.drivetrain.trajectoryBuilder(trajSecondDeposit.end())
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-12, 0)), Math.PI/2)
                .splineToSplineHeading(SHOOT_POS_START, Math.PI/4)
                .addDisplacementMarker(() -> bot.shooter.advance())
                .splineToConstantHeading(SHOOT_POS_MIDDLE,Math.PI/4, slowConstraints)
                .addDisplacementMarker(() -> bot.shooter.advance())
                .splineToConstantHeading(SHOOT_POS_END, Math.PI/4, slowConstraints)
                .addDisplacementMarker(() -> bot.shooter.advance())
                .splineToSplineHeading(PARK_POS, 0)
                .build();
        bot.drivetrain.followTrajectoryAsync(trajDeposit);

        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
        cameraStreaming = false;

        bot.wobbleGrabber.setMode(WobbleGrabber.Mode.AUTO);
        bot.wobbleGrabber.lift();
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
                if (!bot.wobbleGrabber.isBusy()) {
                    bot.drivetrain.followTrajectoryAsync(trajSecondWobble);
                    state = AutoState.DRIVE_BACK;
                }
                break;
            case DRIVE_BACK:
                if (!bot.drivetrain.isBusy()) {
                    bot.wobbleGrabber.lift();
                    state = AutoState.GET_SECOND_WOBBLE;
                }
                break;
            case GET_SECOND_WOBBLE:
                if (!bot.wobbleGrabber.isBusy()) {
                    bot.drivetrain.followTrajectoryAsync(trajSecondDeposit);
                    state = AutoState.DRIVE_TO_SECOND_DEPOSIT;
                }
                break;
            case DRIVE_TO_SECOND_DEPOSIT:
                if (!bot.drivetrain.isBusy()) {
                    bot.wobbleGrabber.drop();
                    bot.shooter.runShooter();
                    state = AutoState.DROP_SECOND_WOBBLE;
                }
                break;
            case DROP_SECOND_WOBBLE:
                if (!bot.wobbleGrabber.isBusy()) {
                    bot.drivetrain.followTrajectoryAsync(trajShootAndPark);
                    state = AutoState.PARK;
                    bot.shooter.advance();
                }
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
