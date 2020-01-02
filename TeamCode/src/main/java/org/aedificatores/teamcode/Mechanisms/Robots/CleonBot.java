package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.CleonGrabber;
import org.aedificatores.teamcode.Mechanisms.Components.CleonIntake;
import org.aedificatores.teamcode.Mechanisms.Components.CleonLift;
import org.aedificatores.teamcode.Mechanisms.Components.FoundationGrabber;
import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mechanum;
import org.aedificatores.teamcode.Universal.GyroAngles;
import org.aedificatores.teamcode.Universal.JSONAutonGetter;
import org.aedificatores.teamcode.Universal.Math.PIDController;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.json.JSONException;

import java.io.IOException;

/*
* Robot Class for Frank's robot
*
* Creator: Hunter Seachrist
* */
public class CleonBot {
    public Mechanum     drivetrain;

    public BNO055IMU    imu;
    public double       robotAngle;
    public double       prevRobotAngle;
    public double       deltaRobotAngle;
    public GyroAngles   gyroangles;
    public Orientation  angles;
    public double       startAngleZ;
    public double       startAngleY;

    private final double DIST_FORE_WHEEL_FROM_CENTER = 7.553149291;
    private final double DIST_STRAFE_WHEEL_FROM_CENTER = 3.4154453269;

    double prevForeInches;
    double prevStrafeInches;
    public double deltaForeMovementAfterTurn;
    public Vector2 robotPosition;

    // JSON object for getting PID constant values stored on the phone
    JSONAutonGetter pidConstantsJson;

    class PidConstantJSONNames {
        static final String ANGLE_KP = "angleKP";
        static final String ANGLE_KI = "angleKI";
        static final String ANGLE_KD = "angleKD";

        static final String X_POS_KP = "XPosKP";
        static final String X_POS_KI = "XPosKI";
        static final String X_POS_KD = "XPosKD";

        static final String Y_POS_KP = "YPosKP";
        static final String Y_POS_KI = "YPosKI";
        static final String Y_POS_KD = "YPosKD";

        static final String DELTA_TIME = "dt";
    }

    private final String JSON_PID_FILENAME = "CleonBotOrientationPID.json";

    // PID Controllers for variables relating to robot position and orientation
    public PIDController robotAnglePID;
    public PIDController robotXPosPID;
    public PIDController robotYPosPID;

    public CleonIntake intake;
    public CleonGrabber grabber;
    public CleonLift lift;
    public FoundationGrabber foundationGrabber;

    static final String FOUNDATION_GRABBER_NAME = "found1";
    static final double FOUNDATION_GRABBED = 0.3;
    static final double FOUNDATION_RELEASED = 1;

    public CleonBot(HardwareMap map, boolean initJson) throws IOException, JSONException {
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
        robotAngle = 0.0;

        // This huge chunk of code just gets PID constant values from a json file and intitializes
        // their respective PID controllers with them
        if(initJson) {
            pidConstantsJson = new JSONAutonGetter(JSON_PID_FILENAME);

            robotAnglePID = new PIDController(pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.ANGLE_KP),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.ANGLE_KI),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.ANGLE_KD),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DELTA_TIME));

            robotXPosPID = new PIDController(pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.X_POS_KP),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.X_POS_KI),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.X_POS_KD),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DELTA_TIME));

            robotYPosPID = new PIDController(pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.X_POS_KP),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.X_POS_KI),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.X_POS_KD),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DELTA_TIME));
        } else {
            robotAnglePID = new PIDController(0,0,0,0);
            robotXPosPID = new PIDController(0,0,0,0);
            robotYPosPID = new PIDController(0,0,0,0);

        }
        robotPosition = new Vector2();

        setStartAngleZ();
        setRobotAngle();
        prevStrafeInches = 0;
        prevForeInches = 0;
        prevRobotAngle = robotAngle;
        deltaRobotAngle = 0.0;

        deltaForeMovementAfterTurn = 0.0;

        intake = new CleonIntake(map);
        grabber = new CleonGrabber(map);
        lift = new CleonLift(map);
        foundationGrabber = new FoundationGrabber(map, FOUNDATION_GRABBER_NAME, FOUNDATION_GRABBED, FOUNDATION_RELEASED);
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
        robotAngle = Math.toRadians(getGyroAngleZ());
    }

    public void updateRobotPosition() {
        setRobotAngle();
        // gets the change in orientation and position since last opmode update
        double deltaForeMovement = getForeDistanceInches() - prevForeInches;
        double deltaStrafeMovement = getStrafeDistanceInches() - prevStrafeInches;
        deltaRobotAngle = robotAngle;
        deltaRobotAngle -= prevRobotAngle;

        if (deltaRobotAngle > Math.PI) {
            deltaRobotAngle = 2 * Math.PI - deltaRobotAngle;
        }

        // Eliminates any encoder ticks that were caused by turning the robot
        // This makes sense since when the robot turns in place, the amount of ticks in each
        // odometry pod changes, but the position of the robot doesn't change, so these ticks would
        // be negligible
        // This code subtracts the arc length of the turn from the change in enc ticks
        deltaForeMovementAfterTurn = deltaForeMovement - deltaRobotAngle * DIST_FORE_WHEEL_FROM_CENTER;
        //deltaStrafeMovementAfterTurn = deltaStrafeMovement - deltaRobotAngle * DIST_STRAFE_WHEEL_FROM_CENTER;

        // add the change in position to the current position
        robotPosition.y = robotPosition.y + deltaForeMovementAfterTurn * Math.cos(robotAngle);
        robotPosition.x = robotPosition.x + deltaForeMovementAfterTurn * Math.sin(robotAngle);

        updatePrevPosition();
    }

    public void updatePrevPosition() {
        prevForeInches = getForeDistanceInches();
        prevStrafeInches = getStrafeDistanceInches();
        prevRobotAngle = robotAngle;
    }

    public void close() throws IOException {
        pidConstantsJson.close();
    }
}
