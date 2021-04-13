package org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Components.SawronWobbleGoal.SawronWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Universal.Math.Pose;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.Taemer;

public class GandalfWobbleGrabber {
    public enum Mode {
        AUTO(GandalfWobbleMotor.Mode.AUTO),
        TELEOP(GandalfWobbleMotor.Mode.TELEOP);

        private GandalfWobbleMotor.Mode motorMode;

        Mode(GandalfWobbleMotor.Mode motorMode) {
            this.motorMode = motorMode;
        }

        public GandalfWobbleMotor.Mode getMotorMode() {
            return motorMode;
        }
    };

    enum SubsystemState {
        IDLE,
        CLOSE_GRABBER,
        MOVE_UP,
        MOVE_DOWN,
        OPEN_GRABBER,
        UNINITIALIZED
    }

    public static final double GRABBER_TIME_OUT = .5;
    public static final double SERV_UP_CLOSED_POS = .1278;
    public static final double SERV_LO_CLOSED_POS = .052;
    public static final double SERV_UP_OPEN_POS = .66;
    public static final double SERV_LO_OPEN_POS = .6;
    public static final double LIFTED_ANGLE = 240*Math.PI/180;
    public static final double DROPPED_ANGLE = 270*Math.PI/180;

    Servo upper, lower;
    GandalfWobbleMotor actuator;

    Mode mode;
    Taemer clock;
    SubsystemState subsystemState = SubsystemState.UNINITIALIZED;

    public GandalfWobbleGrabber(HardwareMap map, Mode m) {
        upper = map.servo.get(GandalfBotConfig.WOBBLE.SERV_UP);
        lower = map.servo.get(GandalfBotConfig.WOBBLE.SERV_LO);
        actuator = new GandalfWobbleMotor(map);
        clock = new Taemer();
        setMode(m);
    }

    public void setMode(Mode m) {
        mode = m;
        actuator.setMode(m.getMotorMode());
    }
    public Mode getMode() {return mode;}

    public double getAngleRadians() {
        return actuator.getCurrentAngleRadians();
    }
    public double getAngleDegrees() {
        return actuator.getCurrentAngleDegrees();
    }
    public double getCurrentTargetAngleRadians() {
        return actuator.getTargetAngleRadians();
    }
    public double getCurrentTargetAngleDegrees() {
        return actuator.getTargetAngleDegrees();
    }

    public void setPower(double power) {
        if (mode == Mode.TELEOP) {
            if (power <= 0.0 && actuator.getCurrentAngleDegrees() >= 270) {
                actuator.setPower(0.0);
            } else if (power > 0.0 && actuator.getCurrentAngleDegrees() <= 0) {
                actuator.setPower(0.0);
            } else {
                actuator.setPower(power);
            }

        }
    }

    public void openGrabber() {
        upper.setPosition(SERV_UP_OPEN_POS);
        lower.setPosition(SERV_LO_OPEN_POS);
    }

    public void closeGrabber() {
        upper.setPosition(SERV_UP_CLOSED_POS);
        lower.setPosition(SERV_LO_CLOSED_POS);
    }

    public void lift() {
        if (subsystemState == SubsystemState.UNINITIALIZED || subsystemState == SubsystemState.IDLE) {
            subsystemState = SubsystemState.CLOSE_GRABBER;
            upper.setPosition(SERV_UP_CLOSED_POS);
            lower.setPosition(SERV_LO_CLOSED_POS);
            clock.resetTime();
        }
    }

    public void drop() {
        if (subsystemState == SubsystemState.UNINITIALIZED || subsystemState == SubsystemState.IDLE) {
            subsystemState = SubsystemState.MOVE_DOWN;
            actuator.gotoAngle(DROPPED_ANGLE);
        }
    }

    public void update() {
        switch (subsystemState) {
            case IDLE:
                break;
            case CLOSE_GRABBER:
                if (clock.getTimeSec() < GRABBER_TIME_OUT) {
                    subsystemState = SubsystemState.MOVE_UP;
                    actuator.gotoAngle(LIFTED_ANGLE);
                }
                break;
            case MOVE_UP:
                if (!actuator.isMoving()) {
                    subsystemState = SubsystemState.IDLE;
                }
                break;
            case MOVE_DOWN:
                if (!actuator.isMoving()) {
                    subsystemState = SubsystemState.OPEN_GRABBER;
                    upper.setPosition(SERV_UP_OPEN_POS);
                    lower.setPosition(SERV_LO_OPEN_POS);
                    clock.resetTime();
                }
                break;
            case OPEN_GRABBER:
                if (clock.getTimeSec() < GRABBER_TIME_OUT) {
                    subsystemState = SubsystemState.IDLE;
                }
                break;
            case UNINITIALIZED:
                break;
        }
        actuator.update();
    }
}
