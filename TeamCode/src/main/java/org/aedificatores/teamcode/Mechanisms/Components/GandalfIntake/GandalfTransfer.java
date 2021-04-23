package org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;

public class GandalfTransfer {
    private CRServo left, right;

    public GandalfTransfer(HardwareMap map) {
        left = map.crservo.get(GandalfBotConfig.INTAKE.TRANSFER_LEFT);
        left.setDirection(DcMotorSimple.Direction.REVERSE);
        right = map.crservo.get(GandalfBotConfig.INTAKE.TRANSFER_RIGHT);
        right.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void setPower(double pow) {
        left.setPower(pow);
        right.setPower(pow);
    }
}
