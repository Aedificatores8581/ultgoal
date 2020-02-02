package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.aedificatores.teamcode.Components.Sensors.TouchSensor;
import org.aedificatores.teamcode.Universal.Math.PIDController;


public class CleonLift {

    public enum  LiftBrakeState {
        STOPPED,
        IDLE
    }

    private LiftBrakeState liftState;

    public PIDController liftPID;

    private DcMotor liftMotor1;
    private DcMotor liftMotor2;

    private TouchSensor limitSwitch;

    private static final double ENC_TO_BOT = 20;
    private static final double SPEED = .9;

    private static final double MIN_EXTENSION_POWER = 0.2;
    private static final double MAX_RETRACT_POWER = 0.13;
    private static final double IDLE_POWER = 0;
    private static final double BRAKE_POWER = (MIN_EXTENSION_POWER + MAX_RETRACT_POWER) / 2;

    private static final double MOVEMENT_THRESHOLD = 0;
    public CleonLift(HardwareMap map) {
        liftMotor1 = map.dcMotor.get("llift");
        liftMotor2 = map.dcMotor.get("rlift");

        limitSwitch.init(map, "botls");

        liftMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //normalizes lift power input so that the lift has the same speed, regardless of the sign of the input
    public void setNormalizedLiftPower(double pow){
        if (pow > MOVEMENT_THRESHOLD)
            setLiftPower(pow / (1 - MIN_EXTENSION_POWER) + MIN_EXTENSION_POWER);
        if (pow < -MOVEMENT_THRESHOLD)
            setLiftPower(pow / (1 - MAX_RETRACT_POWER) + MAX_RETRACT_POWER);
        else
            setLiftPower(BRAKE_POWER);
    }

    private void setLiftPower(double pow) {

        if(pow < MIN_EXTENSION_POWER &&(atBottom() || limitSwitch.isPressed())) {
            liftMotor1.setPower(0);
            liftMotor1.setPower(0);
        }
        else {
            double setpoint = pow * SPEED;
            liftMotor1.setPower(pow * SPEED);
            liftMotor2.setPower(pow * SPEED);
        }
    }

    public void idle(){
        setLiftPower(IDLE_POWER);
    }

    public void snapToNextPreset(){

    }

    public boolean atBottom() {
        return liftMotor1.getCurrentPosition() < ENC_TO_BOT;
    }
}
