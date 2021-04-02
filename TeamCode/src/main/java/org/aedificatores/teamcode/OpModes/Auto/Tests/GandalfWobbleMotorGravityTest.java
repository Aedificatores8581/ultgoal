package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleMotor;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
@Config
public class GandalfWobbleMotorGravityTest extends OpMode {
    public static final double SERV_UP_CLOSED_POS = .1278;
    public static final double SERV_LO_CLOSED_POS = .052;

    GandalfWobbleMotor motor;
    FtcDashboard dashboard;
    Servo upper, lower;
    public static double POWER = -.3;
    public static double THRESH = 20;

    @Override
    public void init() {
        motor = new GandalfWobbleMotor(hardwareMap, GandalfWobbleMotor.Mode.TELEOP);
        motor.setWobbleState(GandalfWobbleMotor.WobbleState.HOLDING_WOBBLE);
        upper = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_UP);
        lower = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_LO);
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void init_loop() {
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("feedforward", motor.getFeedforeward());
        dashboard.sendTelemetryPacket(packet);
        motor.update();
        upper.setPosition(SERV_UP_CLOSED_POS);
        lower.setPosition(SERV_LO_CLOSED_POS);
    }

    @Override
    public void loop() {
        motor.setPower(POWER + motor.getFeedforeward());
        motor.update();

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("vel", motor.getCurrentAngularVelocityDegrees());
        packet.put("pos", motor.getCurrentAngleDegrees());
        packet.put("power", motor.getPower());
        dashboard.sendTelemetryPacket(packet);
        if (motor.getCurrentAngleDegrees() < THRESH) {
            requestOpModeStop();
        }
    }
}
