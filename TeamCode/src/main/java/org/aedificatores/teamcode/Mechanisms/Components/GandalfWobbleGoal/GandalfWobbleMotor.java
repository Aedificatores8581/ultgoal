package org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Potentiometer;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.Taemer;

@Config
public class GandalfWobbleMotor {
    public enum Mode {
        AUTO,
        TELEOP
    }

    public enum MovementState {
        ACTIVE_MOVE,
        IDLE
    }

    public static final double GRABBER_MASS = .650; // in kilograms
    public static final double WOBBLE_MASS = .410; // in kilograms

    public static double maxAccel = 4;
    public static double maxVel = 3;
    public static PIDCoefficients coeff = new PIDCoefficients(1.2, 0.0, 0.1);
    PIDFController controller = new PIDFController(coeff, 0.0, 0.0, 0.0);

    public enum WobbleState {
        NOT_HOLDING_WOBBLE,
        HOLDING_WOBBLE
    }
    // .2667 meters is the distance between the axle and the center of gravity
    // 1 radians is angle between the center of gravity and what the potentiometer reads as 0 rad
    public static final Vector2 CENTER_GRAVITY_NO_WOBBLE = new Vector2(.2667 * Math.cos(-0.4768525039), .2667 * Math.sin(-0.4768525039));

    private Potentiometer potentiometer;
    private DcMotorEx actuator;



    private double power = 0.0;
    private double goalAngle = 0.0;

    private Mode mode = Mode.TELEOP;
    private MovementState movementState = MovementState.IDLE;
    private WobbleState wobbleState = WobbleState.NOT_HOLDING_WOBBLE;
    private Taemer clock;
    private MotionProfile currentProfile;
    private MotionState currentState;
    boolean moving = false;
    double prevVel;


    public GandalfWobbleMotor(HardwareMap map) {
        this(map, Mode.TELEOP);
    }

    public GandalfWobbleMotor(HardwareMap map, Mode m) {
        potentiometer = new Potentiometer(map, GandalfBotConfig.WOBBLE.POTENT);
        actuator = map.get(DcMotorEx.class, GandalfBotConfig.WOBBLE.MOT);
        actuator.setDirection(DcMotorSimple.Direction.REVERSE);
        clock = new Taemer();
        clock.resetTime();
        setMode(m);
    }

    public double getCurrentAngleRadians() {
        return potentiometer.getAngleNonLinearRadians();
    }

    public double getTargetAngleRadians() {
        return currentProfile.get(clock.getTimeSec()).getX();
    }

    public double getTargetAngleDegrees() {
        return getTargetAngleRadians() * 180.0/Math.PI;
    }

    public double getTargetAngularVelocityRadians() {
        return currentProfile.get(clock.getTimeSec()).getV();
    }

    public double getTargetAngularVelocityDegrees() {
        return getTargetAngularVelocityRadians() * 180.0/Math.PI;
    }

    public double getCurrentAngleDegrees() {
        return potentiometer.getAngleNonLinearDegrees();
    }

    public double getCurrentAngularVelocityRadians() {
        return potentiometer.getVelocityRadians();
    }

    public double getCurrentAngularVelocityDegrees() {
        return potentiometer.getVelocityDegrees();
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setWobbleState(WobbleState state) {
        wobbleState = state;
    }

    public void update() {
        if (mode == Mode.AUTO) {
            switch (movementState) {
                case ACTIVE_MOVE:
                    currentState = currentProfile.get(clock.getTimeSec());
                    // both controllers are updated so there are not large jumps in error
                    controller.setTargetPosition(currentState.getX());
                    controller.setTargetVelocity(currentState.getV());
                    controller.setTargetAcceleration(currentState.getA());

                    power = controller.update(getCurrentAngleRadians(), getCurrentAngularVelocityRadians());
                    actuator.setPower(power);

                    //if (clock.getTime() / 1000.0 >= currentProfile.duration()) {
                    if (epsilonEquals(getCurrentAngleRadians(), currentProfile.end().getX()) || clock.getTimeSec() >= currentProfile.duration() + 1.0) {
                        moving = false;
                    }
                case IDLE:
                    break;
            }
        } else {
            actuator.setPower(power);
        }

        potentiometer.update();
    }

    public void setPower(double x) {
        power = x;
    }

    public double getPower() {
        return power;
    }

    // in radians
    public void gotoAngle(double angle) {
        MotionState start = new MotionState(getCurrentAngleRadians(), 0, 0,0 );
        MotionState goal = new MotionState(angle, 0, 0,0 );
        currentProfile =  MotionProfileGenerator.generateSimpleMotionProfile(start, goal, maxVel, maxAccel, 0.0);
        movementState = MovementState.ACTIVE_MOVE;
        clock.resetTime();
        moving = true;
    }

    public boolean isMoving() {
        return moving;
    }

    // angle is in radians, velocity in rad/s
    // unused. used to be for when we had a much less intensive gearing, se we would
    // have to counteract the forces of gravity to make the wobble goal stay still.
    public double getFeedforeward() {
        return 0.0;
    }

    private boolean epsilonEquals(double a, double b) {
        final double THRESHOLD = .007;
        return Math.abs(a - b) <= THRESHOLD;
    }
}
