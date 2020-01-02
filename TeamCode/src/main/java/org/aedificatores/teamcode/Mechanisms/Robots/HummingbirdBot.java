package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.HummingbirdBlockGrabber;
import org.aedificatores.teamcode.Mechanisms.Components.HummingbirdFoundationGrabber;
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

    public Vector2 robotPosition;

    public HummingbirdFoundationGrabber foundationGrabber;
    public HummingbirdBlockGrabber blockGrabber;

    class DrivetrainConfig {
        static final String RF = "front right";
        static final String LF = "front left";
        static final String RR = "rear right";
        static final String RL = "rear left";
    }

    class GrabberConfig {
        static final String PITCH_SERVO = "up down servo";
        static final String ROLL_SERVO = "turn servo";
        static final String GRABBER = "grip servo";
    }

    static final String FOUNDATION_GRAB_CONFIG = "foundation servo";

    public HummingbirdBot(HardwareMap map) {
        // Initialize rev imu
        initIMU(map);
        drivetrain = new Mechanum(map, DrivetrainConfig.RF, DrivetrainConfig.LF, DrivetrainConfig.RL, DrivetrainConfig.RR);
        foundationGrabber = new HummingbirdFoundationGrabber(map, FOUNDATION_GRAB_CONFIG);
        blockGrabber = new HummingbirdBlockGrabber(map, GrabberConfig.PITCH_SERVO, GrabberConfig.ROLL_SERVO, GrabberConfig.GRABBER);
    }




    private void initIMU(HardwareMap map) {
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
    }
}
