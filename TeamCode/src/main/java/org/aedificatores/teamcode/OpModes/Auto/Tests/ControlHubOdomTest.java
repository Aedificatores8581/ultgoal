package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "odom test control hub")
public class ControlHubOdomTest extends OpMode {
    DcMotor odom;

    @Override
    public void init() {
        odom = hardwareMap.dcMotor.get("test");
    }

    @Override
    public void loop() {
        telemetry.addData("Encoder", odom.getCurrentPosition());
    }
}
