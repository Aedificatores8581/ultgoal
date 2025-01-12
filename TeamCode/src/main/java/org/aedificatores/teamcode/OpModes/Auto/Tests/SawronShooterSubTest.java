package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.SawronShooterSubsystem;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(name = "SawronShooterSubTest", group = OpModeGroups.SAWRON)
public class SawronShooterSubTest extends OpMode {
    SawronShooterSubsystem shooter;
    FtcDashboard dashboard;
    DcMotorEx motor;

    Gamepad prev;

    @Override
    public void init() {
        shooter = new SawronShooterSubsystem(hardwareMap);
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

        if (gamepad1.b) {
            shooter.advance();
        }
        if (gamepad1.a && !prev.a) {
            shooter.toggleShooter();
        }
        try {
            prev.copy(gamepad1);
        } catch (RobotCoreException e) {
            e.printStackTrace();
        }

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("target", shooter.getTargetShooterVelocity());
        packet.put("actual", shooter.getActualShooterVelocity());
        dashboard.sendTelemetryPacket(packet);
        shooter.update();
    }
}
