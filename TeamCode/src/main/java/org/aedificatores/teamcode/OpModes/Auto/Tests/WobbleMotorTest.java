package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.aedificatores.teamcode.Mechanisms.Components.WobbleGoal.WobbleMotor;


@Autonomous(name = "WobbleMotorTest")
public class WobbleMotorTest extends OpMode {
    enum State {
        DOWN,
        UP,
    }

    WobbleMotor motor;
    State state = State.DOWN;
    FtcDashboard dashboard;

    private final double ANGLE_UP = 0;
    private final double ANGLE_DOWN = -Math.PI;


    @Override
    public void init() {
        motor = new WobbleMotor(hardwareMap, WobbleMotor.Mode.TELEOP);
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void init_loop() {
        motor.setPower(gamepad1.left_stick_y);
        motor.update();
    }

    @Override
    public void start() {
        motor.setMode(WobbleMotor.Mode.AUTO);
        motor.resetEncoders();
        motor.gotoAngle(ANGLE_DOWN);
    }

    @Override
    public void loop() {
        switch (state) {
            case DOWN:
                if (!motor.isBusy()) {
                    motor.gotoAngle(ANGLE_UP);
                    state = State.UP;
                }
                break;

            case UP:
                if (!motor.isBusy()) {
                    motor.gotoAngle(ANGLE_DOWN);
                    state = State.DOWN;
                }
                break;
        }

        motor.update();

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("target x", motor.getCurrentTargetAngle() * 180 / Math.PI);
        packet.put("actual x", motor.getCurrentAngle() * 180 / Math.PI);
        packet.put("target vel", motor.getCurrentTargetState().getV() * 180 / Math.PI);
        packet.put("actual vel", motor.getCurrentAngularVelocity() * 180 / Math.PI);
        dashboard.sendTelemetryPacket(packet);
    }
}
