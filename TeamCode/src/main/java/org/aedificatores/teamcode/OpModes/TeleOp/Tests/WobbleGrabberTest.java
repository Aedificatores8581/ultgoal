package org.aedificatores.teamcode.OpModes.TeleOp.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal.WobbleGrabber;

@TeleOp(name = "WobbleGrabberTest")
public class WobbleGrabberTest extends OpMode {
    WobbleGrabber grabber;
    Gamepad prev;
    FtcDashboard dashboard;

    enum Mode {
        TELEOP,
        AUTO,
        PID,
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
        telemetry.addData("position", grabber.getAngleDegrees());
        telemetry.addData("target", grabber.getCurrentTargetAngleDegrees());

        switch (mode) {
            case TELEOP:
                grabber.setPower(gamepad1.left_stick_y);

                if (gamepad1.a && !prev.a) {
                    grabber.openGrabber();
                }

                if (gamepad1.b && !prev.b) {
                    grabber.closeGrabber();
                }

                if (gamepad1.x && !prev.x) {
                    switchMode(Mode.AUTO);
                }
                try {
                    prev.copy(gamepad1);
                } catch (RobotCoreException e) {
                    e.printStackTrace();
                }

                telemetry.addLine("\nYou are currently in TELEOP Mode");
                telemetry.addLine("Use the Left stick to control the grabbers position");
                telemetry.addLine("Press 'A' and 'B' to open and close the grabber");
                telemetry.addLine("\nTo Switch Mode to auto press 'X'");
                break;
            case AUTO:
                if (gamepad1.a && !prev.a) {
                    grabber.lift();
                }

                if (gamepad1.b && !prev.b) {
                    grabber.drop();
                }

                if (gamepad1.x && !prev.x) {
                    switchMode(Mode.TELEOP);
                }
                try {
                    prev.copy(gamepad1);
                } catch (RobotCoreException e) {
                    e.printStackTrace();
                }

                telemetry.addLine("\nYou are currently in AUTO Mode");
                telemetry.addLine("Press 'A' and 'B' to lift and lower the grabber");
                telemetry.addLine("\nTo Switch Mode to teleop press X");
                break;
            case PID:
                break;
            default:

        }

        grabber.update();

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("actual", grabber.getAngleDegrees());
        packet.put("target", grabber.getCurrentTargetAngleDegrees());
        dashboard.sendTelemetryPacket(packet);

    }

    void switchMode(Mode m) {
        grabber.setMode(m == WobbleGrabberTest.Mode.AUTO ? WobbleGrabber.Mode.AUTO : WobbleGrabber.Mode.TELEOP);
        mode = m;
    }
}
