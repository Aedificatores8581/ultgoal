package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mechanum;
import org.aedificatores.teamcode.Universal.Math.Vector2;

@TeleOp(name = "Mechanum Tele Test")
public class MechanumTeleTest extends OpMode {
    private Mechanum drivetrain;
    double speedMult = .3;


    @Override
    public void init() {
        drivetrain = new Mechanum(hardwareMap);
    }

    @Override
    public void loop() {
        if(gamepad1.left_bumper || gamepad1.right_bumper)
            speedMult = 1;
        else
            speedMult = .3;
        drivetrain.setVelocityBasedOnGamePad(new Vector2(gamepad1.left_stick_x,gamepad1.left_stick_y)
                ,                            new Vector2(gamepad1.right_stick_x,gamepad1.right_stick_y));
        drivetrain.refreshMotors();

        // TODO: Note that Franks drivetrain has some reversed motors or whatever, so the following code only works on his drivetrain
//        drivetrain.leftForePower = -gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x;
//        drivetrain.leftAftPower = gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x;
//        drivetrain.rightForePower = gamepad1.left_stick_y + gamepad1.left_stick_x -gamepad1.right_stick_x;
//        drivetrain.rightAftPower = gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x;
//        drivetrain.refreshMotors();
    }
}
