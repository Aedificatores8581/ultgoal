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

        INITIATE_DROP,
        MOVE_DOWN,
        IDLE

    };

    private final double CLOSED_POSITION = .54;
    private final double OPEN_POSITION = .26;
    private final double POWER = .7;

    DcMotorEx lift;
    MagneticLimitSwitch limitSwitch;
    Servo gate;
    long startTime;
    State state;

    public WobbleGrabber(HardwareMap map) {
        limitSwitch = new MagneticLimitSwitch();

        lift = map.get(DcMotorEx.class, WobbleSub.MOT);
        limitSwitch.init(map, WobbleSub.LIMIT);
        gate = map.servo.get(WobbleSub.GATE);
    }

    public void init() {
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void drop() {
        state = State.INITIATE_DROP;
        resetTime();
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
                if (limitSwitch.isActive() && getTime() > 400) {
                    state = State.IDLE;
                }
                break;

            case INITIATE_DROP:
                gate.setPosition(OPEN_POSITION);
                if (getTime() > 400) {
                    resetTime();
                    state = State.MOVE_DOWN;
                }
                break;

            case MOVE_DOWN:
                lift.setPower(-POWER);
                if (limitSwitch.isActive() && getTime() > 400) {
                    state = State.IDLE;
                }
                break;
        }
    }
}
