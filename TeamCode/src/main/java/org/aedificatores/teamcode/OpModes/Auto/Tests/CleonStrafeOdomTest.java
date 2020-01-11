package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Robots.CleonBot;
import org.aedificatores.teamcode.Universal.TelemetryLogger;
import org.json.JSONException;

import java.io.IOException;

@Autonomous(name = "Cleon Strafe Odom Test")
public class CleonStrafeOdomTest extends OpMode {
    private CleonBot bot;
    private TelemetryLogger logger;
    private int opmodeTicks = 0;

    @Override
    public void init() {
        try {
            bot = new CleonBot(hardwareMap, true);
        } catch (IOException | JSONException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }

        try {
            logger = new TelemetryLogger();
        } catch (IOException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }
    }

    @Override
    public void start() {
        try {
            logger.writeToLogInCSV("OpTic", "Angle","sOdom","sActual", "fOdom","fActual");
        } catch (IOException e) {
            telemetry.addData("EXCEPTION", e.getMessage());
        }
    }

    @Override
    public void loop() {
        bot.turnPID(Math.PI/2, CleonBot.TurnDirection.LEFT);

        opmodeTicks++;
        double strafeArc = bot.robotAngle * bot.DIST_STRAFE_WHEEL_FROM_CENTER;
        double foreArc = bot.robotAngle * bot.DIST_FORE_WHEEL_FROM_CENTER;
        try {
            logger.writeToLogInCSV(opmodeTicks, bot.robotAngle,bot.getStrafeDistanceInches(),strafeArc,bot.getForeDistanceInches(),foreArc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        telemetry.addLine("angle:\t\t" + bot.getGyroAngleZ());
        telemetry.addLine("strafe Odom:\t" + bot.getStrafeDistanceInches());
        telemetry.addLine("strafe Actual:\t" + strafeArc);

        telemetry.addLine("\nFore Odom:\t\t" + bot.getForeDistanceInches());
        telemetry.addLine("Fore Actual:\t\t" + foreArc);

        telemetry.addLine("\nKP: " + bot.robotAnglePID.KP);
        telemetry.addLine("KI: " + bot.robotAnglePID.KI);
        telemetry.addLine("KD: " + bot.robotAnglePID.KD);

        bot.drivetrain.refreshMotors();
        bot.updateRobotPosition();

    }
}
