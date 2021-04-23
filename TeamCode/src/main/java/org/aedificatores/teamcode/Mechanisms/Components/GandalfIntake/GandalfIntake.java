package org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class GandalfIntake {

    enum IntakeState { FOREWARD, REVERSE, OFF}
    private IntakeState state = IntakeState.OFF;

    public DcMotorEx actuator;
    public GandalfIntakeLift lift;
    public GandalfTransfer transfer;

    private static final double INTAKE_POW = 1.0;
    private static final double TOP_DIST_THRESHOLD = 9.0;
    private static final double BOTTOM_DIST_THRESHOLD = 6.0;
    private static final double INTAKE_DIST_THRESHOLD = 1.0;

    private RevColorSensorV3 topSensor;
    private RevColorSensorV3 bottomSensor;
    private RevColorSensorV3 intakeSensor;

    public GandalfIntake(HardwareMap map, double angle, GandalfIntakeLift.Mode m) {
        actuator = map.get(DcMotorEx.class, GandalfBotConfig.INTAKE.MOT);
        lift = new GandalfIntakeLift(map, angle, m);
        transfer = new GandalfTransfer(map);

        topSensor = map.get(RevColorSensorV3.class, GandalfBotConfig.INTAKE.TOP_DETECT);
        bottomSensor = map.get(RevColorSensorV3.class, GandalfBotConfig.INTAKE.BOTTOM_DETECT);
        intakeSensor = map.get(RevColorSensorV3.class, GandalfBotConfig.INTAKE.INTAKE_DETECT);
    }

    public void update() {
        lift.update();
    }

    public boolean ringInIntake() {
        return intakeSensor.getDistance(DistanceUnit.CM) < INTAKE_DIST_THRESHOLD;
    }

    public boolean ringAtTopTransfer() {
        return bottomSensor.getDistance(DistanceUnit.CM) < BOTTOM_DIST_THRESHOLD || topSensor.getDistance(DistanceUnit.CM) < TOP_DIST_THRESHOLD;
    }

    public void toggleIntake() {
        if (state != IntakeState.OFF) {
            off();
        } else {
            forward();
        }
    }

    public void toggleOuttake() {
        if (state != IntakeState.OFF) {
            off();
        } else {
            backward();
        }
    }

    public void forward() {
        state = IntakeState.FOREWARD;
        actuator.setPower(INTAKE_POW);
    }

    public void backward() {
        state = IntakeState.REVERSE;
        actuator.setPower(-INTAKE_POW);
    }

    public void off() {
        state = IntakeState.OFF;
        actuator.setPower(0.0);
    }
}
