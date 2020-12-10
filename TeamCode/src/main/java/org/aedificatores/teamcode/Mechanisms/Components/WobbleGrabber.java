package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig.WobbleSub;
import org.aedificatores.teamcode.Mechanisms.Sensors.MagneticLimitSwitch;
import org.aedificatores.teamcode.Universal.Taemer;

public class WobbleGrabber {
    enum State {
        PULL_WOBBLE,
        CLOSE_GATE,
        MOVE_UP,
        IDLE_UP,

        MOVE_DOWN,
        OPEN_GATE,
        OPEN_PULLER,
        IDLE_DOWN

    };

    private final double GATE_CLOSED_POSITION = .54;
    private final double GATE_OPEN_POSITION = .15;
    private final double PULL_CLOSED_POSITION=.3;
    private final double PULL_OPEN_POSITION=0.0;
    private final double POWER = .7;

    DcMotorEx lift;
    MagneticLimitSwitch limitSwitchDown, limitSwitchUp;
    Servo gate, puller;
    State state;
    Taemer timer;

    public WobbleGrabber(HardwareMap map) {
        limitSwitchDown = new MagneticLimitSwitch();
        limitSwitchUp = new MagneticLimitSwitch();

        lift = map.get(DcMotorEx.class, WobbleSub.MOT);
        limitSwitchDown.init(map, WobbleSub.LIMIT_DOWN);
        limitSwitchUp.init(map, WobbleSub.LIMIT_UP);
        gate = map.servo.get(WobbleSub.GATE);
        puller = map.servo.get(WobbleSub.PULL);
        state = State.IDLE_UP;
        timer = new Taemer();
    }

    public void init() {
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        gate.setPosition(GATE_CLOSED_POSITION);
        puller.setPosition(PULL_CLOSED_POSITION);
    }

    public boolean isUp() {
        return state == State.IDLE_UP;
    }

    public boolean isDown() {
        return state == State.IDLE_DOWN;
    }

    public void setUp(boolean isUp) {
        state = isUp ? State.IDLE_UP : State.IDLE_DOWN;
    }

    public void drop() {
        if (isUp()) {
            state = State.MOVE_DOWN;
            timer.resetTime();
        }
    }

    public void lift() {
        if (isDown()) {
            forceLift();
        }
    }

    public void forceLift() {
        state = State.PULL_WOBBLE;
        timer.resetTime();
    }

    public void update() {
        switch (state) {
            case PULL_WOBBLE:
                puller.setPosition(PULL_CLOSED_POSITION);
                if (timer.getTime() > 400) {
                    timer.resetTime();
                    state = State.CLOSE_GATE;
                }
                break;
            case CLOSE_GATE:
                gate.setPosition(GATE_CLOSED_POSITION);
                if (timer.getTime() > 400) {
                    timer.resetTime();
                    state = State.MOVE_UP;
                }
                break;

            case MOVE_UP:
                lift.setPower(POWER);
                if (limitSwitchUp.isActive() && timer.getTime() > 400) {
                    timer.resetTime();
                    state = State.IDLE_UP;
                    lift.setPower(0.0);
                }
                break;

            case MOVE_DOWN:
                lift.setPower(-POWER);
                if (limitSwitchDown.isActive() && timer.getTime() > 400) {
                    timer.resetTime();
                    state = State.OPEN_GATE;
                    lift.setPower(0.0);
                }
                break;

            case OPEN_GATE:
                gate.setPosition(GATE_OPEN_POSITION);
                if (timer.getTime() > 400) {
                    timer.resetTime();
                    state = State.OPEN_PULLER;
                }
                break;

            case OPEN_PULLER:
                puller.setPosition(PULL_OPEN_POSITION);
                if (timer.getTime() > 400) {
                    timer.resetTime();
                    state = State.IDLE_DOWN;
                }
                break;
        }
    }
}
