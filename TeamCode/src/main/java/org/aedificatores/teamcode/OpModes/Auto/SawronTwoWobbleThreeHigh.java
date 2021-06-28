package org.aedificatores.teamcode.OpModes.Auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Components.SawronWobbleGoal.SawronWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.aedificatores.teamcode.Vision.RingDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(name = "TwoWobbleThreeHighAuto", group = OpModeGroups.SAWRON)
public class SawronTwoWobbleThreeHigh extends OpMode {
    enum AutoState {
        DRIVE_TO_DEPOSIT,
        DROP_WOBBLE,
        DRIVE_BACK,
        GET_SECOND_WOBBLE,
        DRIVE_TO_SECOND_DEPOSIT,
        DROP_SECOND_WOBBLE,
        DRIVE_TO_SHOOT,
        SHOOT,
        PARK,
        END
    }

    AutoState state = AutoState.DRIVE_TO_DEPOSIT;
    private static final Pose2d START_POSE = new Pose2d(-(72.0 - 17.0/2.0), -24, Math.PI);
    private static final Vector2d POINT_AVOID_RINGS = new Vector2d(-24, -24);
    private static Vector2d SIDE_NEAR_POS = new Vector2d(0.0,-60.0);
    private static Vector2d SIDE_FAR_POS = new Vector2d(48, -60.0);
    private static Vector2d MIDDLE_POS = new Vector2d(21.0, -36);
    private static  Pose2d SECOND_WOBBLE_LINEUP = new Pose2d(-12, -51, 0);
    private static Pose2d SECOND_WOBBLE_SIDE_NEAR = new Pose2d(-34, -51, Math.toRadians(10));
    private static Pose2d SECOND_WOBBLE_SIDE_FAR = new Pose2d(-34, -48, Math.toRadians(10));
    private static Pose2d SECOND_WOBBLE_MIDDLE = new Pose2d(-34, -51, 0);
    private static Pose2d POINT_AVOID_RINGS_AGAIN = new Pose2d(-24, -48, Math.PI);
    private static Pose2d SHOOT_POS = new Pose2d(-2, -37, Math.toRadians(3));
    private static Vector2d PARK_POS = new Vector2d(12.0, -40);

    enum WobblePosition {
        SIDE_NEAR(SIDE_NEAR_POS, SECOND_WOBBLE_SIDE_NEAR),
        SIDE_FAR(SIDE_FAR_POS, SECOND_WOBBLE_SIDE_FAR),
        MIDDLE(MIDDLE_POS, SECOND_WOBBLE_MIDDLE);

        Vector2d pos;
        Pose2d secondWobble;
        WobblePosition(Vector2d pos, Pose2d secondWobble) {
            this.pos = pos;
            this.secondWobble = secondWobble;
        }
        public Vector2d getPos() {
            return pos;
        }
        public Pose2d getSecondWobble() {
            return secondWobble;
        }
    }

    SawronBot bot;
    Trajectory trajDeposit;
    Trajectory trajSecondWobble;
    Trajectory trajSecondDeposit;
    Trajectory trajShoot;
    Trajectory trajPark;
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

        bot = new SawronBot(hardwareMap, true);
        bot.drivetrain.setPoseEstimate(START_POSE);
        bot.wobbleGrabber.setMode(SawronWobbleGrabber.Mode.TELEOP);
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

        bot.drivetrain.setPoseEstimate(START_POSE);

        trajDeposit = bot.drivetrain.trajectoryBuilder(START_POSE)
                .splineToConstantHeading(POINT_AVOID_RINGS, 0)
                .splineToConstantHeading(wobblePosition.getPos(),Math.PI/6)
                .addDisplacementMarker(() -> bot.wobbleGrabber.drop())
                .build();
        trajSecondWobble = bot.drivetrain.trajectoryBuilder(trajDeposit.end())
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-6, 0)), Math.toRadians(120))
                .splineToSplineHeading(wobblePosition.getSecondWobble().plus(new Pose2d(22, 0, 0)), Math.PI)
                .splineToSplineHeading(wobblePosition.getSecondWobble(), Math.PI)
                .addDisplacementMarker(() -> bot.wobbleGrabber.lift())
                .build();
        if (wobblePosition == WobblePosition.MIDDLE) {
            trajSecondDeposit = bot.drivetrain.trajectoryBuilder(trajSecondWobble.end())
                    .splineToSplineHeading(POINT_AVOID_RINGS_AGAIN, 0)
                    .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-8, 0)), 0)
                    .addDisplacementMarker(() -> bot.wobbleGrabber.drop())
                    .build();
        } else {
            trajSecondDeposit = bot.drivetrain.trajectoryBuilder(trajSecondWobble.end())
                    .splineToSplineHeading(POINT_AVOID_RINGS_AGAIN, 0)
                    .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-6, 0)), 0)
                    .addDisplacementMarker(() -> bot.wobbleGrabber.drop())
                    .build();
        }
        if (wobblePosition == WobblePosition.SIDE_NEAR) {
            trajShoot = bot.drivetrain.trajectoryBuilder(trajSecondDeposit.end())
                    .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-20, 0)), Math.PI / 2)
                    .splineToSplineHeading(SHOOT_POS.plus(new Pose2d(0,0,Math.toRadians(5))), Math.PI / 2)
                    .addDisplacementMarker(() -> bot.shooter.advance())
                    .build();
        } else if (wobblePosition == WobblePosition.SIDE_FAR) {
            trajShoot = bot.drivetrain.trajectoryBuilder(trajSecondDeposit.end())
                    .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-20, 0)), Math.PI / 2)
                    .splineToSplineHeading(SHOOT_POS.plus(new Pose2d(0,0,Math.toRadians(2))), Math.PI / 2)
                    .addDisplacementMarker(() -> bot.shooter.advance())
                    .build();
        } else {
            trajShoot = bot.drivetrain.trajectoryBuilder(trajSecondDeposit.end())
                    .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-20, 0)), Math.PI / 2)
                    .splineToSplineHeading(SHOOT_POS.plus(new Pose2d(0,0,-Math.toRadians(0))), Math.PI / 2)
                    .addDisplacementMarker(() -> bot.shooter.advance())
                    .build();
        }
        trajPark = bot.drivetrain.trajectoryBuilder(trajShoot.end())
                .splineToConstantHeading(PARK_POS,0)
                .build();

        bot.drivetrain.followTrajectoryAsync(trajDeposit);

        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
        cameraStreaming = false;

        bot.wobbleGrabber.setMode(SawronWobbleGrabber.Mode.AUTO);
        bot.shooter.setSpeedMax();
        bot.wobbleGrabber.lift();
    }


    @Override
    public void loop() {
        switch (state) {
            case DRIVE_TO_DEPOSIT:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.DROP_WOBBLE;
                }
                break;
            case DROP_WOBBLE:
                if (bot.wobbleGrabber.isReleasing()) {
                    bot.drivetrain.followTrajectoryAsync(trajSecondWobble);
                    state = AutoState.DRIVE_BACK;
                }
                break;
            case DRIVE_BACK:
                if (!bot.drivetrain.isBusy() && !bot.wobbleGrabber.isBusy()) {
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
                if (bot.wobbleGrabber.isReleasing()) {
                    bot.drivetrain.followTrajectoryAsync(trajShoot);
                    state = AutoState.DRIVE_TO_SHOOT;
                    bot.shooter.forceAdvance();
                }
                break;
            case DRIVE_TO_SHOOT:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.SHOOT;
                }
                break;
            case SHOOT:
                if (bot.shooter.idle() && bot.shooter.shotAllThree()) {
                    bot.drivetrain.followTrajectory(trajPark);
                    state = AutoState.PARK;
                } else {
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
