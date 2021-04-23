package org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Encoder;
import org.aedificatores.teamcode.Universal.Taemer;

public class GandalfIntakeLift {
    public static final double TICKS_PER_REV = 8192.0;
    public static final double GEARING = 0.748031/3.75;

    CRServo servo;
    double startingAngle;
    Encoder enc;

    double power;

    public static double maxAccel = 2;
    public static double maxVel = 1;
    public static PIDCoefficients coeff = new PIDCoefficients(1.2, 0.0, 0.1);
    PIDFController controller = new PIDFController(coeff, 0.0, 0.0, 0.0);
    private Taemer clock;
    private MotionProfile currentProfile;
    private MotionState currentState;

    boolean moving;

    public enum Mode {
        AUTO,
        TELEOP
    }

    public enum State {
        IDLE,
        RUNNING
    }

    Mode mode;
    State state = State.IDLE;
    public GandalfIntakeLift(HardwareMap map, double angle, Mode m) {
        startingAngle = angle;
        servo = map.crservo.get(GandalfBotConfig.INTAKE.SERV);
        DcMotorEx tmp = map.get(DcMotorEx.class, GandalfBotConfig.INTAKE.ENC);
        tmp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tmp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        enc = new Encoder(tmp);
        enc.setDirection(Encoder.Direction.FORWARD);
        clock = new Taemer();
        clock.resetTime();
        mode = m;
    }

    public void update() {
        if (mode == Mode.AUTO && state == State.RUNNING) {
            currentState = currentProfile.get(clock.getTimeSec());
            controller.setTargetPosition(currentState.getX());
            controller.setTargetVelocity(currentState.getV());
            controller.setTargetAcceleration(currentState.getA());

            power = controller.update(getCurrentAngleRadians(), getCurrentAngularVelocityRadians());
            servo.setPower(power);

            //if (clock.getTime() / 1000.0 >= currentProfile.duration()) {
            if (epsilonEquals(getCurrentAngleRadians(), currentProfile.end().getX()) || clock.getTimeSec() >= currentProfile.duration() + .2) {
                moving = false;
            }
        } else {
            servo.setPower(power);
        }
    }

    public double getCurrentAngleRadians() {
        return enc.getCurrentPosition() * 2 * Math.PI * GEARING / TICKS_PER_REV + startingAngle;
    }

    public double getTargetAngleRadians() {
        return (state == State.RUNNING) ? currentProfile.get(clock.getTimeSec()).getX() : 0.0;
    }

    public double getTargetAngleDegrees() {
        return getTargetAngleRadians() * 180.0/Math.PI;
    }

    public double getTargetAngularVelocityRadians() {
        return (state == State.RUNNING) ? currentProfile.get(clock.getTimeSec()).getV() : 0.0;
    }

    public double getTargetAngularVelocityDegrees() {
        return getTargetAngularVelocityRadians() * 180.0/Math.PI;
    }

    public double getCurrentAngleDegrees() {
        return getCurrentAngleRadians() * 180/Math.PI;
    }

    public double getCurrentAngularVelocityRadians() {
        return enc.getRawVelocity() * 2 * Math.PI * GEARING / TICKS_PER_REV;
    }

    public double getCurrentAngularVelocityDegrees() {
        return getCurrentAngularVelocityRadians() * 180/Math.PI;
    }

    public void setPower(double x) {
        power = x;
    }

    public double getPower() {
        return power;
    }

    public boolean isMoving() {
        return moving;
    }

    // in radians
    public void gotoAngle(double angle) {
        MotionState start = new MotionState(getCurrentAngleRadians(), 0, 0,0 );
        MotionState goal = new MotionState(angle, 0, 0,0 );
        currentProfile =  MotionProfileGenerator.generateSimpleMotionProfile(start, goal, maxVel, maxAccel, 0.0);
        clock.resetTime();
        moving = true;
        state = State.RUNNING;
    }

    private boolean epsilonEquals(double a, double b) {
        final double THRESHOLD = .007;
        return Math.abs(a - b) <= THRESHOLD;
    }
}
