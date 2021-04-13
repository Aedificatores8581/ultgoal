package org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleMotor;
import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;
import org.aedificatores.teamcode.Mechanisms.Sensors.Encoder;
import org.aedificatores.teamcode.Universal.Taemer;

public class GandalfIntake {
    public DcMotorEx actuator;
    public GandalfIntakeLift lift;
    public GandalfTransfer transfer;
    public GandalfIntake(HardwareMap map, double angle, GandalfIntakeLift.Mode m) {
        actuator = map.get(DcMotorEx.class, GandalfBotConfig.INTAKE.MOT);
        lift = new GandalfIntakeLift(map, angle, m);
        transfer = new GandalfTransfer(map);
    }
}
