package org.aedificatores.teamcode.Mechanisms.Components;

import android.os.SystemClock;

import com.acmerobotics.roadrunner.control.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig.WobbleSub;
import org.aedificatores.teamcode.Mechanisms.Sensors.MagneticLimitSwitch;

public class WobbleGrabber {
    enum State {
        INITIATE_LIFT,
        MOVE_UP,
        IDLE_UP,

        MOVE_DOWN,
        RELEASE_WOBBLE,
        IDLE_DOWN

    };

    private final double CLOSED_POSITION = .54;
    private final double OPEN_POSITION = .26;
    private final double POWER = .7;

    DcMotorEx lift;
    MagneticLimitSwitch limitSwitchDown, limitSwitchUp;
    Servo gate;
    long startTime;
    State state;

    public WobbleGrabber(HardwareMap map) {
        limitSwitchDown = new MagneticLimitSwitch();
        limitSwitchUp = new MagneticLimitSwitch();

        lift = map.get(DcMotorEx.class, WobbleSub.MOT);
        limitSwitchDown.init(map, WobbleSub.LIMIT_DOWN);
        limitSwitchUp.init(map, WobbleSub.LIMIT_UP);
        gate = map.servo.get(WobbleSub.GATE);
        state = State.IDLE_UP;
    }

    public void init() {
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        gate.setPosition(CLOSED_POSITION);
    }

    public void drop() {
        state = State.MOVE_DOWN;
        resetTime();
    }

    public boolean isUp() {
        return state == State.IDLE_UP;
    }

    public boolean isDown() {
        return state == State.IDLE_DOWN;
    }

    private void resetTime() {
        startTime = System.currentTimeMillis();
    }

    private long getTime() {
        return System.currentTimeMillis() - startTime;
    }

    public void lift() {
        state = State.INITIATE_LIFT;
        resetTime();
    }

    public void update() {
        switch (state) {
            case INITIATE_LIFT:
                gate.setPosition(CLOSED_POSITION);
                if (getTime() > 400) {
                    resetTime();
                    state = State.MOVE_UP;
                }
                break;

            case MOVE_UP:
                lift.setPower(POWER);
                if (limitSwitchUp.isActive() && getTime() > 400) {
                    state = State.IDLE_UP;
                    lift.setPower(0.0);
                }
                break;

            case MOVE_DOWN:
                lift.setPower(-POWER);
                if (limitSwitchDown.isActive() && getTime() > 400) {
                    state = State.RELEASE_WOBBLE;
                    lift.setPower(0.0);
                }
                break;

            case RELEASE_WOBBLE:
                gate.setPosition(OPEN_POSITION);
                if (getTime() > 400) {
                    resetTime();
                    state = State.IDLE_DOWN;
                }
                break;
        }
    }
}
