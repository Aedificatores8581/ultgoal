package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBot;
import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.aedificatores.teamcode.Universal.Taemer;

@Config
@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfAutomatedShootingTest extends OpMode {
    GandalfBot bot;
    Taemer loopClock;
    FtcDashboard dashboard;
    public static double SHOOTER_SPEED = 242.6;

    @Override
    public void init() {
        bot = new GandalfBot(hardwareMap, true);
        loopClock = new Taemer();
        bot.drivetrain.stopPackets();
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void loop() {
        if (gamepad1.a && bot.shooterIdle()) {
            bot.shootRings(SHOOTER_SPEED, 3);
        }

        TelemetryPacket packet = new TelemetryPacket();
        telemetry.addLine("Press 'A' to start shooting");

        packet.put("dist", bot.shooter.getDistanceReading());
        telemetry.addData("actual vel", bot.shooter.getCurrentVelocity());
        telemetry.addData("target vel",  bot.shooter.getTargetVelocity());
        telemetry.addData("up to speed", ((bot.shooter.upToSpeed()) ? "yup" : "nope"));
        telemetry.addData("ready to shoot", ((bot.shooter.readyToShoot()) ? "yup" : "nope"));
        telemetry.addData("rings to shoot",  bot.ringsToShoot());
        telemetry.addData("loop time", loopClock.getTimeSec());
        loopClock.resetTime();

        dashboard.sendTelemetryPacket(packet);
        bot.update();
    }
}
