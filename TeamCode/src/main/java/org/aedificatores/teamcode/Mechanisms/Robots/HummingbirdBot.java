package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mechanum;
import org.aedificatores.teamcode.Universal.GyroAngles;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class HummingbirdBot {
    public Mechanum drivetrain;

    public BNO055IMU imu;
    public Vector2 robotAngle;
    public Vector2      prevRobotAngle;
    public GyroAngles gyroangles;
    public Orientation angles;
    public double       startAngleZ;
    public double       startAngleY;

    double prevForeInches;
    double prevStrafeInches;
    public Vector2 robotPosition;

    class DrivetrainConfig {
        static final String RF = "front right";
        static final String LF = "front left";
        static final String RR = "rear right";
        static final String RL = "rear left";
    }

    public HummingbirdBot(HardwareMap map) {
        // Initialize rev imu
        drivetrain = new Mechanum(map);
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = map.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, GyroAngles.ORDER, GyroAngles.UNIT);

        gyroangles = new GyroAngles(angles);
        robotAngle = new Vector2();

        drivetrain = new Mechanum(map, DrivetrainConfig.RF, DrivetrainConfig.LF, DrivetrainConfig.RL, DrivetrainConfig.RR);
    }

}
