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
    public static double SHOOTER_SPEED = 233;

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
        telemetry.addLine("actual vel: " + bot.shooter.getCurrentVelocity() + "\n" +
                "target vel: " + bot.shooter.getTargetVelocity() + "\n" +
                "up to speed: " + ((bot.shooter.upToSpeed()) ? "yup\n" : "nope\n") +
                "loop time: " + loopClock.getTimeSec() + "\n");
        loopClock.resetTime();

        dashboard.sendTelemetryPacket(packet);
        bot.update();
    }
}
