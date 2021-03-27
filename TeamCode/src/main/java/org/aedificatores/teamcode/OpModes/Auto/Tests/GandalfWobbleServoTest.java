package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfWobbleServoTest extends OpMode {
    Servo upper, lower;

    @Override
    public void init() {
        upper = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_UP);
        lower = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_LO);

    }

    @Override
    public void start() {
        upper.setPosition(.5);
        lower.setPosition(.5);
    }

    @Override
    public void loop() {
        double upperPos = upper.getPosition();
        double lowerPos = lower.getPosition();

        telemetry.addData("upper",upperPos);
        telemetry.addData("lower",lowerPos);

        lowerPos += gamepad1.left_stick_x * .005;
        upperPos += gamepad1.right_stick_x * .005;

        upper.setPosition(upperPos);
        lower.setPosition(lowerPos);

    }
}
