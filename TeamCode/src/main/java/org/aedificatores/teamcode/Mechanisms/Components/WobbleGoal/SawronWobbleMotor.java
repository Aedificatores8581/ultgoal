package org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig;
import org.aedificatores.teamcode.Universal.Taemer;

@Config
public class SawronWobbleMotor {
    // All positions are in radians in this class

    enum State {
        ACTIVE,
        IDLE
    }

    public enum Mode {
        AUTO,
        TELEOP
    }

    private static final double TICKS_PER_REV = 8192;
    private static final double GEAR_RATIO = 1/200.0;
    private static final double MAX_RPM = 5000;

    public static PIDCoefficients pidCoeff = new PIDCoefficients(4, 0.02, 0);
    public static double kV = 0.45; // 1/rpmToAngularVelocity(MAX_RPM);
    public static double kA = 0.006;

    public static double maxVel = 5.0;
    public static double maxAccel = 5.0;
    public static double maxJerk = 0.0;

    private double power = 0.0;
    private double goalAngle = 0.0;

    private PIDFController controller = new PIDFController(pidCoeff, kV, kA,0);
    private Taemer clock;
    private State state;
    private Mode mode;

    private DcMotorEx actuator;
    private MotionProfile currentProfile;
    private MotionState currentState;

    public SawronWobbleMotor(HardwareMap map, Mode m) {
        actuator = map.get(DcMotorEx.class, SawronBotConfig.WobbleSub.MOT);
        actuator.setDirection(DcMotorSimple.Direction.REVERSE);
        clock = new Taemer();
        clock.resetTime();
        state = State.IDLE;
        currentState = new MotionState(0,0);
        setMode(m);
    }

    public SawronWobbleMotor(HardwareMap map) {
        this(map, Mode.TELEOP);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.TELEOP) {
            state = State.ACTIVE; // Always active in teleop mode
        }
    }

    public void update() {
        if (state == State.ACTIVE) {
            if (mode == Mode.AUTO) {
                currentState = currentProfile.get((double) clock.getTime() / 1000.0);
                controller.setTargetPosition(currentState.getX());
                controller.setTargetVelocity(currentState.getV());
                controller.setTargetAcceleration(currentState.getA());
                actuator.setPower(controller.update(getCurrentAngle(), getCurrentAngularVelocity()));

                //if (clock.getTime() / 1000.0 >= currentProfile.duration()) {
                if (epsilonEquals(getCurrentAngle(), currentProfile.end().getX()) || clock.getTime() / 1000.0 >= currentProfile.duration() + 1.0) {
                    actuator.setPower(0.0);
                    state = State.IDLE;
                }
            } else {
                actuator.setPower(power);
            }
        }
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void resetEncoders() {
        actuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        actuator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getCurrentAngularVelocity() {
        return actuator.getVelocity() / TICKS_PER_REV * 2 * Math.PI;
    }

    public MotionState getCurrentTargetState() {
        return currentState;
    }

    public double getCurrentAngle() {
        return actuator.getCurrentPosition() / TICKS_PER_REV * 2 * Math.PI;
    }

    public double getCurrentTargetAngle() {
        return currentState.getX();
    }

    public double getGoalAngle() {
        return goalAngle;
    }

    private boolean epsilonEquals(double a, double b) {
        final double THRESHOLD = .07;
        return Math.abs(a - b) <= THRESHOLD;
    }

    public boolean isBusy() {
        return state == State.ACTIVE && mode == Mode.AUTO;
    }

    public void gotoAngle(double angle) {
        goalAngle = angle;
        MotionState start = new MotionState(getCurrentAngle(), 0, 0,0 );
        MotionState goal = new MotionState(angle, 0, 0,0 );
        currentProfile =  MotionProfileGenerator.generateSimpleMotionProfile(start, goal, maxVel, maxAccel, maxJerk);
        state = State.ACTIVE;
        clock.resetTime();
    }

    public static final double rpmToAngularVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI / 60.0;
    }

}