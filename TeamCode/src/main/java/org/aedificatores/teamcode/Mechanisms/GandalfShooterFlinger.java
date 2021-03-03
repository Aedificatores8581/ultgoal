package org.aedificatores.teamcode.Mechanisms;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Encoder;
import org.aedificatores.teamcode.Universal.Taemer;

@Config
public class GandalfShooterFlinger {
    public static final double MAX_VEL = 600*.8; // Radians/sec
    public static final double MAX_ACCEL = 600*.8; //// Radians/sec^2
    public static final double MAX_JERK = 600*.8; //// Radians/sec^2
    public static final double RADIANS_PER_ENC = (2 * Math.PI)/28;

    public static double kP = 0;
    public static double kI = 0;
    public static double kD = 0;

    PIDCoefficients coeffs = new PIDCoefficients(kP, kI, kD);
    PIDFController controller = new PIDFController(coeffs, 0, 0, 0);
    MotionProfile profile;

    DcMotorEx actuator[];
    Encoder encoder;
    Taemer accelClock, profileClock;

    double currentVel = 0;
    double currentAccel = 0;
    double pastVel = 0.0;
    double currentPower = 0.0;

    public GandalfShooterFlinger(HardwareMap map) {
        actuator = new DcMotorEx[2];
        for (int i = 0; i < 2; ++i) {
            actuator[i] = map.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[i]);
            actuator[i].setDirection(DcMotorSimple.Direction.REVERSE);
            actuator[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            actuator[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        encoder =  new Encoder(map.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[GandalfBotConfig.SHOOT.ODOM_INDEX]));
        encoder.setDirection(Encoder.Direction.REVERSE);

        accelClock = new Taemer();
        profileClock = new Taemer();
    }

    public void update() {

        currentVel = encoder.getRawVelocity() * RADIANS_PER_ENC;
        currentAccel = (currentVel - pastVel) / accelClock.getTimeSec();
        accelClock.resetTime();
        pastVel = currentVel;


        // Roadrunner just sorta assumes that every setpoint/target in its PID Controller
        // is a position. Of course, here we are trying to control velocity. This is why we, for
        // example pass the current velocity into the measured position term, since that parameter
        // is *actually* just the setpoint, and measured velocity is just the derivative of the
        // setpoint. Same applies to the motion profile
        MotionState targetState = profile.get(profileClock.getTimeSec());

        controller.setTargetPosition(targetState.getX()); // NOTE: This ACTUALLY SETS TARGET VELOCITY!!
        controller.setTargetVelocity(targetState.getV()); // NOTE: This ACTUALLY SETS TARGET ACCELERATION!!
        controller.setTargetAcceleration(targetState.getA()); // NOTE: This ACTUALLY SETS TARGET JERK!!
        currentPower += controller.update(currentVel, currentAccel);
        for(int i = 0; i < 2; ++i) actuator[i].setPower(currentPower);
    }

    public double getCurrentVelocity() {
        return currentVel;
    }

    public double getCurrentAcceleration() {
        return currentAccel;
    }

    public double getTargetVelocity() {
        return profile.get(profileClock.getTimeSec()).getX();
    }

    public double getTargetAcceleration() {
        return profile.get(profileClock.getTimeSec()).getV();
    }

    public void setSpeed(double radPerSec) {
        MotionState start = new MotionState(currentVel, currentAccel, 0,0 );
        MotionState goal = new MotionState(radPerSec, 0, 0,0 );
        profile =  MotionProfileGenerator.generateSimpleMotionProfile(start, goal, MAX_VEL, MAX_ACCEL, MAX_JERK);
        profileClock.resetTime();
    }


}
