package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.ShooterSubsystem;

@Autonomous(name = "ShooterSubTest")
public class ShooterSubTest extends OpMode {
    ShooterSubsystem shooter;
    FtcDashboard dashboard;

    Gamepad prev;

    @Override
    public void init() {
        shooter = new ShooterSubsystem(hardwareMap);
        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);
        prev = new Gamepad();
    }

    @Override
    public void start() {
        shooter.runShooter();
    }

    @Override
    public void loop() {
        if (gamepad1.a && !prev.a) {
            shooter.toggleShooter();
        }
        if (gamepad1.b) {
            shooter.advance();
        }

        shooter.update();

        try {
            prev.copy(gamepad1);
        } catch (RobotCoreException e) {
            e.printStackTrace();
        }
    }
}
