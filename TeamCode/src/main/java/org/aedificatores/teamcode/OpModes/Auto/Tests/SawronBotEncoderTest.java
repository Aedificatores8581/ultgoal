package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;

@TeleOp(name = "SawronOdomEncoderTest")
public class SawronBotEncoderTest extends OpMode {
    SawronBot bot;

    @Override
    public void init() {
        bot = new SawronBot(hardwareMap);
        bot.init();
    }

    @Override
    public void loop() {
        bot.updateOdometry();
        telemetry.addData("Bot Position", bot.robotPosition.toString());
        telemetry.addData("Strafe (in)",bot.getStrafeOdomPosition());
        telemetry.addData("Left (in)",bot.getLeftOdomPosition());
        telemetry.addData("Right (in)",bot.getRightOdomPosition());
    }
}
