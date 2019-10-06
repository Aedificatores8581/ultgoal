package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Drivetrains.Mechanum;
import org.aedificatores.teamcode.Universal.Math.Vector2;

@TeleOp(name = "Mechanum Tele Test")
public class MechanumTeleTest extends OpMode {
    private Mechanum drivetrain;

    double x, y;


    @Override
    public void init() {
        drivetrain = new Mechanum(hardwareMap);
    }

    @Override
    public void loop() {
        x = .3 * gamepad1.left_stick_x;
        y = .3 * gamepad1.left_stick_y;
        drivetrain.setVelocity(new Vector2(x,y));
        drivetrain.refreshMotors();
    }
}
