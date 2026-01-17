package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "DriveAndShooter")
public class DriveAndShooter extends LinearOpMode {

    // Hardware Definitions
    private DcMotor leftFront, rightFront, leftBack, rightBack;
    private DcMotorEx shooter, agitator;
    
    // Shooter PIDF Constants
    final static double F = 13.5354;
    final static double P = 300.0;
    
    // Preset Velocities (Adjust these ticks/sec values based on your testing)
    final static double VELOCITY_FAR   = 1800; 
    final static double VELOCITY_MID   = 1512.0;
    final static double VELOCITY_CLOSE = 1412.0;
    final static double ENEMY_DEPOT = 1620.0;
    
    final static double POWER_ITERATE_STEP = 50.0;
    final static double MAX_VELOCITY = 2800.0;

    @Override
    public void runOpMode() {
        // --- 1. Hardware Mapping ---
        leftFront = hardwareMap.get(DcMotor.class, "LF");
        rightFront = hardwareMap.get(DcMotor.class, "RF");
        leftBack = hardwareMap.get(DcMotor.class, "LB");
        rightBack = hardwareMap.get(DcMotor.class, "RB");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotorEx.class, "agitator");
        
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        agitator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // --- 2. Configuration ---
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        // State Variables
        double targetVelocity = 1312; 
        double driverSpeedPower = 0.5;
        
        boolean lastLB = false, lastRB1 = false; // Driver 1 bumpers
        boolean lastUp = false, lastDown = false;
        boolean shooterOn = false, wasAPressed = false;

        waitForStart();

        while (opModeIsActive()) {
            // --- 3. Driving Logic (GP1) ---
            double y = -gamepad1.left_stick_y; 
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x; 

            leftFront.setPower((y + x + rx) * driverSpeedPower);
            leftBack.setPower((y - x + rx) * driverSpeedPower);
            rightFront.setPower((y - x - rx) * driverSpeedPower);
            rightBack.setPower((y + x - rx) * driverSpeedPower);
            
            if (gamepad1.left_trigger > 0.5) {
                driverSpeedPower = 0.3;
            } else if (gamepad1.right_trigger > 0.5) {
                driverSpeedPower = 0.8; 
            }
            
            // Driver Speed Toggle (GP1 Bumpers)
            if (gamepad1.right_bumper && !lastRB1) driverSpeedPower = Math.min(1.0, driverSpeedPower + 0.1);
            if (gamepad1.left_bumper && !lastLB) driverSpeedPower = Math.max(0.1, driverSpeedPower - 0.1);
            lastRB1 = gamepad1.right_bumper;
            lastLB = gamepad1.left_bumper;

            // --- 4. Shooter Presets & Manual Adjust (GP2) ---
            
            // PRESETS
            if (gamepad2.left_trigger > 0.5) {
                targetVelocity = VELOCITY_FAR;
            } else if (gamepad2.right_trigger > 0.5) {
                targetVelocity = VELOCITY_CLOSE;
            } else if (gamepad2.right_bumper) {
                targetVelocity = VELOCITY_MID;
            } else if (gamepad2.left_bumper) {
                targetVelocity = ENEMY_DEPOT;
            }

            // MANUAL ADJUST (Fine tuning)
            if (gamepad2.dpad_up && !lastUp) targetVelocity = Math.min(MAX_VELOCITY, targetVelocity + POWER_ITERATE_STEP);
            if (gamepad2.dpad_down && !lastDown) targetVelocity = Math.max(0.0, targetVelocity - POWER_ITERATE_STEP);
            lastUp = gamepad2.dpad_up;
            lastDown = gamepad2.dpad_down;

            // Shooter Toggle
            if (gamepad2.a && !wasAPressed) shooterOn = !shooterOn;
            wasAPressed = gamepad2.a;
            
            if (shooterOn) {
                shooter.setVelocity(targetVelocity);
            } else {
                shooter.setVelocity(0.0);
            }
            
            double velocityTolerance = 600.0; 
            boolean isAtSpeed = Math.abs(shooter.getVelocity() - targetVelocity) < velocityTolerance;

            // --- 5. Agitator Logic (GP2 B) ---
            if (gamepad2.b && shooterOn) {
                if (isAtSpeed) {
                    // Shooter is ready: Spin agitator
                    agitator.setDirection(gamepad2.left_bumper ? DcMotor.Direction.FORWARD : DcMotor.Direction.REVERSE);
                    agitator.setVelocity(344.7);
                } else {
                    // Shooter NOT ready: Stop agitator and Rumble
                    agitator.setVelocity(0);
                    
                    // Only trigger rumble if the controller isn't already rumbling
                    if (!gamepad2.isRumbling()) {
                        gamepad2.rumble(0.5, 0.5, 200); // 200ms burst
                    }
                }
            } else {
                // Button not pressed or shooter off
                agitator.setVelocity(0);
            }

            // --- 6. Math Conversion & Telemetry ---
            double v = targetVelocity;
            double calculatedPower = (3.43429 * Math.pow(10, -9)) * Math.pow(v, 3) 
                                   - (0.0000103703) * Math.pow(v, 2) 
                                   + (0.0453263) * v 
                                   + 0.603931;

            telemetry.addLine("--- SHOOTER STATUS ---");
            telemetry.addData("State", shooterOn ? ">> RUNNING <<" : "STOPPED");
            telemetry.addData("Target Velocity", "%.0f ticks/s", targetVelocity);
            telemetry.addData("Calculated Power", "%.2f%%", calculatedPower);
            
            telemetry.addLine("\n=== CONTROLS QUICK-REF ===");
            telemetry.addLine("GP2 A: Toggle Shooter");
            telemetry.addLine("GP2 Dpad Up/Down: +/- Power");
            telemetry.addLine("GP2 B: Agitator (L-Trig to Rev)");
            telemetry.addData("GP1 Bumpers: Drive Speed, Current: ", driverSpeedPower);
            
            telemetry.addLine("\n--- PRESETS (GP2) ---");
            telemetry.addLine("L-Trig: Far | R-Trig: Close | R-Bumper: Mid");
            
            telemetry.update();
        }
    }
}
