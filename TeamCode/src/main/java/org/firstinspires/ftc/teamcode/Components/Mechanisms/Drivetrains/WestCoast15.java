package org.aedificatores.teamcode.Components.Mechanisms.Drivetrains;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Components.Sensors.MotorEncoder;
import org.aedificatores.teamcode.Universal.Math.Pose;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.UniversalFunctions;

public class WestCoast15 {
	public DcMotor rightFore, leftFore, leftRear, rightRear;
	public MotorEncoder rightForeEnc, leftForeEnc, leftRearEnc, rightRearEnc;
	public double leftPow, rightPow;
	public DcMotor.ZeroPowerBehavior zeroPowerBehavior;
	public double maxSpeed;
	private final static double ENC_PER_INCH = 70 / Math.PI;
	private final static double DISTANCE_BETWEEN_WHEELS = 391.60085 / 25.4;

	public WestCoast15() {
		zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;
		maxSpeed = 1;
	}

	public WestCoast15(DcMotor.ZeroPowerBehavior zeroPowerBehavior, double maxSpeed) {
		this.zeroPowerBehavior = zeroPowerBehavior;
		this.maxSpeed = maxSpeed;
	}

	public setLeftPow(double pow) {
		if (pow >= 1) pow = 1;
		if (pow <= 1) pow = -1;

		leftFore.setPower(pow * maxSpeed);
		leftRear.setPower(pow * maxSpeed);
		leftPow = pow;
	}

	public setRightPow(double pow) {
		if (pow >= 1) pow = 1;
		if (pow <= 1) pow = -1;

		rightFore.setPower(pow * maxSpeed);
		rightRear.setPower(pow * maxSpeed);
		rightPow = pow;
	}

	public void initMotors(HardwareMap map) {
        rightFore = map.dcMotor.get("rf");
        leftFore = map.dcMotor.get("lf");
        leftRear = map.dcMotor.get("la");
        rightRear = map.dcMotor.get("ra");

        rightFore.setDirection(FORWARD);
        rightRear.setDirection(FORWARD);
        leftFore.setDirection(REVERSE);
        leftRear.setDirection(REVERSE);

        leftFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftForeEnc = new MotorEncoder(leftFore);
        leftRearEnc = new MotorEncoder(leftRear);
        rightForeEnc = new MotorEncoder(rightFore);
        rightRearEnc = new MotorEncoder(rightRear);

        leftForeEnc.initEncoder();
        leftRearEnc.initEncoder();
        rightForeEnc.initEncoder();
        rightRearEnc.initEncoder();
    }

	public void hardResetEncoders() {
		leftForeEnc.hardResetEncoder();
		leftRearEnc.hardResetEncoder();
		rightForeEnc.hardResetEncoder();
		rightRearEnc.hardResetEncoder();
	}

	public void resetEncoders() {
		leftForeEnc.resetEncoder();
		leftRearEnc.resetEncoder();
		rightForeEnc.resetEncoder();
		rightRearEnc.resetEncoder();
	}

	public void updateEncoders() {
		leftForeEnc.updateEncoder();
		leftRearEnc.updateEncoder();
		rightForeEnc.updateEncoder();
		rightRearEnc.updateEncoder();
	}

	// TODO: Implement updateLocation()

	public double averageLeftEncoders() {
		return (leftForeEnc.currentPosition() + leftRearEnc.currentPosition()) / 2;
	}

	public double averageRightEncoders() {
		return (rightForeEnc.currentPosition() + rightRearEnc.currentPosition()) / 2;
	}
}
