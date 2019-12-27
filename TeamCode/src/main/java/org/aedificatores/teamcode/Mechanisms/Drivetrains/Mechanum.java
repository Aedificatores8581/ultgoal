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
    public DcMotor leftFore, leftRear, rightFore, rightRear;

    public Mechanum(HardwareMap map) {
        rightFore = map.dcMotor.get("rf");
        leftFore = map.dcMotor.get("lf");
        leftRear = map.dcMotor.get("la");
        rightRear = map.dcMotor.get("ra");

        rightFore.setDirection(REVERSE);
        rightRear.setDirection(REVERSE);
        leftFore.setDirection(FORWARD);
        leftRear.setDirection(FORWARD);

        leftFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        maxSpeed = 1;

        rightFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public Mechanum(HardwareMap map, String rf, String lf, String lr, String rr) {
        rightFore = map.dcMotor.get(rf);
        leftFore = map.dcMotor.get(lf);
        leftRear = map.dcMotor.get(lr);
        rightRear = map.dcMotor.get(rr);

        rightFore.setDirection(REVERSE);
        rightRear.setDirection(REVERSE);
        leftFore.setDirection(FORWARD);
        leftRear.setDirection(FORWARD);

        leftFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        maxSpeed = 1;

        rightFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public Mechanum(double ratio, DcMotor.ZeroPowerBehavior zeroPowerBehavior, double speed, HardwareMap map) {
        rightFore = map.dcMotor.get("rf");
        leftFore = map.dcMotor.get("lf");
        leftRear = map.dcMotor.get("la");
        rightRear = map.dcMotor.get("ra");

        rightFore.setDirection(REVERSE);
        rightRear.setDirection(REVERSE);
        leftFore.setDirection(FORWARD);
        leftRear.setDirection(FORWARD);

        leftFore.setZeroPowerBehavior(zeroPowerBehavior);
        leftRear.setZeroPowerBehavior(zeroPowerBehavior);
        rightFore.setZeroPowerBehavior(zeroPowerBehavior);
        rightRear.setZeroPowerBehavior(zeroPowerBehavior);
        maxSpeed = speed;

        rightFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    // NOTE: Maybe these shouldn't be in Roman Numerals but idk who cares.
    // So maybe change?
    public void refreshMotors(double rightFore, double leftFore, double leftRear, double rightRear) {
        this.rightFore.setPower(rightFore);
        this.leftFore.setPower(leftFore);
        this.leftRear.setPower(leftRear);
        this.rightRear.setPower(rightRear);
    }

    public void resetMotorEncoders() {
        this.leftFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightFore.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.leftFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.rightFore.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void refreshMotors(double rightFore, double leftFore, double leftRear, double rightRear, double speed) {
        this.rightFore.setPower(speed * rightFore);
        this.leftFore.setPower(speed * leftFore);
        this.leftRear.setPower(speed * leftRear);
        this.rightRear.setPower(speed * rightRear);
    }

    public void refreshMotors() {
        rightFore.setPower(rightForePower * maxSpeed);
        rightRear.setPower(rightAftPower * maxSpeed);
        leftFore.setPower(leftForePower * maxSpeed);
        leftRear.setPower(leftAftPower * maxSpeed);
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

    public double getLeftForeEncoder(){
        return leftFore.getCurrentPosition();
    }

    public double getRightForeEncoder(){
        return rightFore.getCurrentPosition();
    }

    public double getLeftRearEncoder(){
        return leftRear.getCurrentPosition();
    }

    public double getRightRearEncoder(){
        return rightRear.getCurrentPosition();
    }

    public void setVelocityBasedOnGamePad(Vector2 leftStick, Vector2 rightStick) {
        leftForePower = UniversalFunctions.clamp(-1, leftStick.y + leftStick.x + rightStick.x, 1);
        leftAftPower = UniversalFunctions.clamp(-1, leftStick.y - leftStick.x + rightStick.x, 1);
        rightAftPower  = UniversalFunctions.clamp(-1, leftStick.y + leftStick.x - rightStick.x, 1);
        rightForePower = UniversalFunctions.clamp(-1, leftStick.y - leftStick.x - rightStick.x, 1);
    }

    public void setVelocity(double angle, double speed) {
        setVelocity(new Vector2(angle, speed));
    }

    public void setVelocity(Vector2 velocity) {
        leftForePower = UniversalFunctions.clamp(-1, velocity.y + velocity.x, 1);
        leftAftPower = UniversalFunctions.clamp(-1, velocity.y - velocity.x, 1);
        rightAftPower  = UniversalFunctions.clamp(-1, velocity.y + velocity.x, 1);
        rightForePower = UniversalFunctions.clamp(-1, velocity.y - velocity.x, 1);
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