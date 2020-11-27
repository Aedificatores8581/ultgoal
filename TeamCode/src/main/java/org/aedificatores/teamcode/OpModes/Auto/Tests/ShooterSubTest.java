package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Components.ShooterSubsystem;

@Autonomous(name = "ShooterSubTest")
public class ShooterSubTest extends OpMode {
    ShooterSubsystem shooter;
    FtcDashboard dashboard;

    @Override
    public void init() {
        shooter = new ShooterSubsystem(hardwareMap);
        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);
    }

    @Override
    public void start() {
        shooter.runShooter();
    }

    @Override
    public void loop() {
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("target vel", shooter.getTargetShooterVelocity());
        packet.put("actual vel", shooter.getActualShooterVelocity());
        dashboard.sendTelemetryPacket(packet);
        shooter.update();

    }
}
