package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleGrabber;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfWobbleGrabberTest extends OpMode {
    GandalfWobbleGrabber grabber;

    @Override
    public void init() {
        grabber = new GandalfWobbleGrabber(hardwareMap, GandalfWobbleGrabber.Mode.AUTO);
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            grabber.lift();
        } else if (gamepad1.b) {
            grabber.drop();
        }
        grabber.update();
    }
}
