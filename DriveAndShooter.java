package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
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

import java.util.List;

@TeleOp(name = "DriveAndShooter")
public class DriveAndShooter extends LinearOpMode {

    // Hardware Definitions
    private DcMotorEx leftFront, rightFront, leftBack, rightBack;
    private DcMotor intake, agitator;
    private DcMotorEx shooter;
    private Limelight3A limelight;

    //private IMU imu;

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

    final static double MAX_SPEED_ON_WHEELS = 2580;

    int lastVelocity = 0;

    double lastError = 0.0;
    int id_needed = 20;
    int lastIdNeeded = 20;

    boolean align = false;
    double totalError = 0.0;
    
    // Manual per-tag turn signs
    double turnSignBlue = 1.0; // tag 20
    double turnSignRed  = 1.0; // tag 24
    boolean lastX = false;
    @Override
    public void runOpMode() {
        // --- 1. Hardware Mapping ---
        leftFront = hardwareMap.get(DcMotorEx.class, "LF");
        rightFront = hardwareMap.get(DcMotorEx.class, "RF");
        leftBack = hardwareMap.get(DcMotorEx.class, "LB");
        rightBack = hardwareMap.get(DcMotorEx.class, "RB");
        intake =  hardwareMap.get(DcMotor.class, "intake");


        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotor.class, "agitator");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        //imu = hardwareMap.get(IMU.class, "imu");

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // --- 2. Configuration ---
        // IMU Orientation (Update logo/usb direction if your hub is mounted differently)
        //IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                //RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                //RevHubOrientationOnRobot.UsbFacingDirection.UP));
        //imu.initialize(parameters);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);

        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        // Initialize Limelight
        limelight.pipelineSwitch(0);
        limelight.start();

        // State Variables
        double targetVelocity = 1312;
        double driverSpeedPower = 0.5;
        boolean fieldRelative = false;

        boolean lastLB = false, lastRB1 = false; // Driver 1 bumpers
        boolean lastUp = false, lastDown = false;
        boolean shooterOn = false, wasAPressed = false; //Driver 2
        boolean lastBPressed = false; // GP1 B toggle for align
        //boolean lastGamepad1X = false;

        waitForStart();
        while (opModeIsActive()) {

            // Toggle align with B button (GP1)
            if (gamepad1.b && !lastBPressed) {
                align = !align;
            
                // Reset PID when toggling align
                totalError = 0;
                lastError = 0;
            }
            lastBPressed = gamepad1.b;
            
            // Manual turn flip per tag (X button)
            if (gamepad1.x && !lastX) {
                if (id_needed == 20) {
                    turnSignBlue *= -1.0;
                } else if (id_needed == 24) {
                    turnSignRed *= -1.0;
                }
            
                // reset PID so it doesn’t snap
                totalError = 0;
                lastError = 0;
            }
            lastX = gamepad1.x;

            double xOffset = 0.0;
            double yOffset = 0.0;
            double TargetArea = 0.0;
            boolean foundTag = false;


            // --- 3. Driving Logic (GP1) ---

            // Toggle Field Relative with BACK button
            //if (gamepad1.x && !lastGamepad1X) {
               // fieldRelative = !fieldRelative;
            //}
            //lastGamepad1X = gamepad1.back;

            // Reset Yaw with START button (Handy if the robot drifts)
            //if (gamepad1.start) {
                //imu.resetYaw();
            //}

            /* if (fieldRelative) {
                // Field-Centric Conversion
                double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

                // Rotate the movement vector by the negative robot heading
                double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
                double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

                x = rotX;
                y = rotY;
            } */
            
            // Update speed FIRST (must be before setVelocity)
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            
            // Update speed FIRST (must be before setVelocity)
            if (gamepad1.left_trigger > 0.5) {
                driverSpeedPower = 0.3;
            } else if (gamepad1.right_trigger > 0.5) {
                driverSpeedPower = 0.8;
            }
            
            // Driver Speed fine adjust (bumpers)
            if (gamepad1.right_bumper && !lastRB1) driverSpeedPower = Math.min(1.0, driverSpeedPower + 0.1);
            if (gamepad1.left_bumper && !lastLB)  driverSpeedPower = Math.max(0.1, driverSpeedPower - 0.1);
            lastRB1 = gamepad1.right_bumper;
            lastLB  = gamepad1.left_bumper;
            
            if (!align) {
                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);
            
                leftFront.setVelocity(((y + x + rx) / denominator) * MAX_SPEED_ON_WHEELS * driverSpeedPower);
                leftBack.setVelocity(((y - x + rx) / denominator) * MAX_SPEED_ON_WHEELS * driverSpeedPower);
                rightFront.setVelocity(((y - x - rx) / denominator) * MAX_SPEED_ON_WHEELS * driverSpeedPower);
                rightBack.setVelocity(((y + x - rx) / denominator) * MAX_SPEED_ON_WHEELS * driverSpeedPower);
            }


            // --- 4. Shooter Presets & Manual Adjust (GP2) ---


            //INTAKE
            if (gamepad2.dpad_right) {
                intake.setPower(1.0);  // Intake Forward
            } else if (gamepad2.dpad_left) {
                intake.setPower(-1.0); // Intake Reverse
            } else {
                intake.setPower(0.0);  // Stop Intake
            }

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
                int velocityFromDistance = getVelocityFromDistance();
                if (velocityFromDistance != 0) {
                    targetVelocity = velocityFromDistance;
                }
                shooter.setVelocity(targetVelocity);
            } else {
                shooter.setVelocity(0.0);
            }

            double velocityTolerance = 40.0;
            boolean isAtSpeed = Math.abs(shooter.getVelocity() - targetVelocity) < velocityTolerance;

            // --- 5. Agitator Logic (GP2 B) ---
            if (gamepad2.b && isAtSpeed && shooterOn) {
                // Left bumper on GP2 reverses agitator if it gets stuck
                agitator.setDirection(gamepad2.left_bumper ? DcMotor.Direction.FORWARD : DcMotor.Direction.REVERSE);
                agitator.setPower(1.0);
            } else if (gamepad2.b && !isAtSpeed) {
                agitator.setPower(0.0);
                gamepad2.rumble(0.35, 0.35, 100);
            } else {
                agitator.setPower(0);
            }

            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                Pose3D botpose = result.getBotpose();

                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                // AprilTag selection (DPAD): Blue = left = 20, Red = right = 24
                if (gamepad1.dpad_left) {
                    id_needed = 20;
                } else if (gamepad1.dpad_right) {
                    id_needed = 24;
                }
                
                // RESET PID when switching tags
                if (id_needed != lastIdNeeded) {
                    totalError = 0;
                    lastError = 0;
                    lastIdNeeded = id_needed;
                }


                for (LLResultTypes.FiducialResult tag : fiducials) {
                    if (tag.getFiducialId() == id_needed) {
                        // 3. Select values from this specific "row"
                        foundTag = true;
                        xOffset = tag.getTargetXDegrees();
                        yOffset = tag.getTargetYDegrees();

                        TargetArea = tag.getTargetArea();

                        botpose = tag.getRobotPoseTargetSpace();

                    }
                }



                telemetry.addLine("=== FIELD POS ===");
                telemetry.addData("X (m)", "%.3f", botpose.getPosition().x);
                telemetry.addData("Y (m)", "%.3f", botpose.getPosition().y);
                telemetry.addData("Yaw", "%.2f°", botpose.getOrientation().getYaw(AngleUnit.DEGREES));

                telemetry.addLine("=== TARGET DATA ===");
                telemetry.addData("tx", "%.2f°", xOffset);
                telemetry.addData("ty", "%.2f°", yOffset);

                telemetry.addLine("=== TARGET RANGE ===");
                telemetry.addData("Target Area: ", TargetArea);


            }

            if (align) {
                double yDrive = -gamepad1.left_stick_y;
                double xDrive = gamepad1.left_stick_x;
                double rxDrive = gamepad1.right_stick_x;

                if (foundTag) {
                    // Blue (20) and Red (24) can require opposite turn direction depending on camera/pose math
                    double sign = (id_needed == 20) ? turnSignBlue : turnSignRed;
                    rxDrive = sign * GetPefectTurn(xOffset);
                } else {
                    totalError = 0;
                    lastError = 0;
                }

                double denominator = Math.max(
                        Math.abs(yDrive) + Math.abs(xDrive) + Math.abs(rxDrive), 1.0
                );

                leftFront.setVelocity(((yDrive + xDrive + rxDrive) / denominator)
                        * MAX_SPEED_ON_WHEELS * driverSpeedPower);
                leftBack.setVelocity(((yDrive - xDrive + rxDrive) / denominator)
                        * MAX_SPEED_ON_WHEELS * driverSpeedPower);
                rightFront.setVelocity(((yDrive - xDrive - rxDrive) / denominator)
                        * MAX_SPEED_ON_WHEELS * driverSpeedPower);
                rightBack.setVelocity(((yDrive + xDrive - rxDrive) / denominator)
                        * MAX_SPEED_ON_WHEELS * driverSpeedPower);
            }


            // --- 6. Limelight Data Collection ---
            //LLResult result = limelight.getLatestResult();

            // --- 7. Math Conversion & Telemetry ---
            double v = targetVelocity;
            double calculatedPower = (3.43429 * Math.pow(10, -9)) * Math.pow(v, 3)
                    - (0.0000103703) * Math.pow(v, 2)
                    + (0.0453263) * v
                    + 0.603931;

            telemetry.addLine("--- SHOOTER STATUS ---");
            telemetry.addData("State", shooterOn ? ">> RUNNING <<" : "STOPPED");
            telemetry.addData("Target Velocity", "%.0f ticks/s", targetVelocity);
            telemetry.addData("Current Velocity", "%.0f ticks/s", shooter.getVelocity());
            telemetry.addData("Calculated Power", "%.2f%%", calculatedPower);
            telemetry.addData("Left Front Velocity: ", leftFront.getVelocity());
            telemetry.addData("Right Front Velocity: ", rightFront.getVelocity());
            telemetry.addData("Left Back Velocity: ", leftBack.getVelocity());
            telemetry.addData("Right Back Velocity: ", rightBack.getVelocity());
            telemetry.addData("Limelight Distance (cm): ", getDistanceFromAprilTag());

            // Limelight Position Data
            /*telemetry.addLine("\n--- LIMELIGHT DATA ---");
            if (result != null && result.isValid()) {
                Pose3D botpose = result.getBotpose();

                telemetry.addData("X (m)", "%.3f", botpose.getPosition().x);
                telemetry.addData("Y (m)", "%.3f", botpose.getPosition().y);
                telemetry.addData("Yaw", "%.2f°", botpose.getOrientation().getYaw(AngleUnit.DEGREES));
                telemetry.addData("tx", "%.2f°", result.getTx());
                telemetry.addData("ty", "%.2f°", result.getTy());
            } else {
                telemetry.addLine("NO TARGET DETECTED");
            }*/
            // If aligning: keep translation from sticks, but rotation comes from PID when tag is found



            telemetry.addLine("\n=== CONTROLS QUICK-REF ===");
            telemetry.addLine("GP2 A: Toggle Shooter");
            telemetry.addLine("GP2 Dpad Up/Down: +/- Power");
            telemetry.addLine("GP2 B: Agitator (L-Trig to Rev)");
            telemetry.addData("GP1 Bumpers: Drive Speed, Current: ", driverSpeedPower);

            telemetry.addLine("\n--- PRESETS (GP2) ---");
            telemetry.addLine("L-Trig: Far | R-Trig: Close | R-Bumper: Mid");

            telemetry.update();
        }

        //limelight.stop();
    }

    public double getDistanceFromAprilTag() {
        LLResult result = limelight.getLatestResult();

        double foundDistance = 0.0;

        if (result != null && result.isValid()) {
            double ty = result.getTy(); //Distance from center of limelight image to the center of target (apriltag)

            double mountAngle = 20.0;
            double lensHeight = 23.0; //CM
            double tagHeight = 71.12; //CM

            double distance = (tagHeight - lensHeight) / Math.tan(Math.toRadians(mountAngle + ty));
            telemetry.addData("Calculated Distance", distance);
            foundDistance = distance;
        }

        return foundDistance;
    }

    public double GetPefectTurn(double xOffset) {
        boolean aligned = false;

        double pTerm = 0.0;
        double iTerm = 0.0;
        double dTerm = 0.0;

        // Constants
        double kP = 0.045;
        double kI = 0.00001;
        double kD = 0.24;

        telemetry.addData("P", pTerm);
        telemetry.addData("I", iTerm);
        telemetry.addData("D", dTerm);

        double error = xOffset; // Current Error (E)

        // 1. Proportional Term (P1)
        pTerm = kP * error;

        // 2. Integral Term (P2)
        totalError += error;
        iTerm = kI * totalError;

        // 3. Derivative Term (P3)
        dTerm = kD * (error - lastError);

        // Combine them (Turn)
        double power = pTerm + iTerm + dTerm;

        // Save current error for the next loop's D-term calculation
        lastError = error;

        // Exit condition
        return power;

    }

    public int getVelocityFromDistance() {
        int calculatedVelocity = 0;
        double distanceFromDepot = getDistanceFromAprilTag();

        if (distanceFromDepot != 0.0) {
            calculatedVelocity = (int) ((1.75534 * distanceFromDepot) + 1242.90722);
        }

        return calculatedVelocity;
    }
}
