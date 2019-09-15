package org.aedificatores.teamcode.Mechanisms.Drivetrains;

import org.aedificatores.teamcode.Universal.Math.Pose;
import org.aedificatores.teamcode.Universal.UniversalFunctions;
import org.aedificatores.teamcode.Universal.Math.Vector2;

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
    private static final int TESTING_TELEMETRY_VERBOSITY;

    public enum TurnState {
        ARCADE, FIELD_CENTRIC
    }

    public double turnPower, turnMult;
    public double leftForePower, leftRearPower, leftAftPower, rightAftPower;
    private double angleBetween;
    private Pose pose = new Pose(0, 0, 0);
    public TurnState turnState;
    private static final double FRONT_TO_BACK_POWER_RATIO;
    public DcMotor leftFore, leftRear, rightFore, rightRear;

    public Mecanum(double ratio) {
        FRONT_TO_BACK_POWER_RATIO = ratio; // Where ratio usually equals one.
        leftFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        maxSpeed = 1;
    }

    public Mecanum(double ratio, DcMotor.ZeroPowerBehavior zeroPowerBehavior, double speed) {
        FRONT_TO_BACK_POWER_RATIO = ratio
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
        rightFore.setPower(rightForePow * maxSpeed);
        rightRear.setPower(rightAftPow * maxSpeed);
        leftFore.setPower(leftForePow * maxSpeed);
        leftRear.setPower(leftAftPow * maxSpeed);
    }

    // TODO: Nasty! \
    // Fix this.
    public void setTurn(double power) {
        power *= turnMult;
        turnPow = pow;
    }

    // If you are going to implement Auto and Teleop Loop.
    public void switchTurnState(Vector2 velocity, Vector2 turnVector, Vector2 angle) {
        turnMult = 1 - velocity.magnitude() * (1 - minTurn);
        switch (turnState) {
            case ARCADE:
                setTurn(turnVector.x);
                break;
            case FIELD_CENTRIC:
                setTurn(Math.sin(velocity.angleBetween(angle)));
                break
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

    public void setVelocity(double angle, double speed) {
        rightForePower = getRightForePow(angle, speed);
        leftForePower = getLeftForePower(angle, speed);
        leftAftPower = rightForePower / FRONT_TO_BACK_POWER_RATIO;
        rightAftPower = leftForePower / FRONT_TO_BACK_POWER_RATIO;
        rightForePow *= FRONT_TO_BACK_POWER_RATIO;
        leftForePower *= FRONT_TO_BACK_POWER_RATIO;
    }

    public setVelocity(Vector2 velocity) {
        setVelocity(velocity.angle(), velocity.magnitude());
    }

    public void normalizeMotors() {
        double max = UniversalFunctions.maxAbs(leftForePower, rightForePower, leftAftPower, rightAftPower);
        leftForePower /= max;
        rightForePow /= max;
        rightAftPower /= max;
        leftAftPower /= max;
    }

    public void setTurnPower() {
        leftForePower += turnPower * FRONT_TO_BACK_POWER_RATIO;
        leftAftPower += turnPower / FRONT_TO_BACK_POWER_RATIO;
        rightForePower -= turnPow * FRONT_TO_BACK_POWER_RATIO;
        rightAftPower -= turnPow / FRONT_TO_BACK_POWER_RATIO;
    }

    // NOTE: Teleop Loop and Auto Loop are not in here add them if you want them.
}