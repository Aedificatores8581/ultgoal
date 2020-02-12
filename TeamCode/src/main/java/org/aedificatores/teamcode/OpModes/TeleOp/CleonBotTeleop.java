package org.aedificatores.teamcode.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Components.CleonIntake;
import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

@TeleOp(name = "Cleon Teleop")
public class CleonBotTeleop extends OpMode {
	CleonBot robot;
    Vector2 leftStick1, rightStick1, leftStick2;

    final static double SLOW_STRAFE_SPEED = 0.375;
    final static double SLOW_FORWARD_SPEED = 0.3;

    public enum IntakeState{
        INTAKE,
        OUTAKE,
        IDLE
    }

    IntakeState intakeState = IntakeState.IDLE;
    boolean canSwitchIntake = true;

    public enum FoundationState{
        CLOSED,
        OPEN
    }
    FoundationState foundationState = FoundationState.OPEN;
    boolean canSwitchFoundation = true;
    @Override
    public void init() {
        try {
            robot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            telemetry.addLine("Exception Caught: " + e.getMessage());
            telemetry.addLine("\n\nStopping OpMode");
            requestOpModeStop();
        }

        leftStick1 = new Vector2();
        rightStick1 = new Vector2();
        leftStick2 = new Vector2();

        robot.grabber.openGrabber();
        robot.grabber.retract();
    }

    @Override
    public void loop() {
        updateGamepadValues();
        robot.drivetrain.setVelocityBasedOnGamePad(leftStick1, rightStick1);

        switch (intakeState) {
            case IDLE:
                robot.intake.setIntakePower(0);
                if (canSwitchIntake) {
                    if (gamepad1.left_bumper) {
                        intakeState = IntakeState.INTAKE;
                        canSwitchIntake = false;
                    } else if (gamepad1.right_bumper) {
                        intakeState = IntakeState.OUTAKE;
                        canSwitchIntake = false;
                    }
                }
                if (!gamepad1.left_bumper && !gamepad1.right_bumper)
                    canSwitchIntake = true;
                break;
            case INTAKE:
                robot.intake.setIntakePower(0.75);
                if (canSwitchIntake) {
                    if (gamepad1.left_bumper || robot.intake.stoneState == CleonIntake.StoneState.OBTAINED) {
                        intakeState = IntakeState.IDLE;
                        canSwitchIntake = false;
                    } else if (gamepad1.right_bumper) {
                        intakeState = IntakeState.OUTAKE;
                        canSwitchIntake = false;
                    }
                }
                if (!gamepad1.left_bumper && !gamepad1.right_bumper)
                    canSwitchIntake = true;
                break;
            case OUTAKE:
                robot.intake.resetIntakeState();
                robot.intake.setIntakePower(-0.75);
                if (canSwitchIntake) {
                    if (gamepad1.left_bumper) {
                        intakeState = IntakeState.INTAKE;
                        canSwitchIntake = false;
                    } else if (gamepad1.right_bumper) {
                        intakeState = IntakeState.IDLE;
                        canSwitchIntake = false;
                    }
                }
                if (!gamepad1.left_bumper && !gamepad1.right_bumper)
                    canSwitchIntake = true;
                break;
        }


        switch (foundationState){
            case OPEN:
                robot.foundationGrabber.open();
                if(gamepad1.left_stick_button && canSwitchFoundation){
                    canSwitchFoundation = false;
                    foundationState = FoundationState.CLOSED;
                }
                if(!gamepad1.left_stick_button)
                    canSwitchFoundation = true;
                break;
            case CLOSED:
                robot.foundationGrabber.close();
                if(gamepad1.left_stick_button && canSwitchFoundation){
                    canSwitchFoundation = false;
                    foundationState = FoundationState.OPEN;
                }
                if(!gamepad1.left_stick_button)
                    canSwitchFoundation = true;
                break;
        }

        if(gamepad2.left_bumper)
            robot.lift.idle();
        else if (gamepad2.left_stick_button)
            robot.lift.setLiftPower(-1);
        else if(Math.abs(gamepad2.left_stick_y) > 0.1)
            robot.lift.setNormalizedLiftPower(gamepad2.left_stick_y);
        else {
            if (gamepad2.dpad_up)
                robot.lift.snapToStone(robot.lift.closestBlockHeight + 1);
            else if (gamepad2.dpad_down)
                robot.lift.snapToStone(robot.lift.closestBlockHeight - 1);
            else
                robot.lift.snapToStone(robot.lift.closestBlockHeight);
            robot.lift.setPowerUsinngPID();
        }

        if(robot.grabber.extending)
            extendGrabber();
        else
            robot.grabber.retract();
        if(gamepad2.right_bumper){
            if(robot.grabber.isExtended)
                robot.grabber.retract();
            else if(robot.grabber.isRetracted)
                extendGrabber();
        }





        if(gamepad2.left_stick_button)
            robot.grabber.extend();

        if(gamepad2.right_stick_button)
            robot.grabber.retract();





        if(robot.grabber.isExtended){
            extendGrabber();
        }

        if(robot.grabber.isExtended && gamepad1.b)
            robot.grabber.flipGrabber();

        if(gamepad1.y)
            robot.grabber.openGrabber();
        else if(gamepad2.a)
            robot.grabber.closeGrabber();
        if(robot.intake.stoneState == CleonIntake.StoneState.INTAKING)
            robot.grabber.closePusher();


        robot.drivetrain.refreshMotors();
        robot.updateRobotPosition2d();
        robot.updateTimer();

        telemetry.addData("Lift enc", robot.lift.liftMotor1.getCurrentPosition());
    }

    public void updateGamepadValues(){
        leftStick1 = new Vector2(gamepad1.right_stick_x, -gamepad1.left_trigger + gamepad1.right_trigger);
        rightStick1 = new Vector2(gamepad1.left_stick_x, gamepad1.left_stick_y);
        leftStick2 = new Vector2(gamepad1.left_stick_x, gamepad1.left_stick_y);

        leftStick1.x = gamepad1.dpad_left ?  -SLOW_STRAFE_SPEED : leftStick1.x;
        leftStick1.x = gamepad1.dpad_right ?  SLOW_STRAFE_SPEED : leftStick1.x;
        leftStick1.y = gamepad1.dpad_up ?  SLOW_FORWARD_SPEED : leftStick1.y;
        leftStick1.y = gamepad1.dpad_down ?  -SLOW_FORWARD_SPEED : leftStick1.y;
    }

    public void extendGrabber(){
        robot.grabber.closeGrabber();
        robot.grabber.extend();
        robot.intake.resetIntakeState();
        robot.grabber.openPusher();
    }
}
