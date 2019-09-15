package org.aedificatores.teamcode.Sensors;

import com.qualcomm.robotcore.hardware.AnalogInput;

public class Encoder {
	AnalogInput encoder;

	public static int directionMult;
	public static double ticks;
	public static double tickVoltage;
	public RotationDirection rotationDirection;

	public Encoder(AnalogInput enc, double voltage, RotationDirection dir) {
		tickVoltage = voltage;
		directionMult = dir.equals(RotationDirection.CLOCKWISE) ? 1 : -1;
		encoder = enc;
	}

	public void updateEncoder() {
		if (encoder.getVoltage() > tickVoltage) {
			ticks += directionMult;
		}
	}

	public void switchDirection() {
        switch (rotationDirection) {
            case CLOCKWISE:
                directionMult *= -1;
                rotationDirection = RotationDirection.COUNTER_CLOCKWISE;
                break;
            case COUNTER_CLOCKWISE:
                directionMult *= -1;
                rotationDirection = RotationDirection.CLOCKWISE;
                break;
        }
    }

	public enum RotationDirection {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }
}
