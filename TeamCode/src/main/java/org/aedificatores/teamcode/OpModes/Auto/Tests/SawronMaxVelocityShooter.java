package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.aedificatores.teamcode.Mechanisms.Robots.SawronBotConfig;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(name = "SawronMaxVelocityShooter", group = OpModeGroups.SAWRON)
public class SawronMaxVelocityShooter extends OpMode {
    DcMotorEx motor;
    final double TICKS_PER_REV = 28;
    double currentRpm = 0, prevRpm = 0;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, SawronBotConfig.ShootSub.SHOOT_MOT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        motor.setPower(1.0);
        currentRpm = motor.getVelocity() * 60 / (TICKS_PER_REV);
        telemetry.addData("Revolutions/min", currentRpm);
        telemetry.addData("acceleration", (currentRpm - prevRpm) / getRuntime());
        resetStartTime();
        prevRpm = currentRpm;
    }
}
