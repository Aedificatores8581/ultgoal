package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake.GandalfIntakeLift;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
public class GandalfIntakePIDTest extends OpMode {
    enum State {MOVING_UP, MOVING_DOWN}

    public static double UP_ANGLE = 100*Math.PI/180.0;
    public static double DOWN_ANGLE = 0*Math.PI/180.0;

    GandalfIntakeLift motor;
    FtcDashboard dashboard;

    GandalfWobbleMotorMoveTest.State state = GandalfWobbleMotorMoveTest.State.MOVING_UP;
    TelemetryPacket packet = new TelemetryPacket();

    @Override
    public void init() {
        motor = new GandalfIntakeLift(hardwareMap, 0.0, GandalfIntakeLift.Mode.AUTO);
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void start() {
        // motor.gotoAngle(UP_ANGLE);
    }

    @Override
    public void loop() {
        switch (state) {
            case MOVING_UP:
                if (!motor.isMoving()) {
                    motor.gotoAngle(DOWN_ANGLE);
                    state = GandalfWobbleMotorMoveTest.State.MOVING_DOWN;
                }
                break;
            case MOVING_DOWN:
                if (!motor.isMoving()) {
                    motor.gotoAngle(UP_ANGLE);
                    state = GandalfWobbleMotorMoveTest.State.MOVING_UP;
                }
                break;
        }
        packet.put("current angle", motor.getCurrentAngleDegrees());
        packet.put("target angle", motor.getTargetAngleDegrees());
        packet.put("current vel", motor.getCurrentAngularVelocityDegrees());
        packet.put("target vel", motor.getTargetAngularVelocityDegrees());
        packet.put("power", motor.getPower());

        dashboard.sendTelemetryPacket(packet);
        motor.update();
        packet.clearLines();
    }
}
