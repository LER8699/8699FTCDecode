package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name = "DriveAndShooter")
public class DriveAndShooter extends LinearOpMode {

    // Hardware Definitions
    private DcMotor leftFront, rightFront, leftBack, rightBack, intake;
    private DcMotorEx shooter, agitator;
    //private Limelight3A limelight;

    private IMU imu;

    // Shooter PIDF Constants
    final static double F = 13.5354;
    final static double P = 300.0;

    // Preset Velocities
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
        intake =  hardwareMap.get(DcMotor.class, "intake");

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotorEx.class, "agitator");
        imu = hardwareMap.get(IMU.class, "imu");

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        agitator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // --- 2. Configuration ---
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP));
        imu.initialize(parameters);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);

        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        // State Variables
        double targetVelocity = 1312;
        double driverSpeedPower = 0.5;
        boolean fieldRelative = false;

        boolean lastLB = false, lastRB1 = false;
        boolean lastUp = false, lastDown = false;
        boolean shooterOn = false, wasAPressed = false;
        boolean lastGamepad1X = false;

        waitForStart();

        while (opModeIsActive()) {
            // --- 3. Driving Logic (GP1) ---

            /* --- RELATIVE MOVEMENT COMMENTED OUT ---
            if (gamepad1.x && !lastGamepad1X) {
                fieldRelative = !fieldRelative;
            }
            lastGamepad1X = gamepad1.x;

            if (gamepad1.start) {
                imu.resetYaw();
            }
            --------------------------------------- */

            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            /* --- FIELD CENTRIC CONVERSION COMMENTED OUT ---
            if (fieldRelative) {
                double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
                double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
                double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
                x = rotX;
                y = rotY;
            }
            ------------------------------------------------ */

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);

            leftFront.setPower(((y + x + rx) / denominator) * driverSpeedPower);
            leftBack.setPower(((y - x + rx) / denominator) * driverSpeedPower);
            rightFront.setPower(((y - x - rx) / denominator) * driverSpeedPower);
            rightBack.setPower(((y + x - rx) / denominator) * driverSpeedPower);

            // Speed Presets
            if (gamepad1.left_trigger > 0.5) {
                driverSpeedPower = 0.3;
            } else if (gamepad1.right_trigger > 0.5) {
                driverSpeedPower = 0.8;
            }

            // Driver Speed Toggle
            if (gamepad1.right_bumper && !lastRB1) driverSpeedPower = Math.min(1.0, driverSpeedPower + 0.1);
            if (gamepad1.left_bumper && !lastLB) driverSpeedPower = Math.max(0.1, driverSpeedPower - 0.1);
            lastRB1 = gamepad1.right_bumper;
            lastLB = gamepad1.left_bumper;

            // --- 4. Shooter Logic (GP2) ---
            if (gamepad2.dpad_right) {
                intake.setPower(1.0);
            } else if (gamepad2.dpad_left) {
                intake.setPower(-1.0);
            } else {
                intake.setPower(0.0);
            }

            if (gamepad2.left_trigger > 0.5) {
                targetVelocity = VELOCITY_FAR;
            } else if (gamepad2.right_trigger > 0.5) {
                targetVelocity = VELOCITY_CLOSE;
            } else if (gamepad2.right_bumper) {
                targetVelocity = VELOCITY_MID;
            } else if (gamepad2.left_bumper) {
                targetVelocity = ENEMY_DEPOT;
            }

            if (gamepad2.dpad_up && !lastUp) targetVelocity = Math.min(MAX_VELOCITY, targetVelocity + POWER_ITERATE_STEP);
            if (gamepad2.dpad_down && !lastDown) targetVelocity = Math.max(0.0, targetVelocity - POWER_ITERATE_STEP);
            lastUp = gamepad2.dpad_up;
            lastDown = gamepad2.dpad_down;

            if (gamepad2.a && !wasAPressed) shooterOn = !shooterOn;
            wasAPressed = gamepad2.a;

            if (shooterOn) {
                shooter.setVelocity(targetVelocity);
            } else {
                shooter.setVelocity(0.0);
            }

            double velocityTolerance = 40.0;
            boolean isAtSpeed = Math.abs(shooter.getVelocity() - targetVelocity) < velocityTolerance;

            // --- 5. Agitator Logic ---
            if (gamepad2.b && isAtSpeed && shooterOn) {
                agitator.setDirection(gamepad2.left_bumper ? DcMotor.Direction.FORWARD : DcMotor.Direction.REVERSE);
                agitator.setVelocity(600.0);
            } else if (gamepad2.b && !isAtSpeed) {
                agitator.setVelocity(0);
                gamepad2.rumble(0.35, 0.35, 100);
            } else {
                agitator.setVelocity(0);
            }

            // --- 7. Telemetry ---
            double v = targetVelocity;
            double calculatedPower = (3.43429 * Math.pow(10, -9)) * Math.pow(v, 3)
                    - (0.0000103703) * Math.pow(v, 2)
                    + (0.0453263) * v
                    + 0.603931;

            telemetry.addLine("--- SHOOTER STATUS ---");
            telemetry.addData("State", shooterOn ? ">> RUNNING <<" : "STOPPED");
            telemetry.addData("Target Velocity", "%.0f ticks/s", targetVelocity);
            telemetry.addData("Current Velocity", "%.0f ticks/s", shooter.getVelocity());
            telemetry.addData("Drive Mode", fieldRelative ? "FIELD-CENTRIC" : "ROBOT-CENTRIC");
            telemetry.addData("Speed Multiplier", "%.1f", driverSpeedPower);

            telemetry.update();
        }
    }
}
