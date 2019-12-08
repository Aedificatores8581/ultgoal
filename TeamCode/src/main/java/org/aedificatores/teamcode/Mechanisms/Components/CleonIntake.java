package org.aedificatores.teamcode.Mechanisms.Components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Universal.UniversalFunctions;

public class CleonIntake {

    private DcMotor leftIntake;
    private DcMotor rightIntake;

    private static final String INTAKE_LEFT_NAME = "lint";
    private static final String INTAKE_RIGHT_NAME = "rint";

    public CleonIntake(HardwareMap map){
        leftIntake = map.dcMotor.get(INTAKE_LEFT_NAME);
        leftIntake.setDirection(DcMotor.Direction.REVERSE);
        rightIntake = map.dcMotor.get(INTAKE_RIGHT_NAME);
    }

    public CleonIntake(HardwareMap map, String leftIntakeName, String rightIntakeName){
        leftIntake = map.dcMotor.get(leftIntakeName);
        leftIntake.setDirection(DcMotor.Direction.REVERSE);
        rightIntake = map.dcMotor.get(rightIntakeName);
    }

    /**
     * Sets the speed of the motors on the intake
     * @param speed intake motor speed (negative vaules makes the intake suck in blocks)
     */
    public void setIntakePower(double speed) {
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
}
