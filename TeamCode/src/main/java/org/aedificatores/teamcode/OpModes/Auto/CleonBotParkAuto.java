package org.aedificatores.teamcode.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.Math.Vector2;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon Bot Park Auto")
public class CleonBotParkAuto extends OpMode {
    enum Alliance {
        BLUE("Blue\tAlliance"),
        RED("Red\tAlliance");

        String description;

        Alliance(String description) {
            this.description = description;
        }

        public String getDescription(){
            return description;
        }
    }
    enum StartPosition {
        LOADING_AREA("Near\tLoading Area"),
        BUILDING_AREA( "Far\tLoading Area");

        String description;

        StartPosition(String description) {
            this.description = description;
        }

        public String getDescription(){
            return description;
        }
    }



    Alliance alliance;
    StartPosition startPosition;
    CleonBot bot;

    double speed = 0.0;
    final double INCHES_TO_PARK = 12.0;

    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, false);
        } catch (IOException | JSONException e) {
            telemetry.addData("\n\nEXCEPTION",e.getMessage());
        }

        alliance = Alliance.BLUE;
        startPosition = StartPosition.BUILDING_AREA;
    }

    @Override
    public void init_loop() {
        super.init_loop();
        if (gamepad1.a){
            alliance = Alliance.BLUE;
        }
        if (gamepad1.b){
            alliance = Alliance.RED;
        }
        if (gamepad1.x){
            startPosition = StartPosition.BUILDING_AREA;
        }
        if (gamepad1.y){
            startPosition = StartPosition.LOADING_AREA;
        }

        telemetry.addData("Alliance",alliance.getDescription());
        telemetry.addData("Start Position",startPosition.getDescription());
    }

    public void start(){
        switch (alliance){
            case BLUE:
                speed = -.5;
                break;
            case RED:
                speed = .5;
                break;
        }

        if (startPosition == StartPosition.BUILDING_AREA){
            speed = speed * -1.0;
        }

        resetStartTime();
    }

    @Override
    public void loop() {
        if (getRuntime() > 29.0) {
            if (Math.abs(bot.getStrafeDistanceInches()) < INCHES_TO_PARK) {
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(speed, 0.0), new Vector2());
            } else {
                bot.drivetrain.setVelocityBasedOnGamePad(new Vector2(0.0, 0.0), new Vector2(0.0, 0.0));
                bot.drivetrain.refreshMotors();
            }
        }
        bot.drivetrain.refreshMotors();
    }

    @Override
    public void stop(){

    }
}
