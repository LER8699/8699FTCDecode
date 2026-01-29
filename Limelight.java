package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name = "Data Collector: Keli Light")
public class Limelight extends LinearOpMode {

    private Limelight3A limelight;
    private DcMotor lf, lb, rf, rb;

    // Default speed starts at 50%
    private double speedMultiplier = 0.5;
    private boolean lbPressed = false;
    private boolean rbPressed = false;

    private int directionFactor = -1;

    @Override
    public void runOpMode() throws InterruptedException {
        lf = hardwareMap.get(DcMotor.class, "lf");
        lb = hardwareMap.get(DcMotor.class, "lb");
        rf = hardwareMap.get(DcMotor.class, "rf");
        rb = hardwareMap.get(DcMotor.class, "rb");

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // --- SPEED MULTIPLIER LOGIC ---
            // Increase speed with Right Bumper (max 1.0)
            if (gamepad1.right_bumper && !rbPressed) {
                speedMultiplier = Math.min(speedMultiplier + 0.1, 1.0);
                rbPressed = true;
            } else if (!gamepad1.right_bumper) {
                rbPressed = false;
            }

            // Decrease speed with Left Bumper (min 0.1)
            if (gamepad1.left_bumper && !lbPressed) {
                speedMultiplier = Math.max(speedMultiplier - 0.1, 0.1);
                lbPressed = true;
            } else if (!gamepad1.left_bumper) {
                lbPressed = false;
            }

            // --- MECANUM DRIVE SECTION ---
            // We multiply y and x by directionFactor to flip the "front"
            double y = -gamepad1.left_stick_y * directionFactor;
            double x = gamepad1.left_stick_x * directionFactor;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            double lfPower = ((y + x + rx) / denominator) * speedMultiplier;
            double lbPower = ((y - x + rx) / denominator) * speedMultiplier;
            double rfPower = ((y - x - rx) / denominator) * speedMultiplier;
            double rbPower = ((y + x - rx) / denominator) * speedMultiplier;

            lf.setPower(lfPower);
            lb.setPower(lbPower);
            rf.setPower(rfPower);
            rb.setPower(rbPower);

            // --- DATA COLLECTION SECTION ---
            LLResult result = limelight.getLatestResult();

            telemetry.addData("Current Speed", "%.0f%%", speedMultiplier * 100);

            if (result != null && result.isValid()) {
                Pose3D botpose = result.getBotpose();

                boolean align = false;

                telemetry.addLine("=== FIELD POS ===");
                telemetry.addData("X (m)", "%.3f", botpose.getPosition().x);
                telemetry.addData("Y (m)", "%.3f", botpose.getPosition().y);
                telemetry.addData("Yaw", "%.2f°", botpose.getOrientation().getYaw(AngleUnit.DEGREES));

                telemetry.addLine("=== TARGET DATA ===");
                telemetry.addData("tx", "%.2f°", result.getTx());
                telemetry.addData("ty", "%.2f°", result.getTy());

                if (gamepad2.b) {
                    align =!(align);
                };

                if (align) {
                    if (result.getTx() == 0 && result.getTy() == 0) {
                        telemetry.addData("Target Angle", "Straight Ahead");
                        align = false;
                    } else {

                        if (result.getTx() < 0) {
                            telemetry.addData("Target Angle", "Turn Left");

                            lf.setPower(0.5);
                            rf.setPower(-0.5);
                            rb.setPower(0.5);
                        } else {
                            telemetry.addData("Target Angle", "Turn Right");

                            lf.setPower(-0.5);
                            lb.setPower(0.5);
                            rf.setPower(0.5);
                        }

                        if (result.getTy() > 0) {
                            telemetry.addData("Vertical Alignment", "Move Down");

                            lf.setPower(-0.5);
                            lb.setPower(-0.5);
                        } else {
                            telemetry.addData("Vertical Alignment", "Move Up");

                            lf.setPower(0.5);
                            lb.setPower(0.5);
                        }
                    }
                }

                telemetry.addLine("=== TARGET RANGE ===");
                telemetry.addData("Target Area: ", result.getTa());
            } else {
                telemetry.addLine("BUNS DETECTED");
            }

            telemetry.update();
        }
        limelight.stop();
    }
}
