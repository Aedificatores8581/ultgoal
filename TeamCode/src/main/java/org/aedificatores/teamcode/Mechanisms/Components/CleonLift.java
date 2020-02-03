package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.aedificatores.teamcode.Components.Sensors.TouchSensor;
import org.aedificatores.teamcode.Universal.Math.PIDController;


public class CleonLift {

    public PIDController liftPID = new PIDController(0,0,0, 150);

    private boolean usingPID = false;
    public DcMotor liftMotor1;
    private DcMotor liftMotor2;

    private TouchSensor limitSwitch;

    public static final int ENC_TO_BOT = 20;
    private static final double SPEED = .9;

    private static final double MIN_EXTENSION_POWER = 0.2;
    private static final double MAX_RETRACT_POWER = 0.13;
    private static final double IDLE_POWER = 0;
    private static final double BRAKE_POWER = (MIN_EXTENSION_POWER + MAX_RETRACT_POWER) / 2;

    private static final int[] PID_SETPOINTS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int[] MIN_SNAP_HEIGHT = new int[12];

    public int closestBlockHeight = 1;


    private static final double MOVEMENT_THRESHOLD = 0;

    public CleonLift(HardwareMap map) {
        liftMotor1 = map.dcMotor.get("llift");
        liftMotor2 = map.dcMotor.get("rlift");

        limitSwitch.init(map, "botls");

        liftMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        MIN_SNAP_HEIGHT[0] = 0;
        for (int i = 1; i < 12; i++){
            MIN_SNAP_HEIGHT[i] = (PID_SETPOINTS[i] + PID_SETPOINTS[i-1]) / 2;
        }
    }

    //normalizes lift power input so that the lift has the same speed, regardless of the sign of the input
    public void setNormalizedLiftPower(double pow){
        if (pow > MOVEMENT_THRESHOLD)
            setLiftPower(pow / (1 - MIN_EXTENSION_POWER) + MIN_EXTENSION_POWER);
        if (pow < -MOVEMENT_THRESHOLD)
            setLiftPower(pow / (1 - MIN_EXTENSION_POWER) + MAX_RETRACT_POWER);
        else
            setLiftPower(BRAKE_POWER);
    }

    public void setLiftPower(double pow) {

        if(pow < MAX_RETRACT_POWER &&(atBottom() || limitSwitch.isPressed())) {
            liftMotor1.setPower(0);
            liftMotor1.setPower(0);
            closestBlockHeight = 1;
        }
        else {
            liftMotor1.setPower(pow * SPEED);
            liftMotor2.setPower(pow * SPEED);
        }
    }

    public void idle(){
        setLiftPower(IDLE_POWER);
    }

    public void snapToStone(int stone){
        usingPID = true;
        liftPID.setpoint = PID_SETPOINTS[stone - 1];
    }

    public void updateBlockHeight(){
        if(MIN_SNAP_HEIGHT[closestBlockHeight - 1] > liftMotor1.getCurrentPosition())
            closestBlockHeight--;
        else if(MIN_SNAP_HEIGHT[closestBlockHeight] < closestBlockHeight)
            closestBlockHeight++;

    }

    public void setPowerUsinngPID(){
        liftPID.processVar = liftMotor1.getCurrentPosition();
        liftPID.idealLoop();
        setNormalizedLiftPower(liftPID.currentOutput);
    }

    public boolean atBottom() {
        return liftMotor1.getCurrentPosition() < ENC_TO_BOT;
    }
}
