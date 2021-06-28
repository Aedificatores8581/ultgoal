package org.aedificatores.teamcode.Universal;

public class Taemer {
    private long baseTime;

    public Taemer() {
        baseTime = System.currentTimeMillis();
    }

    public long getTimeMillis() {
        return System.currentTimeMillis() - baseTime;
    }
    public double getTimeSec() {
        return getTimeMillis() / 1000.0;
    }

    public void resetTime() {
        baseTime = System.currentTimeMillis();
    }
}
