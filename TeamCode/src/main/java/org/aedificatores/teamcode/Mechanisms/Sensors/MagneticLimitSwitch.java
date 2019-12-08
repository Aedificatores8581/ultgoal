package org.aedificatores.teamcode.Mechanisms.Sensors;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MagneticLimitSwitch {
	private DigitalChannel limitSwitch;

	public void init(HardwareMap hardwareMap, String name) {
		limitSwitch = hardwareMap.digitalChannel.get(name);
		limitSwitch.setMode(DigitalChannel.Mode.INPUT);
	}

	public boolean isActive() {
		return limitSwitch.getState();
	}

	public String toString() {
		return isActive() ? "Active" : "Inactive";
	}
}
