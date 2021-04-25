package org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Robots.GandalfBotConfig;

import java.util.ArrayList;

public class GandalfTransfer {

    public enum TransferPriority {
        EMERGENCY(40), // Used to get rings unstuck
        SHOOT_RING(30),
        MOVE_RING_AUTOMATION(20),
        LOW_PRIORITY_STOP(10);

        private int rank;

        TransferPriority(int rank) {
            this.rank = rank;
        }

        public int getRank() {
            return rank;
        }
    }

    private CRServo left, right;

    private class PowerCommand {
        double pow;
        TransferPriority priority;

        public PowerCommand(double pow, TransferPriority priority) {
            this.pow = pow;
            this.priority = priority;
        }
    }

    ArrayList<PowerCommand> powerSetQueue; // not really a queue, but i don't know what to call it :P

    public GandalfTransfer(HardwareMap map) {
        left = map.crservo.get(GandalfBotConfig.INTAKE.TRANSFER_LEFT);
        left.setDirection(DcMotorSimple.Direction.REVERSE);
        right = map.crservo.get(GandalfBotConfig.INTAKE.TRANSFER_RIGHT);
        right.setDirection(DcMotorSimple.Direction.FORWARD);
        powerSetQueue = new ArrayList<>();
    }

    public void queueSetPower(double pow, TransferPriority priority) {
        powerSetQueue.add(new PowerCommand(pow, priority));
    }

    public void update() {
        if (!powerSetQueue.isEmpty()) {
            int highestPriorityIndex = 0;
            for (int i = 0; i < powerSetQueue.size(); ++i) {
                if (powerSetQueue.get(i).priority.getRank() > powerSetQueue.get(highestPriorityIndex).priority.getRank()) {
                    highestPriorityIndex = i;
                }
            }

            setPower(powerSetQueue.get(highestPriorityIndex).pow);
            powerSetQueue.clear();
        }
    }

    public void setPower(double pow) {
        left.setPower(pow);
        right.setPower(pow);
    }
}
