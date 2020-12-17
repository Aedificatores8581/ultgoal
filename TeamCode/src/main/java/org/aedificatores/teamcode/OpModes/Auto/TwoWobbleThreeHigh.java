package org.aedificatores.teamcode.OpModes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Vision.RingDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

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
    private static Vector2d SECOND_WOBBLE = new Vector2d(-33, -51);
    private static Pose2d POINT_AVOID_RINGS_AGAIN = new Pose2d(-24, -51, Math.PI);
    private static Pose2d SHOOT_POS_ONE = new Pose2d(-8, -10.0, 0);
    private static Vector2d SHOOT_POS_TWO = new Vector2d(-8, -4.0);
    private static Vector2d SHOOT_POS_THREE = new Vector2d(-8, 5);
    private static Vector2d PARK_POS = new Vector2d(0.0, 0.0);

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
    Trajectory trajShootOne;
    Trajectory trajShootTwo;
    Trajectory trajShootThree;
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

        bot = new SawronBot(hardwareMap);
        bot.drivetrain.setPoseEstimate(START_POSE);
    }

    // Used to Account for the fact that we don't know the state of the wobble
    private boolean alreadyLifted = false;

    @Override
    public void init_loop() {
        telemetry.addData("Ring Stack", pipe.getRingStackType());
        bot.update();

        if (gamepad1.a && !alreadyLifted) {
            bot.wobbleGrabber.forceLift();
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
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-6, 0)), 0)
                .splineToSplineHeading(SECOND_WOBBLE_LINEUP, Math.PI)
                .splineToConstantHeading(SECOND_WOBBLE, Math.PI)
                .build();
        trajSecondDeposit = bot.drivetrain.trajectoryBuilder(trajSecondWobble.end())
                .splineToSplineHeading(POINT_AVOID_RINGS_AGAIN, 0)
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-6, 0)), 0)
                .build();
        trajShootOne = bot.drivetrain.trajectoryBuilder(trajSecondDeposit.end())
                .splineToConstantHeading(wobblePosition.getPos().plus(new Vector2d(-12, 0)), Math.PI/2)
                .splineToSplineHeading(SHOOT_POS_ONE, 0)
                .build();
        trajShootTwo = bot.drivetrain.trajectoryBuilder(trajShootOne.end())
                .splineToConstantHeading(SHOOT_POS_TWO, Math.PI/2)
                .build();
        trajShootThree = bot.drivetrain.trajectoryBuilder(trajShootTwo.end())
                .splineToConstantHeading(SHOOT_POS_THREE, Math.PI/2)
                .build();
        trajPark = bot.drivetrain.trajectoryBuilder(trajShootThree.end())
                .splineToConstantHeading(PARK_POS, 0)
                .build();
        bot.drivetrain.followTrajectoryAsync(trajDeposit);

        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
        cameraStreaming = false;

        bot.shooter.runShooter();
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
                if (bot.wobbleGrabber.isDown()) {
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
                if (bot.wobbleGrabber.isLifting()) {
                    bot.drivetrain.followTrajectoryAsync(trajSecondDeposit);
                    state = AutoState.DRIVE_TO_SECOND_DEPOSIT;
                }
                break;
            case DRIVE_TO_SECOND_DEPOSIT:
                if (!bot.drivetrain.isBusy() && bot.wobbleGrabber.isUp()) {
                    bot.wobbleGrabber.drop();
                    state = AutoState.DROP_SECOND_WOBBLE;
                }
                break;
            case DROP_SECOND_WOBBLE:
                if (bot.wobbleGrabber.isDown()) {
                    bot.drivetrain.followTrajectoryAsync(trajShootOne);
                    state = AutoState.DRIVE_TO_SHOOT_ONE;
                    bot.shooter.advance();
                }
                break;
            case DRIVE_TO_SHOOT_ONE:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.SHOOT_ONE;
                }
                break;
            case SHOOT_ONE:
                if (bot.shooter.getAdvancedCounter() == 2 && bot.shooter.idle()) {
                    bot.drivetrain.followTrajectoryAsync(trajShootTwo);
                    state = AutoState.DRIVE_TO_SHOOT_TWO;
                } else {
                    bot.shooter.advance();
                }
                break;
            case DRIVE_TO_SHOOT_TWO:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.SHOOT_TWO;
                }
                break;
            case SHOOT_TWO:
                if (bot.shooter.getAdvancedCounter() == 3 && bot.shooter.idle()) {
                    bot.drivetrain.followTrajectoryAsync(trajShootThree);
                    state = AutoState.DRIVE_TO_SHOOT_THREE;
                } else {
                    bot.shooter.advance();
                }
                break;
            case DRIVE_TO_SHOOT_THREE:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.SHOOT_THREE;
                }
                break;
            case SHOOT_THREE:
                if (bot.shooter.getAdvancedCounter() == 4 && bot.shooter.idle()) {
                    bot.drivetrain.followTrajectoryAsync(trajPark);
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
