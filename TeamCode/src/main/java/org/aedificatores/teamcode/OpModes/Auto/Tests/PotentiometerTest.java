package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogSensor;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Potentiometer;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class PotentiometerTest extends OpMode {
    Potentiometer potentiometer;
    FtcDashboard dashboard;

    @Override
    public void init() {
        potentiometer = new Potentiometer(hardwareMap, GandalfBotConfig.WOBBLE.POTENT);
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void loop() {
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("voltage", potentiometer.getRawVoltage());
        packet.put("angle", potentiometer.getAngleDegrees());
        packet.put("rad", potentiometer.getAngleRadians());
        packet.put("angle (nl)", potentiometer.getAngleNonLinearDegrees());
        packet.put("rad (nl)", potentiometer.getAngleNonLinearRadians());
        dashboard.sendTelemetryPacket(packet);
    }
}
