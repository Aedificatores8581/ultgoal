package org.aedificatores.teamcode.Mechanisms.Components;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;

import org.aedificatores.teamcode.Universal.Taemer;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Rotation;

import static org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig.ShootSub;

public class SawronShooterSubsystem {
    enum State {
        IDLE,
        KICKING,
        MOVING_UP
    }

    private State state = State.IDLE;
    private Shooter shooter;
    private Lift lift;
    private Kicker kicker;
    private Intake intakeMechanism;

    private Taemer timer;
    private int advancedCounter = 0;

    boolean advanceQueued = false;
    public SawronShooterSubsystem(HardwareMap map) {
        this(map, false);
    }

    public SawronShooterSubsystem(HardwareMap map, boolean isAuto) {
        shooter = new Shooter(map);
        kicker = new Kicker(map);
        lift = new Lift(map, isAuto);
        intakeMechanism = new Intake(map);
        timer = new Taemer();
    }

    public void intake() {
        intakeMechanism.foreward();
    }

    public void outtake() {
        intakeMechanism.reverse();
    }

    public void intakeOff() {
        intakeMechanism.off();
    }

    public void toggleIntake() {
        intakeMechanism.toggleForeward();
    }

    public void toggleOuttake() {
        intakeMechanism.toggleReverse();
    }

    public void runShooter() {
        shooter.runShooter();
    }

    public void setSpeed(double speed) {
        shooter.setSpeed(speed);
    }

    public void setSpeedMax() {
        shooter.setSpeed(Shooter.MAX_RPM);
    }

    public void stopShooter() {
        shooter.stopShooter();
    }

    public boolean runningShooterMotor() {
        return shooter.isRunning();
    }

    public void toggleShooter() {
        shooter.toggleShooter();
    }

    public void maxLowerLift() {
        lift.setPosition(Lift.Position.DOWN);
    }

    public void maxRaiseLift() {
        lift.setPosition(Lift.Position.POS_SHOOT_BOTTOM_RING);
    }

    public void setLiftPosShootTopRing() {
        lift.setPosition(Lift.Position.POS_SHOOT_TOP_RING);
    }

    public void setLiftPosShootMiddleRing() {
        lift.setPosition(Lift.Position.POS_SHOOT_MIDDLE_RING);
    }

    public void kick() {
        kicker.kick();
    }

    public void advance() {
        if (shooter.upToSpeed()) {
            forceAdvance();
        }
    }

    public void queueAdvance() {
        advanceQueued = true;
    }

    public void forceAdvance() {
        if (state == State.IDLE) {
            state = State.KICKING;
            kicker.kick();
            timer.resetTime();
            advancedCounter++;
            advanceQueued = false;
        }
    }

    public boolean shotAllThree() {
        return advancedCounter == 4 && state == State.IDLE;
    }

    public void resetShots() {
        advancedCounter = 0;
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
        if (advanceQueued && shooter.upToSpeed()) {
            forceAdvance();
        }

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
                if (timer.getTimeMillis() > lift.getPosition().getTime()) {
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
    public enum Position {
        DOWN(0.91, 900),
        POS_SHOOT_TOP_RING(0.25, 1000),
        POS_SHOOT_MIDDLE_RING(0.12, 400),
        POS_SHOOT_BOTTOM_RING(0.0, 400);

        private double pos;
        private int time;
        private static Position[] val = values();
        Position(double pos, int time) {
            this.pos = pos;
            this.time = time;
        }

        public double getPos() {
            return pos;
        }
        public int getTime() {
            return time;
        }

        public Position next() {
            return val[(this.ordinal() + 1) % val.length];
        }
    }

    public boolean isAuto; // HACK!

    Servo servo;
    Position position;

    Lift(HardwareMap map) {
        this(map, false);
    }

    Lift(HardwareMap map, boolean isAuto) {
        servo = map.servo.get(ShootSub.LIFT_SERV);
        position = Position.DOWN;
        this.isAuto = isAuto;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void gotoNextPosition() {
        position = position.next();
    }

    public Position getPosition() {
        return position;
    }

    public Position getNextPosition() {
        return position.next();
    }

    void update() {
        if(isAuto && position == Position.DOWN) {
            servo.setPosition(0.35); // SUPER HACK
        } else {
            servo.setPosition(position.getPos());
        }
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
                if (timer.getTimeMillis() > 300) {
                    state = State.KICK_BACK;
                    timer.resetTime();
                }
                break;
            case KICK_BACK:
                if (timer.getTimeMillis() > 300) {
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
    public static double MAX_RPM = 4550;
    public static double SPEED_UP_TIME = 6000; // milliseconds until max velocity

    DcMotorEx actuator;
    public static PIDFCoefficients velocityPIDCoeff = new PIDFCoefficients(200, 0.0, 0.0,0.0);
    boolean runningMotor = false;
    Taemer timer;
    double speed = MAX_RPM;

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
        actuator.setVelocity(-speed * 2 * Math.PI / 60, AngleUnit.RADIANS);
    }

    void stopShooter() {
        actuator.setVelocity(0);
        runningMotor = false;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isRunning() {
        return runningMotor;
    }

    public boolean upToSpeed() {
        // The -30 is a HACK due to some steady state error
        // TODO: FIX steady state error
        double target = getTargetVelocity() + 30;
        return Math.abs((getActualVelocity() - target) / target)  < .02;
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
        return -speed * 2 * Math.PI / 60;
    }

    double getActualVelocity() {
        return actuator.getVelocity(AngleUnit.RADIANS);
    }
}

class Intake {
    final double SPEED = 1.0;
    DcMotorEx actuator;

    enum IntakeState { FOREWARD, REVERSE, OFF}
    IntakeState state;

    Intake(HardwareMap map) {
        actuator = map.get(DcMotorEx.class, ShootSub.INTAKE_MOT);
        actuator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        state = IntakeState.OFF;
    }

    public void toggleForeward() {
        if (state == IntakeState.FOREWARD) {
            off();
        } else {
            foreward();
        }
    }

    public void toggleReverse() {
        if (state == IntakeState.REVERSE) {
            off();
        } else {
            reverse();
        }
    }

    public void foreward() {
        actuator.setPower(SPEED);
        state = IntakeState.FOREWARD;
    }

    public void reverse() {
        actuator.setPower(-SPEED);
        state = IntakeState.FOREWARD;
    }

    public void off() {
        actuator.setPower(0.0);
        state = IntakeState.REVERSE;
    }
}

// The shooter uses a Neverest Series motor, which isn't part of the SDK, so we define it here
@com.qualcomm.robotcore.hardware.configuration.annotations.MotorType(ticksPerRev=28, gearing=1, maxRPM=6600, orientation = Rotation.CW)
@DeviceProperties(xmlTag="NeverestSeriesMotor", name="Neverest Series Motor")
interface SupaSpecialMotor {} // It's the Supa Mario Brotha's Supa Show