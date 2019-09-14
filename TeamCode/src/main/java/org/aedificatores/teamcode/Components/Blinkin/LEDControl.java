package org.aedificatores.teamcode.Components.Blinkin;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PWMOutputController;
import com.qualcomm.robotcore.hardware.Servo;

enum BlinkinLightsState {
	AESTHETIC,
	FUNCTIONAL,
	TEST
}

public class BlinkinLEDControl {
	public Servo colorControlServo;
	public PWMOutputController colorControlModulator;
	public BlinkinPresets blinkinPresets;

	public BlinkinLightsState lightState;

	public void setSolidColor(double PWM) {
		colorControlServo.setPosition(PWM);
	}

	public void setSolidColor(int PWM) {
		colorControlModulator.setPulseWidthPeriod(blinkinPresets.RAW_PWM_PORT, PWM);
	}

	public void init(HardwareMap hardwareMap) {
		colorControlServo = hardwareMap.servo.get("blinkin");
		lightState = lightState.AESTHETIC;
		setSolidColor(blinkinPresets.BLUE);
	}

	public void init() {
		lightState = lightState.AESTHETIC;
		setSolidColor(blinkinPresets.RAW_PWM_AQUA);
	}

	public static final double MAX_PWM_ADDRESS = -0.05;
	public static final double MIN_PWM_ADDRESS = -0.99;
	public void setPattern(double pattern) {
		if (pattern > MIN_PWM_ADDRESS && pattern < MAX_PWM_ADDRESS) {
			setSolidColor(pattern);
		} else {
			// Not within given range.
			setSolidColor(blinkinPresets.RED);
		}
	}

	private static final double MAX_RAW_PWM_ADDRESS = 1475;
	private static final double MIN_RAW_PWM_ADDRESS = 1005;
	public void setPattern(int pattern) {
		if (pattern > MIN_RAW_PWM_ADDRESS && pattern < MAX_RAW_PWM_ADDRESS) {
			setSolidColor(pattern);
		} else {
			setSolidColor(blinkinPresets.RED);
		}
	}

	public void grabCustomSetting() { }

	public void switchDisplayState(BlinkinLightsState state) { lightState = state; }

	public void switchDisplayState() {
		if (lightState == BlinkinLightsState.AESTHETIC) {
			lightState = BlinkinLightsState.FUNCTIONAL;
		} else if (lightState == BlinkinLightsState.FUNCTIONAL) {
			lightState = BlinkinLightsState.AESTHETIC;
		} else {
			lightState = BlinkinLightsState.AESTHETIC;
		}
	}

	public BlinkinLightsState getDisplayState() {
		return lightState;
	}
}
