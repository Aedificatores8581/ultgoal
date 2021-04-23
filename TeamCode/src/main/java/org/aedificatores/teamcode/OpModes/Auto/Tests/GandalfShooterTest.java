package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfShooterFlinger;
import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBot;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Config
@TeleOp(group = OpModeGroups.GANDALF)
public class GandalfShooterTest extends OpMode {
    GandalfBot bot;
    Gamepad prev1;
    public static double SPEED = 250;

    FtcDashboard dashboard;

    String log = "";

    @Override
    public void init() {
        bot = new GandalfBot(hardwareMap, true);
        bot.drivetrain.stopPackets();

        dashboard = FtcDashboard.getInstance();
        prev1 = new Gamepad();
    }

    @Override
    public void start() {
        bot.shooter.setSpeed(SPEED);
        bot.wobbleGrabber.setMode(GandalfWobbleGrabber.Mode.AUTO);
        bot.intake.actuator.setPower(.8);
        bot.intake.lift.gotoAngle(Math.toRadians(5));
    }

    @Override
    public void loop() {
        if (gamepad1.a && bot.shooter.upToSpeed()) {
            bot.intake.transfer.setPower(.75);
        } else if (gamepad1.b) {
            bot.intake.transfer.setPower(0.0);
        }

        SPEED += gamepad1.left_stick_y;

        log =   "SHOOTER_SPEED: " + SPEED + "\n" +
                "Use left joystick to adjust the speed\nPress 'x' to set the shooter to that speed\n" +
                "Press 'a'/'b' to turn the transfer on/off\n" +
                "----------------------------------------\n" +
                "actual vel: " + bot.shooter.getCurrentVelocity() + "\n" +
                "target vel: " + bot.shooter.getTargetVelocity() + "\n" +
                "up to speed: " + ((bot.shooter.upToSpeed()) ? "yup\n" : "nope\n");

        telemetry.addLine(log);
        TelemetryPacket packet = new TelemetryPacket();
        packet.addLine(log);
        dashboard.sendTelemetryPacket(packet);
        bot.update();

        if (gamepad1.x && !prev1.x) {
            bot.shooter.setSpeed(SPEED);
        }

        try {
            prev1.copy(gamepad1);
        } catch (RobotCoreException e) {
            telemetry.addLine("EXCEPTION: " + e.getMessage());
        }
    }
}
