package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Potentiometer;
import org.aedificatores.teamcode.Universal.DashboardUtil;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfWobbleCOMTest extends OpMode {
    Servo upper, lower;
    Potentiometer potentiometer;
    FtcDashboard dashboard;

    @Override
    public void init() {
        upper = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_UP);
        lower = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_LO);
        potentiometer = new Potentiometer(hardwareMap, GandalfBotConfig.WOBBLE.POTENT);
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void loop() {
        upper.setPosition(.1278);
        lower.setPosition(.052);

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("angle", potentiometer.getAngleNonLinearDegrees());
        dashboard.sendTelemetryPacket(packet);
    }
}
