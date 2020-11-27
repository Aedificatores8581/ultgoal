package org.aedificatores.teamcode.Mechanisms.Components;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.aedificatores.teamcode.Mechanisms.Sensors.Encoder;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Rotation;

import static org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig.ShootSub;

public class ShooterSubsystem {
    private Shooter shooter;

    public ShooterSubsystem(HardwareMap map) {
        shooter = new Shooter(map);
    }

    public void runShooter() {
        shooter.runShooter();
    }

    public void stopShooter() {
        shooter.stopShooter();
    }

    public double getTargetShooterVelocity() {
        return shooter.getTargetVelFromTime();
    }

    public double getActualShooterVelocity() {
        return shooter.getActualVelocity();
    }

    public void update() {
        shooter.update();
    }
}

class Lift {
    // TODO Fill Constants
    enum Position {
        DOWN(0.8),
        POS_SHOOT_TOP_RING(0.2),
        POS_SHOOT_MIDDLE_RING(0.008),
        POS_SHOOT_BOTTOM_RING(0.0);

        private double pos;
        Position(double pos) {
            this.pos = pos;
        }

        public double getPos() {
            return pos;
        }
    }

    Servo servo;
    Position position;

    Lift(HardwareMap map) {
        servo = map.servo.get(ShootSub.LIFT_SERV);
        position = Position.DOWN;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    void update() {
        servo.setPosition(position.getPos());
    }
}

@Config
class Shooter {
    final double targetShootingSpeed = 0.0; // TODO FIND
    final double WHEEL_RADIUS = 1.875;
    public static double MAX_RPM = 4000;
    public static double SPEED_UP_TIME = 6000; // milliseconds until max velocity

    DcMotorEx actuator;
    Encoder encoder;
    public static PIDFCoefficients velocityPIDCoeff = new PIDFCoefficients(0.0, 0.0, 0.0,0.0);
    boolean runningMotor = false;
    long baseTime;

    Shooter(HardwareMap map) {
        actuator = map.get(DcMotorEx.class, ShootSub.SHOOT_MOT);
        actuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        actuator.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, velocityPIDCoeff);
        encoder = new Encoder(actuator);
        resetTime();
    }

    void runShooter() {
        runningMotor = true;
        resetTime();
    }

    void stopShooter() {
        runningMotor = false;
    }

    void update () {
        if (runningMotor) {
            actuator.setVelocity(getTargetVelFromTime(), AngleUnit.RADIANS);
            actuator.setPower(1.0);
        } else {
            actuator.setPower(0.0);
        }
    }

    // Hacky Motion Profile :( (returns in radians/second)
    double getTargetVelFromTime() {
        long time = getTime();
        if (time < SPEED_UP_TIME) {
            return time * MAX_RPM/SPEED_UP_TIME * 2 * Math.PI / 60;
        } else {
            return MAX_RPM * 2 * Math.PI / 60;
        }
    }

    double getActualVelocity() {
        return -actuator.getVelocity(AngleUnit.RADIANS);
    }

    long getTime() {
        return System.currentTimeMillis() - baseTime;
    }

    void resetTime() {
        baseTime = System.currentTimeMillis();
    }
}

class Intake {
    final double SPEED = 0.7;
    DcMotorEx actuator;

    public void on() {
        actuator.setPower(SPEED);
    }

    public void off() {
        actuator.setPower(0.0);
    }
}

// The shooter uses a Neverest Series motor, which isn't part of the SDK, so we define it here
@com.qualcomm.robotcore.hardware.configuration.annotations.MotorType(ticksPerRev=28, gearing=1, maxRPM=6600, orientation = Rotation.CW)
@DeviceProperties(xmlTag="NeverestSeriesMotor", name="Neverest Series Motor")
interface SupaSpecialMotor {} // It's the Supa Mario Brotha's Supa Show