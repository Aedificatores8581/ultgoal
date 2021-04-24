package org.aedificatores.teamcode.Mechanisms.Robots;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake.GandalfIntake;
import org.aedificatores.teamcode.Mechanisms.Components.GandalfIntake.GandalfIntakeLift;
import org.aedificatores.teamcode.Mechanisms.Components.GandalfShooterFlinger;
import org.aedificatores.teamcode.Mechanisms.Components.GandalfWobbleGoal.GandalfWobbleGrabber;
import org.aedificatores.teamcode.Mechanisms.Drivetrains.GandalfMecanum;
import org.aedificatores.teamcode.Mechanisms.Sensors.RevLEDIndicator;
import org.aedificatores.teamcode.Universal.Taemer;

import java.util.List;

public class GandalfBot {
    enum AutoShootingState {
        START_SHOOTER,
        RUN_TRANSFER,
        PAUSE_TRANSFER,
        IDLE
    }

    public static final double TRANSFER_CLOCK_THRESH = .6;
    public static final double MAX_SHOOT_TIME = 6.0;

    public GandalfMecanum drivetrain;
    public GandalfWobbleGrabber wobbleGrabber;
    public GandalfShooterFlinger shooter;
    public GandalfIntake intake;
    RevLEDIndicator shooterIndicator;
    RevLEDIndicator transferIndicator;
    private List<LynxModule> allHubs;

    private AutoShootingState shootingState = AutoShootingState.IDLE;
    private int numRings = 0;

    Taemer transferClock;
    Taemer maxShootTimeClock;

    boolean runTransfer = false;
    boolean isAuto = false;

    public GandalfBot(HardwareMap map, boolean isAuto) {
        drivetrain = new GandalfMecanum(map);
        wobbleGrabber = new GandalfWobbleGrabber(map, (isAuto) ? GandalfWobbleGrabber.Mode.AUTO : GandalfWobbleGrabber.Mode.TELEOP);
        shooter = new GandalfShooterFlinger(map);
        intake = new GandalfIntake(map,0, (isAuto) ? GandalfIntakeLift.Mode.AUTO : GandalfIntakeLift.Mode.TELEOP);

        shooterIndicator = new RevLEDIndicator(map, GandalfBotConfig.SHOOT.SHOOTER_LED_C1, GandalfBotConfig.SHOOT.SHOOTER_LED_C2);
        transferIndicator = new RevLEDIndicator(map, GandalfBotConfig.INTAKE.TRANSFER_LED_C1, GandalfBotConfig.INTAKE.TRANSFER_LED_C2);

        allHubs = map.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
        this.isAuto = isAuto;
        transferClock = new Taemer();
        maxShootTimeClock = new Taemer();
    }

    public void update() {
        drivetrain.update();
        wobbleGrabber.update();
        shooter.update();
        intake.update();

        switch (shootingState) {
            case START_SHOOTER:
                if (shooter.readyToShoot()) {
                    shootingState = AutoShootingState.RUN_TRANSFER;
                    forceTransfer();
                    intake.actuator.setPower(1.0);
                    maxShootTimeClock.resetTime();
                }
                break;
            case PAUSE_TRANSFER:
                if (shooter.readyToShoot() || transferClock.getTimeSec() > TRANSFER_CLOCK_THRESH) {
                    shootingState = AutoShootingState.RUN_TRANSFER;
                    intake.actuator.setPower(1.0);
                    forceTransfer();
                }
                break;
            case RUN_TRANSFER:
                if (!shooter.upToSpeed()) {
                    stopforceTransfer();
                    intake.actuator.setPower(0.0);
                    shootingState = AutoShootingState.PAUSE_TRANSFER;
                    transferClock.resetTime();
                }
        }

        if (shootingState != AutoShootingState.IDLE && shootingState != AutoShootingState.START_SHOOTER && maxShootTimeClock.getTimeSec() > MAX_SHOOT_TIME) {
            if (isAuto) {
                shooter.setSpeed(0.0);
                intake.actuator.setPower(0.0);
            }
            stopforceTransfer();
            shootingState = AutoShootingState.IDLE;
            shooterIndicator.setColor(RevLEDIndicator.Color.OFF);
        }

        if (ringAtTop()) {
            transferIndicator.setColor(RevLEDIndicator.Color.GREEN);
        } else {
            transferIndicator.setColor(RevLEDIndicator.Color.OFF);
        }
        if (intake.ringInIntake() && !ringAtTop() || runTransfer) {
            intake.transfer.setPower(.75);
        } else {
            intake.transfer.setPower(0.0);
        }

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    public void forceTransfer() {
        runTransfer = true;
    }

    public void stopforceTransfer() {
        runTransfer = false;
    }

    public void shootRings(double shooterSpeed, int numRings) {
        shootingState = AutoShootingState.START_SHOOTER;
        this.numRings = numRings;
        shooter.setSpeed(shooterSpeed);
        shooterIndicator.setColor(RevLEDIndicator.Color.RED);
    }

    public int ringsToShoot() {
        return numRings;
    }

    public boolean shooterIdle() {
        return shootingState == AutoShootingState.IDLE;
    }

    public boolean ringAtTop() {
        return intake.ringAtTopTransfer() || shooter.containsRing();
    }
}
