package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mechanum;
import org.aedificatores.teamcode.Mechanisms.OdometryWheels;
import org.aedificatores.teamcode.Universal.Math.Pose;

public class SawronBot {
    // Hardware map constants
    private static final String RF = "Front Right";
    private static final String LF = "Front Left";
    private static final String RR = "Rear Right";
    private static final String LR = "Rear Left";

    // ENC_PER_INCH is wheel circumference / enc ticks per inch
    public final double ENC_PER_INCH = 11.1316365 / 8192;
    public final int FRONT_ENC = 0, RIGHT_ENC = 1, LEFT_ENC = 2;
    public final Pose WHEEL_POSITIONS[] = {new Pose(0, 5.875),
            new Pose(7.25, 0),
            new Pose(-7.25, -1.125)};

    private OdometryWheels odometryWheels;
    Mechanum drivetrain;
    public Pose robotPosition;

    public SawronBot(HardwareMap map) {
        drivetrain = new Mechanum(map, RF, LF, LR, RR);
        odometryWheels = new OdometryWheels(WHEEL_POSITIONS[FRONT_ENC], WHEEL_POSITIONS[RIGHT_ENC],
                WHEEL_POSITIONS[LEFT_ENC]);
        robotPosition = new Pose();
    }

    // TODO: Figure out which motors each encoder corresponds with
    public int getStrafeOdomPosition() {
        return drivetrain.leftFore.getCurrentPosition();
    }

    public int getLeftOdomPosition() {
        return drivetrain.leftFore.getCurrentPosition();
    }

    public int getRightPosition() {
        return drivetrain.leftFore.getCurrentPosition();
    }

    public void updateOdometry() {
        robotPosition = odometryWheels.standardPositionTrack(robotPosition,
                getStrafeOdomPosition(), getLeftOdomPosition(), getRightPosition());
    }
}
