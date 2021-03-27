package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleMotor;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
@Config
public class GandalfWobbleMotorGravityTest extends OpMode {
    GandalfWobbleMotor motor;
    FtcDashboard dashboard;
    public static double POWER = -.3;
    public static double THRESH = 20;

    @Override
    public void init() {
        motor = new GandalfWobbleMotor(hardwareMap, GandalfWobbleMotor.Mode.TELEOP);
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void init_loop() {
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("feedforward", motor.getFeedforeward());
        dashboard.sendTelemetryPacket(packet);
        motor.update();
    }

    @Override
    public void loop() {
        motor.setPower(POWER + motor.getFeedforeward());
        motor.update();

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("vel", motor.getCurrentAngularVelocityDegrees());
        packet.put("pos", motor.getCurrentAngleDegrees());
        dashboard.sendTelemetryPacket(packet);
        if (motor.getCurrentAngleDegrees() < THRESH) {
            requestOpModeStop();
        }
    }
}
