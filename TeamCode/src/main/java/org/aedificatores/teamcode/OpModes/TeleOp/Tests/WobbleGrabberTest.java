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
    public void init_loop() {
        if (gamepad1.a) {
            grabber.setUp(true);
        }
        if (gamepad1.b) {
            grabber.setUp(false);
        }

        telemetry.addLine("Press \"A\" to tell the grabber that it is in the up position");
        telemetry.addLine("Press \"B\" to tell the grabber that it is in the down position");
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

        telemetry.addLine("Press \"A\" to lift the wobble goal");
        telemetry.addLine("Press \"B\" to drop the wobble goal");
    }
}
