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

    public static WobbleController noWobbleController = new WobbleController(GRABBER_MASS,
            new Vector2(.2667 * Math.cos(-0.4768525039),.2667 * Math.sin(-0.4768525039)),
            new PIDCoefficients(0.6, 0, 0.05),
            5.5,
            4.0,
            0.0,
            0.17,
            0.125,
            0.0);
    public static WobbleController withWobbleController = new WobbleController(GRABBER_MASS + WOBBLE_MASS,
            new Vector2(0.3048 * Math.cos(-0.2967059728),0.3048 * Math.sin(-0.2967059728)),
            new PIDCoefficients(0.6, 0, 0.05),
            5.5,
            4.0,
            0.0,
            .18,
            0.125,
            0);

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
                    noWobbleController.controller.setTargetPosition(currentState.getX());
                    noWobbleController.controller.setTargetVelocity(currentState.getV());
                    noWobbleController.controller.setTargetAcceleration(currentState.getA());
                    withWobbleController.controller.setTargetPosition(currentState.getX());
                    withWobbleController.controller.setTargetVelocity(currentState.getV());
                    withWobbleController.controller.setTargetAcceleration(currentState.getA());
                    if (wobbleState == WobbleState.NOT_HOLDING_WOBBLE) {
                        actuator.setPower(noWobbleController.controller.update(getCurrentAngleRadians(), getCurrentAngularVelocityRadians()));
                    } else {
                        actuator.setPower(withWobbleController.controller.update(getCurrentAngleRadians(), getCurrentAngularVelocityRadians()));
                    }


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

    // in radians
    public void gotoAngle(double angle) {
        MotionState start = new MotionState(getCurrentAngleRadians(), 0, 0,0 );
        MotionState goal = new MotionState(angle, 0, 0,0 );
        currentProfile =  MotionProfileGenerator.generateSimpleMotionProfile(start, goal, noWobbleController.maxVel, noWobbleController.maxAccel, noWobbleController.maxJerk);
        movementState = MovementState.ACTIVE_MOVE;
        clock.resetTime();
        moving = true;
    }

    public boolean isMoving() {
        return moving;
    }

    // angle is in radians, velocity in rad/s
    public double getFeedforeward() {
        return noWobbleController.feedforward(getCurrentAngleRadians(), getCurrentAngularVelocityRadians());
    }

    private boolean epsilonEquals(double a, double b) {
        final double THRESHOLD = .007;
        return Math.abs(a - b) <= THRESHOLD;
    }
}

class WobbleController {
    public double mass;
    public Vector2 centerOfGravity;

    public PIDCoefficients pidCoeff;
    public double maxVel;
    public double maxAccel;
    public double maxJerk;

    // F stands for fudge. :)
    public double kF;

    public double kV;
    public double kA;

    public PIDFController controller;

    public WobbleController(double mass, Vector2 centerOfGravity, PIDCoefficients pidCoeff, double maxVel, double maxAccel, double maxJerk, double kF, double kV, double kA) {
        this.mass = mass;
        this.centerOfGravity = centerOfGravity;
        this.pidCoeff = pidCoeff;
        this.maxVel = maxVel;
        this.maxAccel = maxAccel;
        this.maxJerk = maxJerk;
        this.kF = kF;
        this.kV = kV;
        this.kA = kA;

        controller = new PIDFController(pidCoeff, kV, kA, 0.0, this::feedforward);
    }

    public double feedforward(double angle, double velocity) {

        // get the angle of the center of mass;
        double centerOfMassAngle = angle + centerOfGravity.angle();
        // get torque
        double torqueDueToGravity = mass * 9.81 * centerOfGravity.magnitude() * Math.cos(centerOfMassAngle);

        // The fudge factor (kF) is sort of a hack to compensate for REV's opaque controller design
        // (which may not be entirely rev's fault, let's be clear)
        // the signal sent to the controller is between 1.0 and -1.0 (actually it's between
        // -32768 and 32767 but i digress). 1.0 is the max power that can be supplied at some point
        // in time, factoring in current battery power and other mechanisms running on the robot. -1.0
        // is the same but in the other direction.
        // so kF is a best guess at trying to convert torque to whatever that power is.
        return kF * torqueDueToGravity;
    }
}