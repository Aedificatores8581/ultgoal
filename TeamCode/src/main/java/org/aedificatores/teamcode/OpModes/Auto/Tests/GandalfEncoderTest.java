package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfEncoderTest extends OpMode {
    DcMotorEx left, right, strafe;
    FtcDashboard dashboard;

    @Override
    public void init() {
        left = hardwareMap.get(DcMotorEx.class, GandalfBotConfig.ODOM.LEFT);
        right = hardwareMap.get(DcMotorEx.class, GandalfBotConfig.ODOM.RIGHT);
        strafe = hardwareMap.get(DcMotorEx.class, GandalfBotConfig.ODOM.STRAFE);

        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void loop() {
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("left", left.getCurrentPosition());
        packet.put("right", right.getCurrentPosition());
        packet.put("strafe", strafe.getCurrentPosition());

        dashboard.sendTelemetryPacket(packet);
    }
}
