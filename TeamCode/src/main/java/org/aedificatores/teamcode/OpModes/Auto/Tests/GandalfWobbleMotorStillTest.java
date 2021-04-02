package org.aedificatores.teamcode.OpModes.Auto.Tests;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleMotor;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfWobbleMotorStillTest extends OpMode {
    public static final double SERV_UP_CLOSED_POS = .1278;
    public static final double SERV_LO_CLOSED_POS = .052;

    GandalfWobbleMotor motor;
    Servo upper, lower;
    FtcDashboard dashboard;

    @Override
    public void init() {
        motor = new GandalfWobbleMotor(hardwareMap, GandalfWobbleMotor.Mode.TELEOP);
        motor.setWobbleState(GandalfWobbleMotor.WobbleState.NOT_HOLDING_WOBBLE);
        upper = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_UP);
        lower = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_LO);
        dashboard = FtcDashboard.getInstance();

        upper.setPosition(SERV_UP_CLOSED_POS);
        lower.setPosition(SERV_LO_CLOSED_POS);
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        motor.setPower(motor.getFeedforeward());
        motor.update();

        resetStartTime();
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("angle degrees", motor.getCurrentAngleDegrees());
        packet.put("feedforward", motor.getFeedforeward());
        packet.put("power", motor.getPower());
        dashboard.sendTelemetryPacket(packet);

    }
}
