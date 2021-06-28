package org.aedificatores.teamcode.OpModes.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Components.SawronWobbleGoal.SawronWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBot;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.aedificatores.teamcode.Vision.RingDetector;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Config
@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfThreeHighTwoWobble extends OpMode {
    enum AutoState {
        DRIVE_TO_SHOOT,
        SHOOT,
        DRIVE_DEPOSIT_FIRST,
        DROP_FIRST_WOBBLE,
        DRIVE_SECOND_WOBBLE,
        GET_SECOND_WOBBLE,
        DRIVE_DEPOSIT_SECOND,
        DROP_SECOND_WOBBLE,
        PARK,
        END
    }

    AutoState state = AutoState.DRIVE_TO_SHOOT;
    private static final Pose2d START_POSE = new Pose2d(-(72.0 - 18.0/2.0), -25.5, 0);
    private static final Vector2d POINT_AVOID_RINGS = new Vector2d(-24, -16);
    private static Pose2d SHOOT_POS = new Pose2d(-10, -19, Math.toRadians(-7));
    private static Pose2d SIDE_NEAR_POS = new Pose2d(0.0,-60.0, Math.toRadians(185));
    private static Pose2d SIDE_FAR_POS = new Pose2d(48, -62.0, Math.toRadians(183));
    private static Pose2d MIDDLE_POS = new Pose2d(21.0, -40, Math.PI);
    private static Pose2d SECOND_WOBBLE = new Pose2d(-35, -45, Math.toRadians(2));
    private static Pose2d POINT_AVOID_RINGS_AGAIN = new Pose2d(-24, -48, Math.PI);
    private static Vector2d PARK_POS = new Vector2d(10.0, -30);

    public static double SHOOTER_SPEED = 239.6;

    enum WobblePosition {
        SIDE_NEAR(SIDE_NEAR_POS),
        SIDE_FAR(SIDE_FAR_POS),
        MIDDLE(MIDDLE_POS);

        Pose2d pos;
        WobblePosition(Pose2d pos) {
            this.pos = pos;
        }
        public Pose2d getPos() {
            return pos;
        }
    }

    GandalfBot bot;
    Trajectory trajShoot;
    Trajectory trajDeposit;
    Trajectory trajSecondWobble;
    Trajectory trajSecondDeposit;
    Trajectory trajPark;
    WobblePosition wobblePosition;

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    OpenCvCamera cam;
    RingDetector pipe;
    boolean cameraStreaming = false;

    Gamepad prev1 = new Gamepad();

    FtcDashboard dashboard;

    @Override
    public void init() {
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"));

        pipe = new RingDetector(WIDTH, HEIGHT);
        cam.setPipeline(pipe);

        cam.openCameraDeviceAsync(() -> {
            cam.startStreaming(WIDTH, HEIGHT, OpenCvCameraRotation.UPRIGHT);
            cameraStreaming = true;
        });

        bot = new GandalfBot(hardwareMap, true);
        bot.drivetrain.setPoseEstimate(START_POSE);
        bot.wobbleGrabber.setMode(GandalfWobbleGrabber.Mode.TELEOP);
    }

    private boolean alreadyLifted = false;
    @Override
    public void init_loop() {
        telemetry.addData("Ring Stack", pipe.getRingStackType());
        bot.update();

        if (gamepad1.a && !alreadyLifted) {
            bot.intake.lift.gotoAngle(120*Math.PI/180.0);
            alreadyLifted = true;
        }

        if (gamepad1.b && !prev1.b) {
            bot.wobbleGrabber.toggleGrabber();
        }

        bot.wobbleGrabber.setPower(gamepad1.left_stick_y * .65);
        try {
            prev1.copy(gamepad1);
        } catch (RobotCoreException e) {
            telemetry.addData("Exception", "Can't copy gamepad");
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

        trajShoot = bot.drivetrain.trajectoryBuilder(START_POSE)
                .splineToConstantHeading(POINT_AVOID_RINGS, 0)
                .splineToSplineHeading(SHOOT_POS, 0)
                .build();

        trajDeposit = bot.drivetrain.trajectoryBuilder(trajShoot.end())
                .splineToSplineHeading(wobblePosition.getPos(),0)
                .addDisplacementMarker(() -> bot.wobbleGrabber.drop())
                .build();
        if (wobblePosition == WobblePosition.SIDE_FAR) {
            trajSecondWobble = bot.drivetrain.trajectoryBuilder(trajDeposit.end())
                    .splineToSplineHeading(wobblePosition.getPos().plus(new Pose2d(-6, 0, 0)), Math.PI)
                    .splineToSplineHeading(SECOND_WOBBLE.plus(new Pose2d(40, 0, 0)), Math.PI)
                    .splineToSplineHeading(SECOND_WOBBLE, Math.PI)
                    .addDisplacementMarker(() -> bot.wobbleGrabber.lift())
                    .build();
        } else {
            trajSecondWobble = bot.drivetrain.trajectoryBuilder(trajDeposit.end())
                    .splineToSplineHeading(wobblePosition.getPos().plus(new Pose2d(-6, 0, 0)), Math.PI)
                    .splineToSplineHeading(SECOND_WOBBLE.plus(new Pose2d(16, 0, 0)), Math.PI)
                    .splineToSplineHeading(SECOND_WOBBLE, Math.PI)
                    .addDisplacementMarker(() -> bot.wobbleGrabber.lift())
                    .build();
        }
        trajSecondDeposit = bot.drivetrain.trajectoryBuilder(trajSecondWobble.end())
                .splineToSplineHeading(POINT_AVOID_RINGS_AGAIN, 0)
                .splineToSplineHeading(wobblePosition.getPos().plus(new Pose2d(-6, 0,0)), 0)
                .addDisplacementMarker(() -> bot.wobbleGrabber.drop())
                .build();
        if (wobblePosition == WobblePosition.SIDE_NEAR) {
            trajPark = bot.drivetrain.trajectoryBuilder(trajSecondDeposit.end())
                    .splineToConstantHeading(UniversalFunctions.pos2vec(wobblePosition.getPos().plus(new Pose2d(-19, 0, 0))), Math.toRadians(60))
                    .splineToConstantHeading(UniversalFunctions.pos2vec(wobblePosition.getPos().plus(new Pose2d(-19, 19, 0))), Math.toRadians(60))
                    .splineToSplineHeading(UniversalFunctions.vec2pos(PARK_POS, Math.toRadians(0)), 0)
                    .build();
        } else {
            trajPark = bot.drivetrain.trajectoryBuilder(trajSecondDeposit.end())
                    .splineToConstantHeading(PARK_POS, Math.PI)
                    .build();
        }

        bot.drivetrain.followTrajectoryAsync(trajShoot);

        cam.stopStreaming();
        pipe.close();
        cam.closeCameraDevice();
        cameraStreaming = false;

        bot.shooter.setSpeed(SHOOTER_SPEED);
        bot.update();
        bot.wobbleGrabber.setMode(GandalfWobbleGrabber.Mode.AUTO);
        bot.wobbleGrabber.lift();
        bot.intake.lift.gotoAngle(0*Math.PI/180);
    }

    @Override
    public void loop() {
        switch (state) {
            case DRIVE_TO_SHOOT:
                if (!bot.drivetrain.isBusy()) {
                    resetStartTime();
                    bot.shootRings(SHOOTER_SPEED, 3);
                    state = AutoState.SHOOT;
                }
                break;
            case SHOOT:
                if (bot.shooterIdle()) {
                    bot.drivetrain.followTrajectoryAsync(trajDeposit);
                    state = AutoState.DRIVE_DEPOSIT_FIRST;
                }
                break;
            case DRIVE_DEPOSIT_FIRST:
                if (!bot.drivetrain.isBusy()) {
                    bot.wobbleGrabber.drop();
                    state = AutoState.DROP_FIRST_WOBBLE;
                }
                break;
            case DROP_FIRST_WOBBLE:
                if (bot.wobbleGrabber.isIdle()) {
                    bot.drivetrain.followTrajectoryAsync(trajSecondWobble);
                    state = AutoState.DRIVE_SECOND_WOBBLE;
                }
                break;
            case DRIVE_SECOND_WOBBLE:
                if (!bot.drivetrain.isBusy()) {
                    bot.wobbleGrabber.lift();
                    state = AutoState.GET_SECOND_WOBBLE;
                }
                break;
            case GET_SECOND_WOBBLE:
                if (bot.wobbleGrabber.isIdle()) {
                    bot.drivetrain.followTrajectoryAsync(trajSecondDeposit);
                    state = AutoState.DRIVE_DEPOSIT_SECOND;
                }
                break;
            case DRIVE_DEPOSIT_SECOND:
                if (!bot.drivetrain.isBusy()) {
                    bot.wobbleGrabber.drop();
                    state = AutoState.DROP_SECOND_WOBBLE;
                }
                break;
            case DROP_SECOND_WOBBLE:
                if (bot.wobbleGrabber.isIdle()) {
                    bot.drivetrain.followTrajectoryAsync(trajPark);
                    state = AutoState.PARK;
                }
                break;
            case PARK:
                if (!bot.drivetrain.isBusy()) {
                    state = AutoState.END;
                    GandalfBot.currentPositionEstimate = bot.drivetrain.getPoseEstimate();
                }
                break;
            case END:
                break;
        }

        bot.update();
    }
}
