package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.CleonIntake;

@TeleOp(name = "Cleon Intake Test")
public class CleonIntakeTest extends OpMode {

    private CleonIntake intake;

    enum IntakeMoveState {
        BOTH("Both intake motors running"),
        LEFT("Left Intake motor running"),
        RIGHT("Right Intake motor running");

        private String msg;

        private IntakeMoveState(String msg) {
            this.msg = msg;
        }

        public String getMessage(){
            return msg;
        }
    }

    private IntakeMoveState state;

    Gamepad prev;
    @Override
    public void init() {
        intake = new CleonIntake(hardwareMap);
        telemetry.addLine("intake subsystem initialized");
        state = IntakeMoveState.BOTH;

        prev = new Gamepad();

        try {
            prev.copy(gamepad1);
        } catch (RobotCoreException e) {
            telemetry.addLine(e.getMessage());
            stop();
        }
    }

    @Override
    public void loop() {
        switch (state){
            case BOTH:
                intake.setIntakePower(gamepad1.left_stick_y);
                if(gamepad1.a && !prev.a) state = IntakeMoveState.LEFT;
                break;
            case LEFT:
                intake.setLeftIntake(gamepad1.left_stick_y);
                if(gamepad1.a && !prev.a) state = IntakeMoveState.RIGHT;
                break;
            case RIGHT:
                intake.setRightIntake(gamepad1.left_stick_y);
                if(gamepad1.a && !prev.a) state = IntakeMoveState.BOTH;
                break;
        }

        try {
            prev.copy(gamepad1);
        } catch (RobotCoreException e) {
            e.printStackTrace();
        }

        telemetry.addLine(state.getMessage());
    }
}
