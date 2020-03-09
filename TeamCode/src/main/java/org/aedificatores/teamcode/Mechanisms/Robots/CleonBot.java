package org.aedificatores.teamcode.Mechanisms.Robots;

import android.nfc.Tag;
import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.CleonFoundation;
import org.aedificatores.teamcode.Mechanisms.Components.CleonGrabber;
import org.aedificatores.teamcode.Mechanisms.Components.CleonIntake;
import org.aedificatores.teamcode.Mechanisms.Components.CleonLift;
import org.aedificatores.teamcode.Mechanisms.Components.CleonSideGrabber;
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
private static final String TAG = "CleonBotClass";

    public enum TurnDirection {
        LEFT(1.0), // Turns positive angles
        RIGHT(-1.0); // Turns negative angles
        private double multiplier;
        TurnDirection(double m) {
            multiplier = m;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    public Mechanum     drivetrain;

    public BNO055IMU    imu;
    public double       robotAngle;
    public double       prevRobotAngle;
    public double       deltaRobotAngle;
    public GyroAngles   gyroangles;
    public Orientation  angles;
    public double       startAngleZ;
    public double       startAngleY;

    public final double DIST_FORE_WHEEL_FROM_CENTER = 7.5625;
    public final double DIST_STRAFE_WHEEL_FROM_CENTER = 15.0/16.0; //3.4154453269;
    public final double ENC_PER_INCH = 1440 / (Math.PI * 38 / 25.4);

    double prevForeInches;
    double prevStrafeInches;
    public double deltaForeMovementAfterTurn;
    public double deltaStrafeMovementAfterTurn;
    public Vector2 robotPosition;

    public CleonSideGrabber backSideGrabber;
    public CleonSideGrabber frontSideGrabber;
    interface BackSideGrabberValues {
        String GRAB_MAP_NAME = "autograb";
        String ROTATE_MAP_NAME = "autograbextend";

        double UP_POSITION = .75;
        double DOWN_GRAB_POSITION = .2;
        double DOWN_PUSH_POSITION = .40;
        double HOLD_POSITION = .65;

        double GRABBED_POSITION = .65;
        double RELEASED_POSITION = .45;
    }

    interface FrontSideGrabberValues {
        String GRAB_MAP_NAME = "autograbfront";
        String ROTATE_MAP_NAME = "autograbextendfront";

        double UP_POSITION = .95;
        double DOWN_GRAB_POSITION = .25;
        double DOWN_PUSH_POSITION = .55;
        double HOLD_POSITION = .8;

        double GRABBED_POSITION = .3;
        double RELEASED_POSITION = .5;
    }

    // JSON object for getting PID constant values stored on the phone
    JSONAutonGetter pidConstantsJson;

    interface PidConstantJSONNames {
        String ANGLE_KP = "angleKP";
        String ANGLE_KI = "angleKI";
        String ANGLE_KD = "angleKD";

        String DRIVE_KP = "DriveKP";
        String DRIVE_KI = "DriveKI";
        String DRIVE_KD = "DriveKD";
        String DRIVE_IM = "DriveIM";

        String STRAFE_KP = "StrafeKP";
        String STRAFE_KI = "StrafeKI";
        String STRAFE_KD = "StrafeKD";

        String FORE_KP = "ForeKP";
        String FORE_KI = "ForeKI";
        String FORE_KD = "ForeKD";

        String DELTA_TIME = "dt";
    }

    // Commmonly used vectors for drivePID are stored here
    public interface DriveVecConstants {
        Vector2 FORE            = new Vector2(0.0,-1.0);
        Vector2 BACK            = new Vector2(0.0,1.0);
        Vector2 STRAFE_LEFT     = new Vector2(-1.0,0.0);
        Vector2 STRAFE_RIGHT    = new Vector2(1.0,0.0);
    }

    static final double MIN_FORE_MOTOR_POWER = .40;
    static final double MIN_STRAFE_MOTOR_POWER = .6; //.44
    static final double MIN_TURN_MOTOR_POWER = .20;
    static final double MAX_TURN_MOTOR_POWER = .80;
    static final double ZERO_POWER_THRESH = .01;

    private final String JSON_PID_FILENAME = "CleonBotOrientationPID.json";

    // PID Controllers for variables relating to robot position and orientation
    public PIDController robotAnglePID;
    public PIDController robotPosPID;
    public PIDController robotStrafePID;
    public PIDController robotForePID;

    public CleonIntake intake;
    public CleonGrabber grabber;
    public CleonLift lift;
    public CleonFoundation foundationGrabber;

    long currentTime;
    long resetTime;

    static final String FOUNDATION_GRABBER_NAME = "foundation";
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

            robotPosPID = new PIDController(pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DRIVE_KP),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DRIVE_KI),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DRIVE_KD),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DELTA_TIME),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DRIVE_IM));

            robotStrafePID = new PIDController(pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.STRAFE_KP),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.STRAFE_KI),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.STRAFE_KD),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DELTA_TIME));

            robotForePID = new PIDController(pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.FORE_KP),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.FORE_KI),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.FORE_KD),
                    pidConstantsJson.jsonObject.getDouble(PidConstantJSONNames.DELTA_TIME));
        } else {
            robotAnglePID = new PIDController(0,0,0,0);
            robotPosPID = new PIDController(0,0,0,0);
            robotPosPID.integralMax = 200.0;
            robotForePID = new PIDController(0,0,0,0);

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
        foundationGrabber = new CleonFoundation(map);
        backSideGrabber = new CleonSideGrabber(map, BackSideGrabberValues.GRAB_MAP_NAME,
                BackSideGrabberValues.ROTATE_MAP_NAME,
                BackSideGrabberValues.UP_POSITION,
                BackSideGrabberValues.DOWN_PUSH_POSITION,
                BackSideGrabberValues.DOWN_GRAB_POSITION,
                BackSideGrabberValues.HOLD_POSITION,
                BackSideGrabberValues.GRABBED_POSITION,
                BackSideGrabberValues.RELEASED_POSITION);
        frontSideGrabber = new CleonSideGrabber(map, FrontSideGrabberValues.GRAB_MAP_NAME,
                FrontSideGrabberValues.ROTATE_MAP_NAME,
                FrontSideGrabberValues.UP_POSITION,
                FrontSideGrabberValues.DOWN_PUSH_POSITION,
                FrontSideGrabberValues.DOWN_GRAB_POSITION,
                FrontSideGrabberValues.HOLD_POSITION,
                FrontSideGrabberValues.GRABBED_POSITION,
                FrontSideGrabberValues.RELEASED_POSITION);
        resetTimer();
    }

    public void resetTimer() {
        resetTime = System.currentTimeMillis();
        currentTime = 0;
    }

    public void updateTimer() {
        currentTime = System.currentTimeMillis() - resetTime;
    }

    long getRuntime() {
        return currentTime;
    }

    public double getStrafeDistanceInches(){
        return (drivetrain.getRightRearEncoder() * (.0132335/4));
    }

    public double getLeftForeDistanceInches(){
        return (drivetrain.getRightForeEncoder() * (.0132335/4));
    }

    public double getRightForeDistanceInches(){
        return (drivetrain.getLeftRearEncoder() * (.0132335/4));
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

    public void updateRobotPosition2d() {
        setRobotAngle();
        // gets the change in orientation and position since last opmode update
        double deltaForeMovement = getRightForeDistanceInches() - prevForeInches;
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
        deltaStrafeMovementAfterTurn = deltaStrafeMovement - deltaRobotAngle * DIST_STRAFE_WHEEL_FROM_CENTER;

        //determines the magnitude and angle of the strafe movement
        Vector2 arcVect = new Vector2(deltaStrafeMovementAfterTurn, deltaForeMovementAfterTurn);
        //determines the radius of the turn
        double turnRadius = arcVect.magnitude() / deltaRobotAngle;
        //creates the strafe-angle-relative change in position
        Vector2 deltaPosition = new Vector2(turnRadius * (1 - Math.cos(deltaRobotAngle)), turnRadius * Math.sin(deltaRobotAngle));
        //rotates the change in position to the feild-relative strafe angle
        deltaPosition.rotate(robotAngle-arcVect.angle());

        robotPosition.add(deltaPosition);

        updatePrevPosition();
    }

    public void updateRobotPosition3d() {
        robotAngle = (getRightForeDistanceInches() + getLeftForeDistanceInches()) / (2 * DIST_FORE_WHEEL_FROM_CENTER);
        // gets the change in orientation and position since last opmode update
        double deltaForeMovement = getRightForeDistanceInches() - prevForeInches;
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
        deltaStrafeMovementAfterTurn = deltaStrafeMovement - deltaRobotAngle * DIST_STRAFE_WHEEL_FROM_CENTER;

        //determines the magnitude and angle of the strafe movement
        Vector2 arcVect = new Vector2(deltaStrafeMovementAfterTurn, deltaForeMovementAfterTurn);
        //determines the radius of the turn
        double turnRadius = arcVect.magnitude() / deltaRobotAngle;
        //creates the strafe-angle-relative change in position
        Vector2 deltaPosition = new Vector2(turnRadius * (1 - Math.cos(deltaRobotAngle)), turnRadius * Math.sin(deltaRobotAngle));
        //rotates the change in position to the feild-relative strafe angle
        deltaPosition.rotate(robotAngle-arcVect.angle());

        robotPosition.add(deltaPosition);

        updatePrevPosition();
    }

    /**
     * Commands the robot to turn a certain number of radians. If the robot has met
     * the angle, it stops and returns true
     * @param targetAngle the angle the robot will turn relative to it's starting angle.
     * @param turnDir the direction to turn
     * @return Returns true if the robot has met it's angle
     */
    public boolean turnPID(double targetAngle, TurnDirection turnDir, long timer) {
        robotAnglePID.setpoint = targetAngle;
        robotAnglePID.processVar = robotAngle;
        robotAnglePID.idealLoop();

        if (Math.abs(robotAnglePID.currentOutput) < MIN_TURN_MOTOR_POWER) {
            robotAnglePID.currentOutput = Math.signum(robotAnglePID.currentOutput) * MIN_TURN_MOTOR_POWER;
        }

        robotAnglePID.currentOutput = UniversalFunctions.clamp(-MAX_TURN_MOTOR_POWER, robotAnglePID.currentOutput, MAX_TURN_MOTOR_POWER);

        Vector2 v = new Vector2(robotAnglePID.currentOutput * turnDir.multiplier, 0.0);
        drivetrain.setVelocityBasedOnGamePad(new Vector2(), v);
        updateTimer();

        if (Math.abs(robotAngle) > Math.abs(targetAngle) || Math.abs(drivetrain.leftForePower) < .01 || getRuntime() > timer) {
            drivetrain.setVelocity(new Vector2());
            drivetrain.resetMotorEncoders();
            robotAnglePID.integral = 0;
            return true;
        }

        return false;
    }

    /**
    * Warning: This is an outdated, depricated function that exists here for the sole purpose of running
    * the old foundation autos
    * */
    @Deprecated
    public boolean drivePID(Vector2 velocity, double targetFaceAngle, double inches, long timer) {
        double distance = Math.sqrt(Math.pow(getRightForeDistanceInches(), 2) + Math.pow(getStrafeDistanceInches(), 2));

        robotPosPID.setpoint = inches;
        robotPosPID.processVar = distance;
        robotPosPID.idealLoop();

        Vector2 v = new Vector2(velocity);

        double output;

        // Hacky way to get around the unoptimal PID loop
        // This may seem complicated, because it is. it's really just a total mess and a very bad solution
        // to the problem I was trying to fix
        if (Math.abs(inches) < 17.0) {
            double sigmoidHorizontalShrink = (Math.abs(inches) < 7.0) ? 6 : 4;
            // Uses the sigmoid function, which caps values between 0-1 in a curve-like fashion
            output = 2 * (1 / (1 + Math.pow(Math.E, (-sigmoidHorizontalShrink * robotPosPID.currentOutput)))) - 1;
        } else {
            output = robotPosPID.currentOutput;
        }
        v.scalarMultiply(output);

        // angle pid used to account for error when strafing
        // sometimes the robot will strafe in an arc when we really want it to go straight
        robotAnglePID.setpoint = targetFaceAngle;
        robotAnglePID.processVar = robotAngle;
        robotAnglePID.idealLoop();
        drivetrain.setVelocityBasedOnGamePad(v, new Vector2(-robotAnglePID.error,0));

        if (distance >= Math.abs(inches) || getRuntime() > timer) {
            drivetrain.setVelocity(new Vector2());
            resetTimer();
            drivetrain.resetMotorEncoders();
            return true;
        }

        return false;
    }

    public boolean driveForePID(double inches, double targetFaceAngle) {
        robotForePID.setpoint = inches;
        robotForePID.processVar = getRightForeDistanceInches();
        robotForePID.idealLoop();

        robotAnglePID.setpoint = targetFaceAngle;
        robotAnglePID.processVar = robotAngle;
        robotAnglePID.idealLoop();

        Vector2 velocity;

        if (Math.abs(robotForePID.currentOutput) < MIN_FORE_MOTOR_POWER) {
            velocity = new Vector2(0, Math.signum(-robotForePID.currentOutput) * MIN_FORE_MOTOR_POWER);
        } else {
            velocity = new Vector2(0, -robotForePID.currentOutput);
        }

        Vector2 angleModification = new Vector2(UniversalFunctions.clamp(-1.0,-robotAnglePID.currentOutput, 1.0),0);

        drivetrain.setVelocityBasedOnGamePad(velocity, angleModification);
        if (Math.abs(getRightForeDistanceInches()) >= Math.abs(inches)) {
            drivetrain.setVelocity(new Vector2());
            resetTimer();
            drivetrain.resetMotorEncoders();
            robotForePID.integral = 0;
            robotAnglePID.integral = 0;
            return true;
        }

//        Log.d(TAG,"Velocity: " + velocity.toString());
//        Log.d(TAG,"angle mod: " + angleModification.toString());

        return false;
    }

    public boolean driveStrafePID(double inches, double targetFaceAngle) {
        robotStrafePID.setpoint = inches;
        robotStrafePID.processVar = getStrafeDistanceInches();
        robotStrafePID.idealLoop();
//        Log.d(TAG, "driveStrafePID: begins");
//        Log.d(TAG, "driveStrafePID: setpoint: " + robotStrafePID.setpoint);
//        Log.d(TAG, "driveStrafePID: processVar: " + robotStrafePID.processVar);
//        Log.d(TAG, "driveStrafePID: error: " + robotStrafePID.error);
//        Log.d(TAG, "driveStrafePID: integral: " + robotStrafePID.integral);
//        Log.d(TAG, "driveStrafePID: error after mult: " + robotStrafePID.error * robotStrafePID.KP);
//        Log.d(TAG, "driveStrafePID: integral after mult: " + robotStrafePID.integral * robotStrafePID.KI);
//        Log.d(TAG, "driveStrafePID: current output: " + robotStrafePID.currentOutput);
        robotAnglePID.setpoint = targetFaceAngle;
        robotAnglePID.processVar = robotAngle;
        robotAnglePID.idealLoop();

        Vector2 velocity;

        if (Math.abs(robotStrafePID.currentOutput) < MIN_STRAFE_MOTOR_POWER && Math.abs(robotStrafePID.currentOutput) > ZERO_POWER_THRESH) {
            velocity = new Vector2(Math.signum(robotStrafePID.currentOutput) * MIN_STRAFE_MOTOR_POWER, 0);
//            Log.d(TAG,"driveStrafePID: Condition: less then min strafe power");
        } else {
            velocity = new Vector2(robotStrafePID.currentOutput, 0);
//            Log.d(TAG,"driveStrafePID: Condition: greater than min strafe power");
        }


        Vector2 angleModification = new Vector2(UniversalFunctions.clamp(-1.0,-robotAnglePID.currentOutput, 1.0),0);
        drivetrain.setVelocityBasedOnGamePad(velocity, angleModification);

//        Log.d(TAG,"driveStrafePID: Velocity: " + velocity.toString());
//        Log.d(TAG,"driveStrafePID: angle mod: " + angleModification.toString());

//        Log.d(TAG, "driveStrafePID: strafe dist: " + getStrafeDistanceInches());

        if (Math.abs(getStrafeDistanceInches()) >= Math.abs(inches)) {
            drivetrain.setVelocity(new Vector2());
            resetTimer();
            drivetrain.resetMotorEncoders();
//            Log.d(TAG, "driveStrafePID: reached goal");
            robotStrafePID.integral = 0;
            robotAnglePID.integral = 0;
            return true;
        }
        return false;
    }

    public void driveToPoint2d(Vector2 destination, Vector2 velocity){
        robotAnglePID.setpoint = destination.angle();
        robotAnglePID.processVar = robotAngle;
        robotAnglePID.idealLoop();

        Vector2 turnVel = new Vector2(robotAnglePID.currentOutput, 0.0);

        robotPosPID.setpoint = destination.magnitude() * Math.cos(robotAnglePID.setpoint);
        robotPosPID.processVar = robotAngle;
        robotPosPID.idealLoop();

        Vector2 forwardVel = new Vector2(velocity);
        forwardVel.scalarMultiply(robotPosPID.currentOutput);
        drivetrain.setVelocity(forwardVel);


        drivetrain.setVelocityBasedOnGamePad(forwardVel, turnVel);
    }

    public void updatePrevPosition() {
        prevForeInches = getRightForeDistanceInches();
        prevStrafeInches = getStrafeDistanceInches();
        prevRobotAngle = robotAngle;
    }

    public void close() throws IOException {
        pidConstantsJson.close();
    }
}
