package org.aedificatores.teamcode.Mechanisms.Drivetrains;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Universal.Math.Pose;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.aedificatores.teamcode.Universal.Math.Vector2;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

/**
 * This is designed for any four-wheeled Mechanum Drivetrain.
 *
 * Created by Frank Portman on 5/21/2018
 * Revised by Theodore Lovinski 15/9/2019
 */

public class Mechanum {
    // TODO: This combined last year's code into a single class, please test to see if this works.
    // I beg you.

    private static final boolean ENABLE_TESTING_TELEMETRY = false;
    private static final int TESTING_TELEMETRY_VERBOSITY = 0;

    public enum TurnState {
        ARCADE, FIELD_CENTRIC
    }

    private double turnPower, turnMult;
    private double maxSpeed;
    public double leftForePower, rightForePower, leftAftPower, rightAftPower;
    private double angleBetween;
    private Pose pose = new Pose(0, 0, 0);
    private TurnState turnState;
    private static final double FRONT_TO_BACK_POWER_RATIO = 1;
    private DcMotor leftFore, leftRear, rightFore, rightRear;

    public Mechanum() {
        leftFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        maxSpeed = 1;
    }

    public Mechanum(double ratio, DcMotor.ZeroPowerBehavior zeroPowerBehavior, double speed) {
        leftFore.setZeroPowerBehavior(zeroPowerBehavior);
        leftRear.setZeroPowerBehavior(zeroPowerBehavior);
        rightFore.setZeroPowerBehavior(zeroPowerBehavior);
        rightRear.setZeroPowerBehavior(zeroPowerBehavior);
        maxSpeed = speed;
    }

    public void initMotors(HardwareMap map) {
        rightFore = map.dcMotor.get("rf");
        leftFore = map.dcMotor.get("lf");
        leftRear = map.dcMotor.get("la");
        rightRear = map.dcMotor.get("ra");

        rightFore.setDirection(REVERSE);
        rightRear.setDirection(REVERSE);
        leftFore.setDirection(FORWARD);
        leftRear.setDirection(FORWARD);
    }

    // NOTE: Maybe these shouldn't be in Roman Numerals but idk who cares.
    // So maybe change?
    public void refreshMotors(double I, double II, double III, double IV) {
        rightFore.setPower(I);
        leftFore.setPower(II);
        leftRear.setPower(III);
        rightRear.setPower(IV);
    }

    public void refreshMotors(double I, double II, double III, double IV, double speed) {
        rightFore.setPower(speed * I);
        leftFore.setPower(speed * II);
        leftRear.setPower(speed * III);
        rightRear.setPower(speed * IV);
    }

    public void refreshMotors() {
        rightFore.setPower(rightForePower * maxSpeed);
        rightRear.setPower(rightAftPower * maxSpeed);
    }

   // setTurn(double power) turnPower = power * turnMult;

    // If you are going to implement Auto and Teleop Loop.
    public void switchTurnState(Vector2 velocity, Vector2 turnVector, Vector2 angle) {
        //turnMult = 1 - velocity.magnitude() * (1 - minTurn);
        switch (turnState) {
            case ARCADE:
                turnPower = turnVector.x * turnMult;
                break;
            case FIELD_CENTRIC:
                turnPower = Math.sin(velocity.angleBetween(angle)) * turnMult;
                break;
            default:
                turnState = TurnState.ARCADE;
                break;
        }
    }

    public double getRightForePower(double angle, double speed) {
        return Math.sin(angle + Math.PI / 4) * FRONT_TO_BACK_POWER_RATIO * speed / 2;
    }

    // Essentially just an overload that doesn't break vector into its parts.
    public double getRightForePower(Vector2 velocity) {
        return getRightForePower(velocity.angle(), velocity.magnitude());
    }

    public double getLeftForePower(double angle, double speed) {
        return Math.cos(angle + Math.PI / 4) * FRONT_TO_BACK_POWER_RATIO * speed / 2;
    }

    public double getLeftForePower(Vector2 velocity) {
        return getLeftForePower(velocity.angle(), velocity.magnitude());
    }

    private void setVelocity(double angle, double speed) {
        rightForePower = getRightForePower(angle, speed);
        leftForePower = getLeftForePower(angle, speed);
        leftAftPower = rightForePower / FRONT_TO_BACK_POWER_RATIO;
        rightAftPower = leftForePower / FRONT_TO_BACK_POWER_RATIO;
        rightForePower *= FRONT_TO_BACK_POWER_RATIO;
        leftForePower *= FRONT_TO_BACK_POWER_RATIO;
    }

    public void setVelocity(Vector2 velocity) {
        setVelocity(velocity.angle(), velocity.magnitude());
    }

    public void normalizeMotors() {
        double max = UniversalFunctions.maxAbs(leftForePower, rightForePower, leftAftPower, rightAftPower);
        leftForePower /= max;
        rightForePower /= max;
        rightAftPower /= max;
        leftAftPower /= max;
    }

    public void setTurnPower() {
        leftForePower += turnPower * FRONT_TO_BACK_POWER_RATIO;
        leftAftPower += turnPower / FRONT_TO_BACK_POWER_RATIO;
        rightForePower -= turnPower * FRONT_TO_BACK_POWER_RATIO;
        rightAftPower -= turnPower / FRONT_TO_BACK_POWER_RATIO;
    }

    // NOTE: Teleop Loop and Auto Loop are not in here add them if you want them.
}