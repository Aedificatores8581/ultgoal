package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfFlingerEncoderTest extends OpMode {

    FtcDashboard dashboard;
    DcMotorEx mot1, mot2;

    @Override
    public void init() {
        mot1 = hardwareMap.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[0]);
        mot2 = hardwareMap.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[1]);
        dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();
    }

    @Override
    public void loop() {
        telemetry.addData("mot1", mot1.getCurrentPosition());
        telemetry.addData("mot2", mot2.getCurrentPosition());
    }
}
