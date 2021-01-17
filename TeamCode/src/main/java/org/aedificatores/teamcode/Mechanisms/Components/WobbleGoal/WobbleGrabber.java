package org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import static org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig.WobbleSub;
import org.aedificatores.teamcode.Mechanisms.Sensors.MagneticLimitSwitch;
import org.aedificatores.teamcode.Universal.Taemer;

@Config
public class WobbleGrabber {
    enum SubsystemState {
        RESET,
        IDLE,
        CLOSE_GRABBER,
        MOVE_UP,
        MOVE_DOWN,
        OPEN_GRABBER,
        MOVE_RELEASE,
        MOVE_BACK_TO_DOWN,
        
        UNINITIALIZED
    };
    
    enum GrabberState {
        CLOSE_PULLER,
        CLOSE_GATE,
        OPEN_GATE,
        OPEN_PULLER,
        IDLE
    }

    public enum Mode {
        TELEOP, AUTO
    }

    private final double GATE_CLOSED_POSITION = .54;
    private final double GATE_OPEN_POSITION = .15;
    private final double PULL_CLOSED_POSITION=.7;
    private final double PULL_OPEN_POSITION=0.1;
    private final double POWER = .7;

    private final double ENC_UP = 0;
    private final double ENC_PARTWAY = -2 * Math.PI / 3;
    private final double ENC_DOWN = -Math.PI;
    private final double ENC_RELEASE = -6 * Math.PI / 5;



    WobbleMotor motor;
    MagneticLimitSwitch limitSwitchUp, limitSwitchDown;
    Servo gate, puller;
    SubsystemState subsystemState;
    GrabberState grabberState;
    Mode mode;
    Taemer timer;

    public static PIDFCoefficients pidfCoefficients = new PIDFCoefficients(10.0,0.0,0.0,0.0);

    private boolean grabberClosed = false;
    boolean onlyGrab = false;
    boolean partway;

    public WobbleGrabber(HardwareMap map) {
        this(map, Mode.AUTO);
    }

    public WobbleGrabber(HardwareMap map, Mode m) {
        limitSwitchDown = new MagneticLimitSwitch();
        limitSwitchUp = new MagneticLimitSwitch();
        limitSwitchDown.init(map, WobbleSub.LIMIT_DOWN);
        limitSwitchUp.init(map, WobbleSub.LIMIT_UP);

        motor = new WobbleMotor(map);

        gate = map.servo.get(WobbleSub.GATE);
        puller = map.servo.get(WobbleSub.PULL);

        subsystemState = SubsystemState.UNINITIALIZED;
        setMode(m);
        grabberState = GrabberState.IDLE;
        timer = new Taemer();
    }

    public void setMode(Mode m) {
        mode = m;
        if (mode == Mode.AUTO) {
            motor.gotoAngle(ENC_PARTWAY);
            motor.setMode(WobbleMotor.Mode.AUTO);
        } else {
            motor.setMode(WobbleMotor.Mode.TELEOP);
        }

    }

    public Mode getMode() {return mode;}
    
    public double getAngleRadians() {
        return motor.getCurrentAngle();
    }
    public double getAngleDegrees() {
        return motor.getCurrentAngle() * 180 / Math.PI;
    }
    public double getCurrentTargetAngleRadians() {
        return motor.getCurrentTargetAngle();
    }
    public double getCurrentTargetAngleDegrees() {
        return motor.getCurrentTargetAngle() * 180 / Math.PI;
    }
    
    public void setPower(double power) {
        if (mode == Mode.TELEOP) {
            motor.setPower(power);
        }
    }

    public boolean isGrabberClosed() {
        return grabberClosed;
    }

    public void openGrabber() {
        grabberState = GrabberState.OPEN_GATE;
        timer.resetTime();
    }

    public void closeGrabber() {
        grabberState = GrabberState.CLOSE_PULLER;
        timer.resetTime();
    }
    
    public void reset() {
        subsystemState = SubsystemState.RESET;
        timer.resetTime();
        motor.gotoAngle(ENC_UP);
        closeGrabber();
    }

    public void lift() {
        if (mode == Mode.AUTO) {
            subsystemState = SubsystemState.CLOSE_GRABBER;
            closeGrabber();
        }
    }

    public void drop() {
        if (mode == Mode.AUTO) {
            subsystemState = SubsystemState.MOVE_DOWN;
            motor.gotoAngle(ENC_DOWN);
        }
    }

    public boolean isBusy() {
        return !(subsystemState == SubsystemState.IDLE);
    }

    public boolean isReleasing() {
        return subsystemState == SubsystemState.MOVE_RELEASE;
    }

    public void update() {
        switch (subsystemState) {
            case RESET:
                if (mode == Mode.AUTO) {
                    if (!motor.isBusy() || limitSwitchUp.isActive()) {
                        motor.resetEncoders();

                        if (grabberState == GrabberState.IDLE) {
                            subsystemState = SubsystemState.IDLE;
                        }
                    }
                    
                } else {
                    if (!limitSwitchUp.isActive()) {
                        motor.setPower(POWER);
                    }
                    if (limitSwitchUp.isActive() && grabberState == GrabberState.IDLE) {
                        motor.setPower(0.0);

                        if (grabberState== GrabberState.IDLE) {
                            motor.resetEncoders();

                            subsystemState = SubsystemState.IDLE;
                        }
                    }
                }
                break;
            case CLOSE_GRABBER:
                if (grabberState == GrabberState.IDLE) {
                    subsystemState = SubsystemState.MOVE_UP;
                    motor.gotoAngle(ENC_PARTWAY);
                }
                break;
            case MOVE_UP:
                if (!motor.isBusy()) {
                    motor.setPower(0.0);
                    subsystemState = SubsystemState.IDLE;
                }
                break;
            case MOVE_DOWN:
                if (!motor.isBusy()) {
                    openGrabber();
                    subsystemState = SubsystemState.OPEN_GRABBER;
                }
                break;
            case OPEN_GRABBER:
                if (grabberState == GrabberState.IDLE) {
                    subsystemState = SubsystemState.MOVE_RELEASE;
                    motor.gotoAngle(ENC_RELEASE);
                }
                break;
            case MOVE_RELEASE:
                if (!motor.isBusy()) {
                    motor.gotoAngle(ENC_DOWN);
                    subsystemState = SubsystemState.MOVE_BACK_TO_DOWN;
                }
                break;
            case MOVE_BACK_TO_DOWN:
                if (!motor.isBusy()) {
                    subsystemState = SubsystemState.IDLE;
                }
                break;
        }

        switch (grabberState) {

            case CLOSE_PULLER:
                puller.setPosition(PULL_CLOSED_POSITION);
                if (timer.getTime() > 300 || grabberClosed) {
                    grabberState = GrabberState.CLOSE_GATE;
                    timer.resetTime();
                }
                break;

            case CLOSE_GATE:
                gate.setPosition(GATE_CLOSED_POSITION);
                if (timer.getTime() > 300 || grabberClosed) {
                    grabberState = GrabberState.IDLE;
                    grabberClosed = true;
                }
                break;

            case OPEN_GATE:
                gate.setPosition(GATE_OPEN_POSITION);
                if (timer.getTime() > 300 || !grabberClosed) {
                    grabberState = GrabberState.OPEN_PULLER;
                }
                break;

            case OPEN_PULLER:
                puller.setPosition(PULL_OPEN_POSITION);
                if (timer.getTime() > 300 || !grabberClosed) {
                    grabberState = GrabberState.IDLE;
                    timer.resetTime();
                    grabberClosed = false;
                }
                break;
            case IDLE:
                break;
        }

        motor.update();
    }
}

