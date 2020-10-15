package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mecanum;
import org.aedificatores.teamcode.Mechanisms.OdometryWheels;
import org.aedificatores.teamcode.Universal.Math.Pose;

public class SawronBot {
    // Hardware map constants
    private static final String RF = "Front Right";
    private static final String LF = "Front Left";
    private static final String RR = "Rear Right";
    private static final String LR = "Rear Left";

    // ENC_PER_INCH is wheel circumference / enc ticks per inch
    public final double INCH_PER_ENC = 11.1316365 / 8192;
    public final int FRONT_ENC = 0, RIGHT_ENC = 1, LEFT_ENC = 2;
    public final Pose WHEEL_POSITIONS[] = {new Pose(0, 5.875, Math.PI/2), // FRONT (Strafe) Encoder
            new Pose(7.25, 0, 0), // Right Encoder
            new Pose(-7.25, -1.125, 0)}; // Left Encoder

    private double prevStrafeWheelPos = 0;
    private double prevLeftWheelPos = 0;
    private double prevRightWheelPos = 0;

    private OdometryWheels odometryWheels;
    Mecanum drivetrain;
    public Pose robotPosition;

    public SawronBot(HardwareMap map) {
        drivetrain = new Mecanum(map);
        odometryWheels = new OdometryWheels(WHEEL_POSITIONS[RIGHT_ENC],
                WHEEL_POSITIONS[LEFT_ENC], WHEEL_POSITIONS[FRONT_ENC], 1/INCH_PER_ENC);
        robotPosition = new Pose();
    }

}
