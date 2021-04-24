package org.aedificatores.teamcode.Mechanisms.Sensors;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RevLEDIndicator {
    public enum Color {
        AMBER(false, false),
        GREEN(true, false),
        RED(false, true),
        OFF(true, true);
        private boolean c1, c2;

        Color(boolean c1, boolean c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        public boolean getC1() {
            return c1;
        }

        public boolean getC2() {
            return c2;
        }
    }

    Color currentColor;

    private DigitalChannel channel1, channel2;

    public RevLEDIndicator(HardwareMap map, String c1, String c2) {
        channel1 = map.digitalChannel.get(c1);
        channel1.setMode(DigitalChannel.Mode.OUTPUT);
        channel2 = map.digitalChannel.get(c2);
        channel2.setMode(DigitalChannel.Mode.OUTPUT);
        setColor(Color.OFF);

    }

    public void setColor(Color c) {
        currentColor = c;
        channel1.setState(currentColor.c1);
        channel2.setState(currentColor.c2);
    }

    public Color getColor() {
        return currentColor;
    }
}
