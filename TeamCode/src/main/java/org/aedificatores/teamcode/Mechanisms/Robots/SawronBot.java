package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.robotcore.hardware.DcMotor;
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

    private double prevStrafeWheelPos = 0;
    private double prevLeftWheelPos = 0;
    private double prevRightWheelPos = 0;

    private OdometryWheels odometryWheels;
    Mechanum drivetrain;
    public Pose robotPosition;

    public SawronBot(HardwareMap map) {
        drivetrain = new Mechanum(map, RF, LF, LR, RR);
        odometryWheels = new OdometryWheels(WHEEL_POSITIONS[RIGHT_ENC],
                WHEEL_POSITIONS[LEFT_ENC], WHEEL_POSITIONS[FRONT_ENC], ENC_PER_INCH);
        robotPosition = new Pose();
    }

    // You may be wondering why we have an init method and a SawronBot constructor.
    // The reasoning is the constructor concerns only actions relating to the construction of
    // a SawronBot object (i.e. setting robot position to zero).
    //
    // The init method concerns mostly with initializing the hardware of the robot (i.e. setting the
    // starting positions for certain servos, setting motor modes, etc.).
    // Maybe we should have a better name than 'init'

    // TODO: More descriptive name than "init?"
    public void init() {
        drivetrain.leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.leftFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        drivetrain.rightFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    public double getStrafeOdomPosition() {
        return drivetrain.rightFore.getCurrentPosition() * ENC_PER_INCH;
    }

    public double getLeftOdomPosition() {
        return drivetrain.leftFore.getCurrentPosition() * ENC_PER_INCH;
    }

    public double getRightOdomPosition() {
        return drivetrain.leftRear.getCurrentPosition() * ENC_PER_INCH;
    }

    public void updateOdometry() {
        robotPosition = odometryWheels.standardPositionTrack(robotPosition,
                getStrafeOdomPosition() - prevStrafeWheelPos,
                getLeftOdomPosition() - prevLeftWheelPos,
                getRightOdomPosition() - prevRightWheelPos);
        prevStrafeWheelPos = getStrafeOdomPosition();
        prevLeftWheelPos = getLeftOdomPosition();
        prevRightWheelPos = getRightOdomPosition();
    }
}
