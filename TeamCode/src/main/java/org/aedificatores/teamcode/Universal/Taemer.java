package org.aedificatores.teamcode.Universal;

public class Taemer {
    private long baseTime;

    public Taemer() {
        baseTime = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - baseTime;
    }

    public void resetTime() {
        baseTime = System.currentTimeMillis();
    }
}
