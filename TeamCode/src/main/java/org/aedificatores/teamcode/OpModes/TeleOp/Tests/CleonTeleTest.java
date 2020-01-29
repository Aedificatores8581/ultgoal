package org.aedificatores.teamcode.OpModes.TeleOp.Tests;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "BlocksCleonTelop (Blocks to Java)", group = "")
public class CleonTeleTest extends LinearOpMode {

    private DcMotor la;
    private DcMotor lf;
    private DcMotor rf;
    private DcMotor ra;
    private Servo kickerservo;
    private DcMotor rlift;
    private DcMotor llift;
    private Servo grab;
    private CRServo extensionservo;
    private DigitalChannel frontextension;
    private DcMotor lint;
    private DcMotor rint;
    private Servo foundation1;
    private Servo teammarker;
    private Servo turndeposit;

    boolean SpeedSlow = true;
    boolean FoundationSlow = true;
    boolean OpenSlow = true;
    boolean PIvotServoSlow = true;
    boolean MarkerSlow = true;
    boolean IntakeSpeedSlow = true;
    boolean IntakeSpeedSlow2 = true;
    boolean GrabberSlow = true;
    String intakedirection = new String();
    double ExtensionServo = 0;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        la = hardwareMap.dcMotor.get("la");
        lf = hardwareMap.dcMotor.get("lf");
        rf = hardwareMap.dcMotor.get("rf");
        ra = hardwareMap.dcMotor.get("ra");
        kickerservo = hardwareMap.servo.get("kickerservo");
        rlift = hardwareMap.dcMotor.get("rlift");
        llift = hardwareMap.dcMotor.get("llift");
        grab = hardwareMap.servo.get("grab");
        extensionservo = hardwareMap.crservo.get("extensionservo");
        frontextension = hardwareMap.digitalChannel.get("frontextension");
        lint = hardwareMap.dcMotor.get("lint");
        rint = hardwareMap.dcMotor.get("rint");
        foundation1 = hardwareMap.servo.get("foundation1");
        teammarker = hardwareMap.servo.get("teammarker");
        turndeposit = hardwareMap.servo.get("turndeposit");

        la.setDirection(DcMotorSimple.Direction.REVERSE);
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        la.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ra.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ra.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        la.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        kickerservo.setPosition(0);
        // Put initialization blocks here.
        boolean Button = false;
        double IntakeSpeed = 0;
        boolean Extending = false;
        boolean Retracting = false;
        boolean Pivoted = false;
        boolean Extended = false;
        double FoundationServoPosition = 1;
        double FoundationServoPosition2 = 1;
        double TeamMarkerServoPosition = 0;
        double GrabberServoPostion = 0;
        double PivotServoPosition = 0.51;
        double MaxSpeed = 0.8;
        double LiftTargetPosition = 0;
        rlift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        llift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rlift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        llift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rlift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        llift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                // Put loop blocks here.
                double PIvot = -gamepad1.left_stick_x;
                double Vertical = gamepad1.left_trigger - gamepad1.right_trigger;
                double Horizontal = -gamepad1.right_stick_x;
                double rfPower = Math.min(Math.max(-PIvot + (Vertical - Horizontal), -MaxSpeed), MaxSpeed);
                double raPower = Math.min(Math.max(-PIvot + Vertical + Horizontal, -MaxSpeed), MaxSpeed);
                double lfPower = Math.min(Math.max(PIvot + Vertical + Horizontal, -MaxSpeed), MaxSpeed);
                double laPower = Math.min(Math.max(PIvot + (Vertical - Horizontal), -MaxSpeed), MaxSpeed);
                if (rf.getPower() != rfPower || ra.getPower() != raPower) {
                    rf.setPower(rfPower);
                    ra.setPower(raPower);
                }
                if (lf.getPower() != lfPower || la.getPower() != laPower) {
                    lf.setPower(lfPower);
                    la.setPower(laPower);
                }
                else if (gamepad2.left_bumper) {
                    double holding_power = 0.18;
                    rlift.setPower(-holding_power);
                    llift.setPower(-holding_power);
                }
                else {
                    double speedToggle = gamepad2.left_trigger * 0.4 - 0.95;
                    rlift.setPower(gamepad2.left_stick_y * speedToggle);
                    llift.setPower(-gamepad2.left_stick_y * speedToggle);
                }
            }
                if (gamepad1.left_stick_button && SpeedSlow) {
                    if (MaxSpeed == 0.8) {
                        MaxSpeed = 0.3;
                    } else {
                        MaxSpeed = 0.8;
                    }
                    SpeedSlow = true;
                } else if (!gamepad1.left_stick_button) {
                    SpeedSlow = true;
                }
                if (gamepad1.right_stick_button && FoundationSlow) {
                    if (FoundationServoPosition == 1) {
                        FoundationServoPosition = 0.4;
                    } else {
                        FoundationServoPosition = 1;
                    }
                    FoundationSlow = false;
                } else if (!gamepad1.right_stick_button) {
                    FoundationSlow = true;
                }
                if (gamepad1.b && OpenSlow && Extended) {
                    if (GrabberServoPostion == 0.2) {
                        GrabberServoPostion = 0.8;
                    } else {
                        GrabberServoPostion = 0.2;
                    }
                    OpenSlow = false;
                } else if (gamepad1.b && OpenSlow && !Extended) {
                    if (kickerservo.getPosition() == 0) {
                        kickerservo.setPosition(0.4);
                    } else {
                        kickerservo.setPosition(0);
                    }
                    OpenSlow = false;
                } else if (!gamepad1.b) {
                    OpenSlow = true;
                }
                if (gamepad1.a && PIvotServoSlow && Extended) {
                    if (PivotServoPosition == 0.51) {
                        if (GrabberServoPostion == 0.8) {
                            PivotServoPosition = 0.85;
                            Pivoted = true;
                        }
                    } else {
                        Pivoted = false;
                        PivotServoPosition = 0.51;
                    }
                    PIvotServoSlow = false;
                } else if (!gamepad1.a) {
                    PIvotServoSlow = true;
                }
                if (gamepad1.x && MarkerSlow) {
                    if (TeamMarkerServoPosition == 0) {
                        TeamMarkerServoPosition = 0.5;
                    } else {
                        TeamMarkerServoPosition = 0;
                    }
                    MarkerSlow = false;
                } else if (!gamepad1.x) {
                    MarkerSlow = true;
                }
                if (gamepad1.left_bumper && IntakeSpeedSlow) {
                    Button = true;
                    intakedirection = "Negative";
                    IntakeSpeedSlow = false;
                } else {
                    IntakeSpeedSlow = true;
                }
                if (gamepad1.right_bumper && IntakeSpeedSlow2) {
                    Button = true;
                    intakedirection = "Postive";
                    IntakeSpeedSlow = false;
                } else {
                    IntakeSpeedSlow2 = true;
                }
                if (Button) {
                    Button = false;
                    if (intakedirection == "Negative" && IntakeSpeed < 0 || intakedirection == "Postive" && IntakeSpeed > 0) {
                        IntakeSpeed = 0;
                    } else if (intakedirection == "Negative") {
                        IntakeSpeed = -1;
                    } else if (intakedirection == "Postive") {
                        IntakeSpeed = 1;
                    }
                }
                if (gamepad1.y && GrabberSlow) {
                    if (Extended && !Pivoted) {
                        GrabberServoPostion = 0.2;
                        kickerservo.setPosition(0);
                        if (grab.getPosition() != GrabberServoPostion) {
                            grab.setPosition(GrabberServoPostion);
                            sleep(300);
                        }
                        ExtensionServo = -0.75;
                        extensionservo.setPower(ExtensionServo);
                        sleep(150);
                        Extending = false;
                        Retracting = true;
                    } else if (Pivoted) {
                    } else {
                        GrabberServoPostion = 0.8;
                        if (kickerservo.getPosition() != 0.4) {
                            kickerservo.setPosition(0.4);
                            sleep(300);
                        }
                        grab.setPosition(GrabberServoPostion);
                        sleep(300);
                        ExtensionServo = 0.75;
                        extensionservo.setPower(ExtensionServo);
                        sleep(100);
                        Retracting = false;
                        Extending = true;
                    }
                    GrabberSlow = false;
                } else if (!gamepad1.y) {
                    GrabberSlow = true;
                }
                if (Extending && frontextension.getState() == false) {
                    ExtensionServo = 0;
                    Extended = true;
                    Extending = false;
                } else if (Retracting && frontextension.getState() == false) {
                    ExtensionServo = 0;
                    Extended = false;
                    Extending = false;
                    Retracting = false;
                }
                if (gamepad1.dpad_left && !gamepad1.dpad_right) {
                    ExtensionServo = -0.85;
                } else if (gamepad1.dpad_right && !gamepad1.dpad_left) {
                    ExtensionServo = 0.85;
                } else if (!gamepad1.dpad_right && !gamepad1.dpad_right) {
                    if (ExtensionServo == 0.75 || ExtensionServo == -0.75) {
                    } else {
                        ExtensionServo = 0;
                    }
                }
                FoundationServoPosition = Math.min(Math.max(FoundationServoPosition, 0), 1);
                FoundationServoPosition2 = Math.min(Math.max(FoundationServoPosition2, 0), 1);
                GrabberServoPostion = Math.min(Math.max(GrabberServoPostion, 0), 1);
                PivotServoPosition = Math.min(Math.max(PivotServoPosition, 0), 1);
                TeamMarkerServoPosition = Math.min(Math.max(TeamMarkerServoPosition, 0), 1);
                if (IntakeSpeed != lint.getPower()) {
                    lint.setPower(IntakeSpeed);
                    rint.setPower(-IntakeSpeed);
                }
                if (ExtensionServo != extensionservo.getPower()) {
                    extensionservo.setPower(ExtensionServo);
                }
                if (FoundationServoPosition != foundation1.getPosition()) {
                    foundation1.setPosition(FoundationServoPosition);
                }
                if (TeamMarkerServoPosition != teammarker.getPosition()) {
                    teammarker.setPosition(TeamMarkerServoPosition);
                }
                if (GrabberServoPostion != grab.getPosition()) {
                    grab.setPosition(GrabberServoPostion);
                }
                if (PivotServoPosition != turndeposit.getPosition()) {
                    turndeposit.setPosition(PivotServoPosition);
                }









                telemetry.addData("Extended or Not", Extended);
                telemetry.addData("Extension Servo", ExtensionServo);
                telemetry.addData("Foundation Position", FoundationServoPosition);
                telemetry.addData("Front Extension", frontextension.getState());
                telemetry.addData("Forward Encoder", rf.getCurrentPosition());
                telemetry.addData("Strafe Encoder", ra.getCurrentPosition());
                telemetry.addData("Encoder 3", la.getCurrentPosition());
                telemetry.addData("Right Lift Encoder", rlift.getCurrentPosition());
                telemetry.addData("Left Lift Encoder", llift.getCurrentPosition());
                telemetry.addData("Lift Target Position", rlift.getTargetPosition());
                telemetry.update();
            }
        }
    }
