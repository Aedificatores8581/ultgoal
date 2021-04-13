package org.aedificatores.teamcode.Mechanisms.Robots;

public interface GandalfBotConfig {
    interface DT {
        String RF = "Front Right";
        String LF = "Front Left";
        String RR = "Rear Right";
        String LR = "Rear Left";
    }

    interface ODOM {
        String RIGHT = "Rear Right";
        String LEFT = "Front Left";
        String STRAFE = "Front Right";
    }

    interface SHOOT {
        String[] FLING = {"shooter1", "shooter2"};
        String INTAKE = "intake";
        int ODOM_INDEX = 1;
    }

    interface WOBBLE {
        String POTENT = "potentiometer";
        String MOT = "wobble";
        String SERV_UP = "upper";
        String SERV_LO = "lower";
    }

    interface INTAKE {
        String MOT = "intake";
        String SERV = "intake";
        String ENC = "wobble";
        String TRANSFER_LEFT = "transferleft";
        String TRANSFER_RIGHT = "transferright";
    }
}
