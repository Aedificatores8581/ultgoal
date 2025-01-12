package org.aedificatores.teamcode.OpModes.Auto.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleMotor;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Universal.OpModeGroups;

@Autonomous(group = OpModeGroups.GANDALF)
@Config
public class GandalfWobbleMotorMoveTest extends OpMode {
    public static final double SERV_UP_CLOSED_POS = .1278;
    public static final double SERV_LO_CLOSED_POS = .052;
    enum State {MOVING_UP, MOVING_DOWN}

    public static double UP_ANGLE = 100*Math.PI/180.0;
    public static double DOWN_ANGLE = 260*Math.PI/180.0;

    GandalfWobbleMotor motor;
    Servo upper, lower;
    FtcDashboard dashboard;

    State state = State.MOVING_UP;
    TelemetryPacket packet = new TelemetryPacket();
    @Override
    public void init() {
        motor = new GandalfWobbleMotor(hardwareMap, GandalfWobbleMotor.Mode.AUTO);
        motor.setWobbleState(GandalfWobbleMotor.WobbleState.NOT_HOLDING_WOBBLE);
        upper = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_UP);
        lower = hardwareMap.servo.get(GandalfBotConfig.WOBBLE.SERV_LO);
        upper.setPosition(SERV_UP_CLOSED_POS);
        lower.setPosition(SERV_LO_CLOSED_POS);
        dashboard = FtcDashboard.getInstance();
    }

    @Override
    public void start() {
        motor.gotoAngle(UP_ANGLE);
    }

    @Override
    public void loop() {
        switch (state) {
            case MOVING_UP:
                if (!motor.isMoving()) {
                    motor.gotoAngle(DOWN_ANGLE);
                    state = State.MOVING_DOWN;
                }
                break;
            case MOVING_DOWN:
                if (!motor.isMoving()) {
                    motor.gotoAngle(UP_ANGLE);
                    state = State.MOVING_UP;
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
