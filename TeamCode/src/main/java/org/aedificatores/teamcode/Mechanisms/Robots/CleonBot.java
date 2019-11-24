package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mechanum;
import org.aedificatores.teamcode.Universal.GyroAngles;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/*
* Robot Class for Frank's robot
*
* Creator: Hunter Seachrist
* */
public class CleonBot {
    public Mechanum     drivetrain;

    public BNO055IMU    imu;
    public Vector2      robotAngle;
    public GyroAngles   gyroangles;
    public Orientation  angles;
    public double       startAngleZ;
    public double       startAngleY;

    public CleonBot(HardwareMap map){
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

        setStartAngleZ();
        setRobotAngle();
    }

    public double getStrafeDistanceInches(){
        return (drivetrain.getRightRearEncoder() * (.0132335/4));
    }

    public double getForeDistanceInches(){
        return (drivetrain.getRightForeEncoder() * (.0132335/4));
    }

    public double getGyroAngleX(){
        return gyroangles.refreshGyroAnglesX(imu.getAngularOrientation(AxesReference.INTRINSIC, GyroAngles.ORDER, GyroAngles.UNIT));
    }

    public double getGyroAngleY(){
        return gyroangles.refreshGyroAnglesY(imu.getAngularOrientation(AxesReference.INTRINSIC, GyroAngles.ORDER, GyroAngles.UNIT));
    }

    public double getGyroAngleZ(){
        return gyroangles.refreshGyroAnglesZ(imu.getAngularOrientation(AxesReference.INTRINSIC, GyroAngles.ORDER, GyroAngles.UNIT));
    }

    //Sets the start angle of the robot
    public void setStartAngleZ(){
        startAngleZ = getGyroAngleZ();
    }

    //Normalizes the gyro measure
    public double normalizeGyroAngleZ(){
        return UniversalFunctions.normalizeAngleDegrees(getGyroAngleZ(), startAngleZ);
    }

    public double normalizeGyroAngleY(){
        return UniversalFunctions.normalizeAngleDegrees(getGyroAngleY(), startAngleY);
    }

    //Sets the angle value of robotAngle
    public void setRobotAngle(){
        robotAngle.setFromPolar(1, Math.toRadians(normalizeGyroAngleZ()));
    }
}
