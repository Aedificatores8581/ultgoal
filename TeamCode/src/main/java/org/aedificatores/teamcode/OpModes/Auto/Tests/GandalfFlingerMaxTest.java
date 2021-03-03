package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Encoder;
import org.aedificatores.teamcode.Universal.OpModeGroups;
import org.aedificatores.teamcode.Universal.Taemer;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfFlingerMaxTest extends OpMode {
    final int TICKS_PER_REV = 8192;
    final int ACCEL_MAX_HISTORY = 100;

    FtcDashboard dashboard;
    DcMotorEx[] actuator;
    Encoder encoder;

    double maxVel;
    double prevVel = 0;
    double maxAccel;

    double[] pastAccel = new double[ACCEL_MAX_HISTORY];
    int index = 0;

    Taemer timer;

    @Override
    public void init() {
        actuator = new DcMotorEx[2];
        for (int i = 0; i < 2; ++i) {
            actuator[i] = hardwareMap.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[i]);
            actuator[i].setDirection(DcMotorSimple.Direction.REVERSE);
            actuator[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            actuator[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        encoder =  new Encoder(hardwareMap.get(DcMotorEx.class, GandalfBotConfig.SHOOT.FLING[GandalfBotConfig.SHOOT.ODOM_INDEX]));
        encoder.setDirection(Encoder.Direction.REVERSE);

        dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();
        timer = new Taemer();
    }

    @Override
    public void start() {
        timer.resetTime();
        actuator[0].setPower(1.0);
        actuator[1].setPower(1.0);
    }

    @Override
    public void loop() {
        double vel = encoder.getCorrectedVelocity();
        insertToAccelHistory(currentAcceleration());
        double accel = pastAverage();


        telemetry.addData("Enc (ticks)", encoder.getCurrentPosition());
        telemetry.addData("Enc Vel (rad/s)", toRadians(vel));
        telemetry.addData("Enc Accel (rad/s)", toRadians(accel));
        telemetry.addLine("\n");

        if (accel > maxAccel) {
            maxAccel = accel;
        }

        if (vel > maxVel) {
            maxVel = vel;
        }

        telemetry.addData("Max vel (rad/s)", toRadians(maxVel));
        telemetry.addData("Max accel (rad/s^2)", toRadians(maxAccel));
    }

    private double pastAverage() {
        double sum = 0;
        for (int i = 0; i < ACCEL_MAX_HISTORY; ++i) {
            sum += pastAccel[i];
        }
        return sum / ACCEL_MAX_HISTORY;
    }

    private void insertToAccelHistory(double accel) {
        pastAccel[index] = accel;
        index = (index + 1) % ACCEL_MAX_HISTORY;
    }

    private double toRadians(double ticks) {
        return (ticks / TICKS_PER_REV) * 2 * Math.PI;
    }

    double currentAcceleration() {
        double vel = encoder.getCorrectedVelocity();
        double accel = (vel - prevVel) / timer.getTimeSec();
        timer.resetTime();
        prevVel = vel;
        return accel;
    }
}
