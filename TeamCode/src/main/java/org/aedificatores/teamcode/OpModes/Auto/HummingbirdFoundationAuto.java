package org.aedificatores.teamcode.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.HummingbirdBot;

@Autonomous(name = "Hummingbird Foundation Auto")
public class HummingbirdFoundationAuto extends OpMode {
    enum AutoState {
        STRAFE_TO_CORNER,
        FORE_TO_FOUNDATION,
        GRAB,
        BACK_TO_WALL,

    }

    HummingbirdBot bot;

    @Override
    public void init() {
        bot = new HummingbirdBot(hardwareMap);
    }

    @Override
    public void loop() {

    }
}
