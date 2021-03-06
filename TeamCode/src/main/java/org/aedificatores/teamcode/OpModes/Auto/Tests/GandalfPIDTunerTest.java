package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfShooterFlinger;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Config
@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfPIDTunerTest extends OpMode {
    GandalfShooterFlinger flinger;
    public static double SPEED = 500;

    FtcDashboard dashboard;

    @Override
    public void init() {
        flinger = new GandalfShooterFlinger(hardwareMap);

        dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();
    }

    @Override
    public void start() {
        flinger.setSpeed(SPEED);
    }

    @Override
    public void loop() {
        telemetry.addData("actual vel", flinger.getCurrentVelocity());
        telemetry.addData("target vel", flinger.getTargetVelocity());
        telemetry.addData("actual accel", flinger.getCurrentAcceleration());
        telemetry.addData("target accel", flinger.getTargetAcceleration());
        flinger.update();
    }
}
