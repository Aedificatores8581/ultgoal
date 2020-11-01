package org.aedificatores.teamcode.Mechanisms.Drivetrains;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;

@Config
public class DriveConstants {
    // These are for the GoBuilda 5202-0002-0014 motors on the drivetrain
    public static final double TICKS_PER_REV = 383.6;
    public static final double MAX_RPM = 435;

    /*
     * Set RUN_USING_ENCODER to true to enable built-in hub velocity control using drive encoders.
     * Set this flag to false if drive encoders are not present and an alternative localization
     * method is in use (e.g., tracking wheels).
     *
     * If using the built-in motor velocity PID, update
     * MOTOR_VELO_PID with the tuned coefficients from DriveVelocityPIDTuner.
     * Set the value of MOTOR_VELO_PID to `new PIDCoefficients(kP, kI, kD);`
     */
    public static final boolean RUN_USING_ENCODER = false;
    public static final PIDCoefficients MOTOR_VELO_PID = null;

    /*
     * These are physical constants that can be determined from your robot (including the track
     * width; it will be tune empirically later although a rough estimate is important). Users are
     * free to chose whichever linear distance unit they would like so long as it is consistently
     * used. The default values were selected with inches in mind. Road runner uses radians for
     * angular distances although most angular parameters are wrapped in Math.toRadians() for
     * convenience. Make sure to exclude any gear ratio included in MOTOR_CONFIG from GEAR_RATIO.
     */
    public static double WHEEL_RADIUS = 1.9685; // in
    public static double GEAR_RATIO = 2; // output (wheel) speed / input (motor) speed
    public static double TRACK_WIDTH = 14.5; // in

    /*
     * These are the feedforward parameters used to model the drive motor behavior. If you are using
     * the built-in velocity PID, *these values are fine as is*. However, if you do not have drive
     * motor encoders or have elected not to use them for velocity control, these values should be
     * empirically tuned.
     */
    public static double kV = 0.022575; // 1.0 / rpmToVelocity(MAX_RPM)
    public static double kA = 0.004;
    public static double kStatic = 0;

    /*
     * These values are used to generate the trajectories for you robot. To ensure proper operation,
     * the constraints should never exceed ~80% of the robot's actual capabilities. While Road
     * Runner is designed to enable faster autonomous motion, it is a good idea for testing to start
     * small and gradually increase them later after everything is working. The velocity and
     * acceleration values are required, and the jerk values are optional (setting a jerk of 0.0
     * forces acceleration-limited profiling). All distance units are inches.
     */
    public static DriveConstraints BASE_CONSTRAINTS = new DriveConstraints(
            34.4, 34.4, 0.0,
            Math.toRadians(360), Math.toRadians(360), 0.0
    );


    public static final double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static final double rpmToVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0;
    }

    public static final double getMotorVelocityF() {
        return 32767 * 60.0 / (MAX_RPM * TICKS_PER_REV);
    }
}
