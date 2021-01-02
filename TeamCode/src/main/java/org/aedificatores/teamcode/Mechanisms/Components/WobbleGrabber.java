package org.aedificatores.teamcode.Mechanisms.Components;

import com.acmerobotics.dashboard.config.Config;
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

    private final int ENC_UP = 0;
    private final int ENC_PARTWAY = -1743;
    private final int ENC_DOWN = -2884;

    DcMotorEx actuator;
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

        actuator = map.get(DcMotorEx.class, WobbleSub.MOT);
        actuator.setDirection(DcMotorSimple.Direction.REVERSE);
        actuator.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, pidfCoefficients);

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
            actuator.setTargetPosition(ENC_PARTWAY);
            actuator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } else {
            actuator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

    }

    public Mode getMode() {return mode;}
    
    public int getPosition() {
        return actuator.getCurrentPosition();
    }

    public int getTargetPosition() {
        return actuator.getTargetPosition();
    }
    
    public void setPower(double power) {
        if (mode == Mode.TELEOP) {
            actuator.setPower(power);
        }
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
        actuator.setTargetPosition(ENC_UP);
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
            actuator.setTargetPosition(ENC_DOWN);
            actuator.setPower(1.0);
        }
    }

    public boolean isBusy() {
        return !(subsystemState == SubsystemState.IDLE);
    }

    public void update() {
        switch (subsystemState) {
            case RESET:
                if (mode == Mode.AUTO) {
                    if (!actuator.isBusy() || limitSwitchUp.isActive()) {
                        actuator.setPower(0.0);
                        actuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        actuator.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        if (grabberState == GrabberState.IDLE) {
                            subsystemState = SubsystemState.IDLE;
                        }
                    }
                    
                } else {
                    if (!limitSwitchUp.isActive()) {
                        actuator.setPower(POWER);
                    }
                    if (limitSwitchUp.isActive() && grabberState == GrabberState.IDLE) {
                        actuator.setPower(0.0);

                        if (grabberState== GrabberState.IDLE) {
                            actuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            actuator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                            subsystemState = SubsystemState.IDLE;
                        }
                    }
                }
                break;
            case CLOSE_GRABBER:
                if (grabberState == GrabberState.IDLE) {
                    subsystemState = SubsystemState.MOVE_UP;
                    actuator.setTargetPosition(ENC_PARTWAY);
                    actuator.setPower(1.0);
                }
                break;
            case MOVE_UP:
                if (!actuator.isBusy()) {
                    actuator.setPower(0.0);
                    subsystemState = SubsystemState.IDLE;
                }
                break;
            case MOVE_DOWN:
                if (!actuator.isBusy()) {
                    actuator.setPower(0.0);
                    openGrabber();
                    subsystemState = SubsystemState.OPEN_GRABBER;
                }
                break;
            case OPEN_GRABBER:
                if (grabberState == GrabberState.IDLE) {
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
    }
}
