package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.SawronBot;

class SawronBotEncoderTest extends OpMode {
    SawronBot bot;

    @Override
    public void init() {
        bot = new SawronBot(hardwareMap);
    }

    @Override
    public void loop() {
        bot.updateOdometry();
        telemetry.addData("Bot Position", bot.robotPosition.toString());
    }
}
