package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGrabber;

@TeleOp(name = "WobbleGrabberTest")
public class WobbleGrabberTest extends OpMode {
    WobbleGrabber grabber;
    Gamepad prev;
    FtcDashboard dashboard;

    enum Mode {
        TELEOP,
        AUTO,
    }
    Mode mode = WobbleGrabberTest.Mode.TELEOP;

    @Override
    public void init() {
        prev = new Gamepad();
        grabber = new WobbleGrabber(hardwareMap, WobbleGrabber.Mode.TELEOP);

        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void init_loop() {
        if (gamepad1.a) {
            grabber.reset();
        }

        telemetry.addLine("Press \"A\" to reset the grabber (move it in the up and closed position)");
        grabber.update();
    }

    @Override
    public void loop() {
        telemetry.addData("position", grabber.getPosition());
        telemetry.addData("target", grabber.getTargetPosition());

        if (mode == WobbleGrabberTest.Mode.TELEOP) {
            grabber.setPower(gamepad1.left_stick_y);

            if (gamepad1.a && !prev.a) {
                grabber.openGrabber();
            }

            if (gamepad1.a && !prev.a) {
                grabber.closeGrabber();
            }

            if (gamepad1.x && !prev.x) {
                switchMode(WobbleGrabberTest.Mode.AUTO);
            }
            try {
                prev.copy(gamepad1);
            } catch (RobotCoreException e) {
                e.printStackTrace();
            }

            telemetry.addLine("\nYou are currently in TELEOP Mode");
            telemetry.addLine("Use the Left stick to control the grabbers position");
            telemetry.addLine("Press 'A' and 'B' To open and close the grabber");
            telemetry.addLine("\nTo Switch Mode to auto press 'X'");
        } else {
            if (gamepad1.a && !prev.a) {
                grabber.lift();
            }

            if (gamepad1.b && !prev.b) {
                grabber.drop();
            }

            if (gamepad1.x && !prev.x) {
                switchMode(WobbleGrabberTest.Mode.TELEOP);
            }
            try {
                prev.copy(gamepad1);
            } catch (RobotCoreException e) {
                e.printStackTrace();
            }

            telemetry.addLine("\nYou are currently in AUTO Mode");
            telemetry.addLine("Press 'A' and 'B' to lift and lower the grabber");
            telemetry.addLine("\nTo Switch Mode to teleop press 'X'");
        }

        grabber.update();

    }

    void switchMode(Mode m) {
        grabber.setMode(m == WobbleGrabberTest.Mode.AUTO ? WobbleGrabber.Mode.AUTO : WobbleGrabber.Mode.TELEOP);
        mode = m;
    }
}
