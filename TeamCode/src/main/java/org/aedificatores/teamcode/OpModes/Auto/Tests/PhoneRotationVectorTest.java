package org.aedificatores.teamcode.OpModes.Auto.Tests;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcontroller.internal.ApplicationContext;

@Disabled
@Autonomous(name = "Phone Rotation Vector Test")
public class PhoneRotationVectorTest extends OpMode {
    SensorManager manager;
    Sensor rotationalVectorSensor;
    @Override
    public void init() {
        manager = (SensorManager) ApplicationContext.getContext().getSystemService(Context.SENSOR_SERVICE);
        rotationalVectorSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void loop() {
        telemetry.addData("To string",rotationalVectorSensor.toString());
    }
}
