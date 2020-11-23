package org.aedificatores.teamcode.Mechanisms.Robots;

public interface SawronBotConfig {
    interface DT {
        String RF = "Front Right";
        String LF = "Front Left";
        String RR = "Rear Right";
        String LR = "Rear Left";
    }

    interface WobbleSub {
        String MOT = "wobble goal grabber";
        String LIMIT = "wobble limit";
        String GATE = "Wobble Goal";
    }

    interface ShootSub {
        String SHOOT_MOT = "shooter";
        String INTAKE_MOT = "intake";
        String KICK_SERV = "ring kicker";
        String LIFT_SERV = "disc lift";
    }

    String CONTROL_IMU = "imu 1";
    String EXPANSION_IMU = "imu";
}
