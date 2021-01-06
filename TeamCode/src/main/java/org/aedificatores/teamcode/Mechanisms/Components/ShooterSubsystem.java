package org.aedificatores.teamcode.Mechanisms.Components;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;

import org.aedificatores.teamcode.Mechanisms.Sensors.Encoder;
import org.aedificatores.teamcode.Universal.Taemer;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Rotation;

import static org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig.ShootSub;

public class ShooterSubsystem {
    enum State {
        IDLE,
        KICKING,
        MOVING_UP
    }

    private State state = State.IDLE;
    private Shooter shooter;
    private Lift lift;
    private Kicker kicker;
    private Taemer timer;
    private int advancedCounter = 0;

    public ShooterSubsystem(HardwareMap map) {
        shooter = new Shooter(map);
        kicker = new Kicker(map);
        lift = new Lift(map);
        timer = new Taemer();
    }

    public void runShooter() {
        shooter.runShooter();
    }

    public void stopShooter() {
        shooter.stopShooter();
    }

    public void toggleShooter() {
        shooter.toggleShooter();
    }

    public void advance() {
        if (state == State.IDLE) {
            state = State.KICKING;
            kicker.kick();
            timer.resetTime();
            advancedCounter++;
        }
    }

    public boolean shotAllThree() {
        return advancedCounter == 4 && state == State.IDLE;
    }

    public int getAdvancedCounter() {
        return advancedCounter;
    }

    public double getTargetShooterVelocity() {
        return shooter.getTargetVelocity();
    }
    public double getActualShooterVelocity() {
        return shooter.getActualVelocity();
    }

    public boolean idle() {
        return state == State.IDLE;
    }

    public void update() {
        switch (state) {
            case IDLE:
                break;
            case KICKING:
                if (kicker.idle()) {
                    timer.resetTime();
                    state = State.MOVING_UP;
                    lift.gotoNextPosition();
                }
                break;
            case MOVING_UP:
                if (timer.getTime() > 500) {
                    state = State.IDLE;
                }
                break;
        }

        shooter.update();
        lift.update();
        kicker.update();
    }
}

class Lift {
    enum Position {
        DOWN(0.87),
        POS_SHOOT_TOP_RING(0.25),
        POS_SHOOT_MIDDLE_RING(0.09),
        POS_SHOOT_BOTTOM_RING(0.0);

        private double pos;
        private static Position[] val = values();
        Position(double pos) {
            this.pos = pos;
        }

        public double getPos() {
            return pos;
        }

        public Position next() {
            return val[(this.ordinal() + 1) % val.length];
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

    public void gotoNextPosition() {
        position = position.next();
    }

    public Position getNextPosition() {
        return position.next();
    }

    void update() {
        servo.setPosition(position.getPos());
    }
}

class Kicker {
    enum State {
        IDLE(.2),
        KICK_FORE(0.0),
        KICK_BACK(0.2);

        private double pos;

        State(double pos) {
            this.pos = pos;
        }

        public double getPos() {
            return pos;
        }
    }

    Servo servo;
    State state = State.IDLE;
    Taemer timer;

    Kicker(HardwareMap map) {
        servo = map.servo.get(ShootSub.KICK_SERV);
        timer = new Taemer();
        timer.resetTime();
    }

    void update() {
        switch (state){
            case KICK_FORE:
                if (timer.getTime() > 300) {
                    state = State.KICK_BACK;
                    timer.resetTime();
                }
                break;
            case KICK_BACK:
                if (timer.getTime() > 300) {
                    state = State.IDLE;
                    timer.resetTime();
                }
                break;
        }

        servo.setPosition(state.getPos());
    }

    boolean idle() {
        return state == State.IDLE;
    }

    void kick() {
        state = State.KICK_FORE;
        timer.resetTime();
    }
}

@Config
class Shooter {
    public static double MAX_RPM = 4200;
    public static double SPEED_UP_TIME = 6000; // milliseconds until max velocity

    DcMotorEx actuator;
    public static PIDFCoefficients velocityPIDCoeff = new PIDFCoefficients(200, 0.0, 0.0,0.0);
    boolean runningMotor = false;
    Taemer timer;
    Shooter(HardwareMap map) {
        actuator = map.get(DcMotorEx.class, ShootSub.SHOOT_MOT);
        actuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        actuator.setDirection(DcMotorSimple.Direction.FORWARD);
        actuator.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, velocityPIDCoeff);
        timer = new Taemer();
        timer.resetTime();
    }

    void runShooter() {
        runningMotor = true;
        timer.resetTime();
        actuator.setVelocity(-MAX_RPM * 2 * Math.PI / 60, AngleUnit.RADIANS);
    }

    void stopShooter() {
        actuator.setVelocity(0);
        runningMotor = false;
    }

    void toggleShooter() {
        if (runningMotor) {
            stopShooter();
        } else {
            runShooter();

        }
    }

    void update () {
    }

    double getTargetVelocity() {
        return -MAX_RPM * 2 * Math.PI / 60;
    }

    double getActualVelocity() {
        return actuator.getVelocity(AngleUnit.RADIANS);
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