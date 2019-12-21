package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Cleon Motor Test")
public class CleonMotorTest extends OpMode {
    DcMotor leftFore;
    DcMotor rightFore;
    DcMotor leftRear;
    DcMotor rightRear;

    @Override
    public void init() {
        leftFore = hardwareMap.dcMotor.get("lf");
        rightFore = hardwareMap.dcMotor.get("rf");
        leftRear = hardwareMap.dcMotor.get("la");
        rightRear = hardwareMap.dcMotor.get("ra");
    }

    @Override
    public void loop() {
        leftFore.setPower(gamepad1.left_stick_y);
        rightFore.setPower(gamepad1.left_stick_y);
        leftRear.setPower(gamepad1.left_stick_y);
        rightRear.setPower(gamepad1.left_stick_y);

        telemetry.addData("Left Fore Pow", leftFore.getPower());
        telemetry.addData("Right Fore Pow", rightFore.getPower());
        telemetry.addData("Left Rear Pow", leftRear.getPower());
        telemetry.addData("Right Rear Pow", rightRear.getPower());
    }
}
