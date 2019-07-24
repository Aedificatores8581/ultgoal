package org.aedificatores.teamcode.Components.Sensors;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TouchSensor {
	private DigitalChannel touchSensor;

	public void init(HardwareMap hardwareMap, String name) {
		touchSensor = hardwareMap.get(DigitalChannel.class, name);
		touchSensor.setMode(DigitalChannel.Mode.INPUT);
	}

	public boolean isPressed() {
		return !touchSensor.getState();
	}

	public String toString() {
		return isPressed() ? "Pressed" : "Released";
	}
}
