package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;



public class CleonLift {

    public enum LiftState {
        MOVING_UP,
        MOVING_DOWN,
        STOPPED
    }

    private LiftState liftState;

    private DcMotor leftLiftMotor;
    private DcMotor rightLiftMotor;

    public static final double ENC_TO_TOP = 1000;
    public static final double ENC_TO_BOT = 0;
    public static final double SPEED = .7;

    public CleonLift(HardwareMap map) {
        leftLiftMotor = map.dcMotor.get("llift");
        rightLiftMotor = map.dcMotor.get("rlift");

        leftLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setLiftState(LiftState liftState) {
        this.liftState = liftState;
    }

    public void setLiftPower(double pow) {
        leftLiftMotor.setPower(pow);
        rightLiftMotor.setPower(pow);
    }

    public boolean atTop() {
        return leftLiftMotor.getCurrentPosition() > ENC_TO_TOP;
    }

    public boolean atBottom() {
        return leftLiftMotor.getCurrentPosition() < ENC_TO_BOT;
    }



    public void updateLift() {
        switch (liftState) {
            case MOVING_UP:
                setLiftPower(SPEED);
                if (atTop())
                    liftState = LiftState.STOPPED;
                break;
            case MOVING_DOWN:
                setLiftPower(-SPEED);
                if (atBottom())
                    liftState = LiftState.STOPPED;
                break;
            case STOPPED:
                setLiftPower(0.0);
                break;
        }
    }
}
