package org.aedificatores.teamcode.Mechanisms.Components;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Encoder;
import org.aedificatores.teamcode.Mechanisms.Sensors.RevLEDIndicator;
import org.aedificatores.teamcode.Universal.Taemer;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class GandalfShooterFlinger {
    // Roadrunner just sorta assumes that every setpoint/target in its PID Controller
    // is a position. Of course, here we are trying to control velocity. This is why we, for
    // example pass the current velocity into the measured position term, since that parameter
    // is *actually* just the setpoint, and measured velocity is just the derivative of the
    // setpoint. Same applies to the motion profile

    enum SubsystemState {
        IDLE,
        RUNNING
    }
    public static final double V3_DISTANCE_THRESHOLD = 4.5;
    public static final double RESHOOT_TIMER_TRESH = .2;
    public static final double DISTANCE_DERIV_ARRIVED_THRESH = -40;

    public static final double MAX_VEL = 520*.8; // Radians/sec
    public static final double MAX_ACCEL = 520*.8; //// Radians/sec^2
    public static final double MAX_JERK = 300*.8; //// Radians/sec^3
    public static final double RADIANS_PER_ENC = (2 * Math.PI)/28;

    public static double kP = -0.00002;
    public static double kI = 0;
    public static double kD = 0;

    public static double kA = -0.00001156, kJ = 0;

    PIDCoefficients coeffs = new PIDCoefficients(kP, kI, kD);
    PIDFController controller = new PIDFController(coeffs, kA, kJ, 0);
    MotionProfile profile;

    DcMotorEx[] actuator;
    RevColorSensorV3 shooterSensor;
    Encoder encoder;
    Taemer accelClock, profileClock, reshootClock, distanceDerivClock;
    SubsystemState subsystemState = SubsystemState.IDLE;

    private double currentVel = 0;
    private double currentAccel = 0;
    private double pastVel = 0.0;
    private double currentPower = 0.0;


    public GandalfShooterFlinger(HardwareMap map) {
        actuator = new DcMotorEx[2];
        for (int i = 0; i < 2; ++i) {
            actuator[i] = map.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[i]);
            actuator[i].setDirection(DcMotorSimple.Direction.REVERSE);
            actuator[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            actuator[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        shooterSensor = map.get(RevColorSensorV3.class, GandalfBotConfig.SHOOT.RING_DETECT);

        encoder =  new Encoder(map.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[GandalfBotConfig.SHOOT.ODOM_INDEX]));
        encoder.setDirection(Encoder.Direction.REVERSE);
        accelClock = new Taemer();
        profileClock = new Taemer();
        reshootClock = new Taemer();
        distanceDerivClock = new Taemer();
    }

    public void update() {
        if (subsystemState == SubsystemState.RUNNING) {
            currentVel = encoder.getRawVelocity() * RADIANS_PER_ENC;
            currentAccel = (currentVel - pastVel) / accelClock.getTimeSec();
            accelClock.resetTime();
            pastVel = currentVel;

            MotionState targetState = profile.get(profileClock.getTimeSec());

            controller.setTargetPosition(targetState.getX()); // NOTE: This ACTUALLY SETS TARGET VELOCITY!!
            controller.setTargetVelocity(targetState.getV()); // NOTE: This ACTUALLY SETS TARGET ACCELERATION!!
            controller.setTargetAcceleration(targetState.getA()); // NOTE: This ACTUALLY SETS TARGET JERK!!
            currentPower += controller.update(currentVel, currentAccel);
        }

        if (containsRing()) {
            reshootClock.resetTime();
        }

        for (int i = 0; i < 2; ++i) actuator[i].setPower(currentPower);

    }

    public double getCurrentVelocity() {
        return currentVel;
    }

    public boolean containsRing() {
        return shooterSensor.getDistance(DistanceUnit.CM) < V3_DISTANCE_THRESHOLD;
    }
    public boolean upToSpeed() {
        double THRESHOLD = 10;
        return Math.abs(getCurrentVelocity() - getFinalTargetVelocity()) < THRESHOLD && subsystemState == SubsystemState.RUNNING;
    }

    public boolean readyToShoot() {
        return upToSpeed() && !containsRing() && reshootClock.getTimeSec() > RESHOOT_TIMER_TRESH;
    }

    public double getCurrentAcceleration() {
        return currentAccel;
    }

    public double getFinalTargetVelocity() {
        if (subsystemState == SubsystemState.RUNNING) {
            return profile.end().getX();
        } else {
            return 0.0;
        }
    }

    public double getTargetVelocity() {
        if (subsystemState == SubsystemState.RUNNING) {
            return profile.get(profileClock.getTimeSec()).getX();
        } else {
            return 0.0;
        }
    }

    public double getTargetAcceleration() {
        return profile.get(profileClock.getTimeSec()).getV();
    }

    public double getCurrentPower() {
        return currentPower;
    }

    public double getDistanceReading() {
        return shooterSensor.getDistance(DistanceUnit.CM);
    }

    public void setSpeed(double radPerSec) {
        if (radPerSec >= -.001 && radPerSec <= .001) {
            subsystemState = SubsystemState.IDLE;
            currentPower = 0.0;
        } else {
            MotionState start = new MotionState(currentVel, currentAccel, 0, 0);
            MotionState goal = new MotionState(radPerSec, 0, 0, 0);
            profile = MotionProfileGenerator.generateSimpleMotionProfile(start, goal, MAX_ACCEL, MAX_JERK);
            profileClock.resetTime();
            subsystemState = SubsystemState.RUNNING;
        }
    }


}
