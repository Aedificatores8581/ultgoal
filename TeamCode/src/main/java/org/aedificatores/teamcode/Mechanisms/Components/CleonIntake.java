package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class CleonIntake {

    private DcMotor leftIntake;
    private DcMotor rightIntake;

    private static final String INTAKE_LEFT_NAME = "lint";
    private static final String INTAKE_RIGHT_NAME = "rint";

    public DistanceSensor distanceSensor;

    public enum StoneState {
        SEARCHING,
        INTAKING,
        OBTAINED
    }
    public StoneState stoneState = StoneState.SEARCHING;

    private static final double BLOCK_DISTANCE_MM = 50;

    /**
     * Instantiates CleonIntake with default hardware map names
     * @param map Hardware map to get the devices from
     */
    public CleonIntake(HardwareMap map){
        leftIntake = map.dcMotor.get(INTAKE_LEFT_NAME);
        leftIntake.setDirection(DcMotor.Direction.REVERSE);
        rightIntake = map.dcMotor.get(INTAKE_RIGHT_NAME);
        distanceSensor = map.get(DistanceSensor.class, "intakecolor");
    }

    /**
     * Sets the speed of the motors on the intake
     * @param speed intake motor speed (negative vaules makes the intake suck in blocks)
     */
    public void setIntakePower(double speed) {
        updateIntakeState();
        leftIntake.setPower(UniversalFunctions.clamp(-1.0,speed,1.0));
        rightIntake.setPower(UniversalFunctions.clamp(-1.0,speed,1.0));
    }

    /**
     * Sets the speed of the left motor on the intake
     * @param speed intake motor speed (positive vaules make the intake rotate counter-clockwise)
     */
    public void setLeftIntake(double speed){
        leftIntake.setPower(UniversalFunctions.clamp(-1.0,speed, 1.0));
    }

    /**
     * Sets the speed of the right motor on the intake
     * @param speed intake motor speed (positive vaules make the intake rotate clockwise)
     */
    public void setRightIntake(double speed){
        rightIntake.setPower(UniversalFunctions.clamp(-1.0,speed, 1.0));
    }

    public void updateIntakeState(){
        switch (stoneState) {
            case SEARCHING:
                if (distanceSensor.getDistance(DistanceUnit.MM) < BLOCK_DISTANCE_MM) {
                    stoneState = StoneState.INTAKING;
                }
                break;
            case INTAKING:
                if (distanceSensor.getDistance(DistanceUnit.MM) > BLOCK_DISTANCE_MM) {
                    stoneState = StoneState.OBTAINED;
                }
                break;
        }
    }
    public void resetIntakeState(){
        stoneState = StoneState.SEARCHING;
    }
}
