package org.aedificatores.teamcode.Mechanisms.Sensors;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Universal.Taemer;

public class Potentiometer {
    private static final int RUNNING_LENGTH = 10;
    AnalogInput sensor;
    final double DEGREES_PER_VOLT = 270.0/3.3;
    private double prevAngle = 0.0;
    private double velocity = 0.0;
    // Due to some wierdness with how the potentiometer reports position
    // we take the running average for velocity
    private double[] velRunAvgQueue = new double[RUNNING_LENGTH];
    private int queueIndex = 0;
    private Taemer timer;

    public Potentiometer(HardwareMap map, String name) {
        sensor = map.analogInput.get(name);
        timer = new Taemer();
        timer.resetTime();
        for (int i = 0; i < RUNNING_LENGTH; ++i) velRunAvgQueue[i] = 0; // yes i am stupid
    }

    public double getRawVoltage() {
        return sensor.getVoltage();
    }

    public double getAngleDegrees() {
        return sensor.getVoltage() * DEGREES_PER_VOLT;
    }

    // Equation is based on this documentation: https://docs.revrobotics.com/potentiometer/untitled-1
    // Supposedly, the potentiometer is linear, but the rev control hub analog circuitry screws it up
    //
    // It is the inverse of the function that returns the voltage with a given angle
    // I used an online inverse calculator to find it. I have no idea how this works
    // :P
    public double getAngleNonLinearDegrees() {
        double v = sensor.getVoltage();
        return 270.0 - (540*v + 891 - Math.sqrt(871600*v*v - 962280*v + 793881)) / (4*v);
    }

    public double getAngleRadians() {
        return getAngleDegrees() * Math.PI / 180.0;
    }

    public double getAngleNonLinearRadians() {
        return getAngleNonLinearDegrees() * Math.PI / 180.0;
    }

    public double getVelocityDegrees() {
        return velocity;
    }

    public double getVelocityRadians() {
        return velocity * Math.PI / 180.0;
    }

    void resetClock() {
        timer.resetTime();
    }

    public void update() {

        velRunAvgQueue[queueIndex] = (getAngleNonLinearDegrees() - prevAngle) / timer.getTimeSec();
        queueIndex = (queueIndex + 1) % RUNNING_LENGTH;
        double sum = 0;
        for (double i : velRunAvgQueue) {
            sum += i;
        }
        velocity = sum / RUNNING_LENGTH;
        prevAngle = getAngleNonLinearDegrees();
        timer.resetTime();
    }
}
