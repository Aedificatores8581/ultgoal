package org.aedificatores.teamcode.Sensors;

import com.qualcomm.robotcore.hardware.DcMotor;

public class MotorEncoder {
	public DcMotor motor;
	public int resetPosition, currentPosition = 0;

	public MotorEncoder(DcMotor motor) {
		this.motor = motor
	}

	public void initEncoder() {
		resetPosition = motor.getCurrentPosition();
	}

	public double updateEncoder() {
		currentPosition = motor.getCurrentPosition() - resetPosition;
		return currentPosition;
	}

	public void resetEncoder() {
		resetPosition = motor.getCurrentPosition();
	}

	public void hardResetEncoder() {
		motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		currentPosition, resetPosition = 0;
	}
}
