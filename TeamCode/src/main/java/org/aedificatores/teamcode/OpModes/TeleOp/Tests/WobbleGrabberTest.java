package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGrabber;

@TeleOp(name = "WobbleGrabberTest")
public class WobbleGrabberTest extends OpMode {
    WobbleGrabber grabber;

    Gamepad prev;

    @Override
    public void init() {
        prev = new Gamepad();
        grabber = new WobbleGrabber(hardwareMap);
        grabber.init();
    }

    @Override
    public void loop() {
        if (gamepad1.a && !prev.a) {
            grabber.lift();
        }
        if (gamepad1.b && !prev.a) {
            grabber.drop();
        }

        try {
            prev.copy(gamepad1);
        } catch (RobotCoreException e) {
            telemetry.addLine(e.getMessage());
            telemetry.update();
            requestOpModeStop();
        }
        grabber.update();
    }
}
