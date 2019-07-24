package org.aeficicatores.teamcode.Universal;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

public class GyroAngles {
	public static final AngleUnit ANGLE_UNIT = AngleUnit.DEGREES;

	public AxesOrder axesOrder;
	private double x, y, z;

	public GyroAngles(Orientation orientation) {
		x = AngleUnit.DEGREES.fromUnit(ANGLE_UNIT, orientation.firstAngle);
		y = AngleUnit.DEGREES.fromUnit(ANGLE_UNIT, orientation.secondAngle);
		z = AngleUnit.DEGREES.fromUnit(ANGLE_UNIT, orientation.thirdAngle);
	}

	public void refreshGyroAngles() {
		x = AngleUnit.DEGREES.fromUnit(ANGLE_UNIT, orientation.firstAngle);
		y = AngleUnit.DEGREES.fromUnit(ANGLE_UNIT, orientation.secondAngle);
		z = AngleUnit.DEGREES.fromUnit(ANGLE_UNIT, orientation.thirdAngle);
	}

	public void refreshGyroAnglesInRadians() {
		x = AngleUnit.RADIANS.fromUnit(ANGLE_UNIT, orientation.firstAngle);
		y = AngleUnit.RADIANS.fromUnit(ANGLE_UNIT, orientation.secondAngle);
		z = AngleUnit.RADIANS.fromUnit(ANGLE_UNIT, orientation.thirdAngle);
	}

	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }

	// TODO: toString method.
}
