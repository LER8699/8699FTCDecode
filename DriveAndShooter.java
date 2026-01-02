package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "DriveAndShooter")
public class DriveAndShooter extends LinearOpMode {

    // Drive Motors
    private DcMotor leftFront, rightFront, leftBack, rightBack;
    // Shooter Motors
    private DcMotor shooter, agitator;

    @Override
    public void runOpMode() {
        // --- 1. Hardware Mapping ---
        leftFront = hardwareMap.get(DcMotor.class, "LF");
        rightFront = hardwareMap.get(DcMotor.class, "RF");
        leftBack = hardwareMap.get(DcMotor.class, "LB");
        rightBack = hardwareMap.get(DcMotor.class, "RB");

        shooter = hardwareMap.get(DcMotor.class, "shooter");
        agitator = hardwareMap.get(DcMotor.class, "agitator");

        // --- 2. Direction Settings ---
        // Adjust these based on your specific robot's motor orientation
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        double shooterPower = 0.5; // Start at a default midpoint
        double driverSpeedPower = 0.1;
        double powerIterateAmount = 0.05;
        
        boolean lastLB = false;
        boolean lastRB = false;
        boolean lastUp = false;
        boolean lastDown = false;
        
        boolean shooterOn = false;
        boolean wasAPressed = false;

        waitForStart();

        while (opModeIsActive()) {
            // --- 3. Driving Logic (Mecanum/Omni) ---
            double y = -gamepad1.left_stick_y; // Push stick forward is negative
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x; // Rotation

            leftFront.setPower((y + x + rx) * driverSpeedPower);
            leftBack.setPower((y - x + rx) * driverSpeedPower);
            rightFront.setPower((y - x - rx) * driverSpeedPower);
            rightBack.setPower((y + x - rx) * driverSpeedPower);
            
            // --- Driver Speed Control ---
            if (gamepad1.right_bumper && !lastRB) {
                driverSpeedPower = Math.min(1.0, driverSpeedPower + 0.1);
            }
            if (gamepad1.left_bumper && !lastLB) {
                driverSpeedPower = Math.max(0.1, driverSpeedPower - 0.1);
            }
            lastRB = gamepad1.right_bumper;
            lastLB = gamepad1.left_bumper;

            // --- 4. Shooter Power Increments (DPAD) ---
            // Simple edge detection to prevent power from flying to 1.0 instantly
            if (gamepad2.dpad_up && !lastUp) {
                shooterPower = Math.min(1.0, shooterPower + powerIterateAmount);
            }
            if (gamepad2.dpad_down && !lastDown) {
                shooterPower = Math.max(0.0, shooterPower - powerIterateAmount);
            }
            lastUp = gamepad2.dpad_up;
            lastDown = gamepad2.dpad_down;

            // --- 5. Shooter & Agitator Controls ---
            // gamepad 2 toggle shooter
            if (gamepad2.a && !wasAPressed) {
                shooterOn = !shooterOn;
            }
            
            wasAPressed = gamepad2.a;
            
            if (shooterOn) {
                shooter.setPower(shooterPower);
            } else {
                shooter.setPower(0);
            }

            if (gamepad2.b) {
                if (gamepad2.left_trigger > 0.1) {
                    agitator.setDirection(DcMotor.Direction.FORWARD);
                } else {
                    agitator.setDirection(DcMotor.Direction.REVERSE);
                }
                agitator.setPower(0.5);
            } else {
                agitator.setPower(0);
            }

            telemetry.addLine("--- LIVE STATUS ---");
            telemetry.addData("Shooter", shooterOn ? "ACTIVE" : "IDLE");
            telemetry.addData("Power", "%.2f", shooterPower);
            telemetry.addData("Drive Speed", "%.0f%%", driverSpeedPower * 100);
            
            telemetry.addLine("\n=== CONTROLS QUICK-REF ===");
            telemetry.addLine("GP2 A: Toggle Shooter");
            telemetry.addLine("GP2 Dpad Up/Down: +/- Power");
            telemetry.addLine("GP2 B: Agitator (L-Trig to Rev)");
            telemetry.addLine("GP1 Bumpers: Drive Speed");

            telemetry.update();
        }
    }
}
