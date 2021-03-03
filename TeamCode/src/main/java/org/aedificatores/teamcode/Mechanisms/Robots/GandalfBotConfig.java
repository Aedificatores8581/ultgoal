package org.aedificatores.teamcode.Mechanisms.Robots;

public interface GandalfBotConfig {
    interface DT {
        String RF = "Front Right";
        String LF = "Front Left";
        String RR = "Rear Right";
        String LR = "Rear Left";
    }

    interface ODOM {
        String RIGHT = "Front Right";
        String LEFT = "Front Left";
        String STRAFE = "Rear Right";
    }

    interface SHOOT {
        String[] FLING = {"shooter1", "shooter2"};
        String INTAKE = "intake";
        int ODOM_INDEX = 0;
    }
}
