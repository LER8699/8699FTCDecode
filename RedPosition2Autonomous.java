package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "RedPos2Auto")
public class RedPosition2Autonomous extends LinearOpMode {

    private DcMotor leftFront, rightFront, leftBack, rightBack;
    private DcMotorEx shooter, agitator;
    
    // Shooter PIDF Constants
    final static double F = 13.5354;
    final static double P = 300.0;

    // Constants
    static final double SHOOTER_TARGET = 1412.0;
    static final double AGITATOR_TARGET = 447.4;

    @Override
    public void runOpMode() {
        // Initialization
        leftFront = hardwareMap.get(DcMotor.class, "LF");
        rightFront = hardwareMap.get(DcMotor.class, "RF");
        leftBack = hardwareMap.get(DcMotor.class, "LB");
        rightBack = hardwareMap.get(DcMotor.class, "RB");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotorEx.class, "agitator");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        agitator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // 1. Start shooter warmup immediately
            shooter.setVelocity(SHOOTER_TARGET);

            // 2. Drive to position
            drive(-0.38, 0, 0);
            sleep(3000);
            stopRobot();
            sleep(1000);

            drive(0, 0, -0.4);
            sleep(85);

            stopRobot();
            
            sleep(1000);

            // 3. Fire 3 balls using our new function
            for (int i = 1; i <= 3; i++) {
                shoot(i); 
            }
            
            sleep(300);
            
            // 4. Final movement
            drive(0, 0.65, 0);
            sleep(800);

            // Shutdown
            shooter.setVelocity(0);
            agitator.setVelocity(0);
            stopRobot();
        }
    }

    /**
     * Helper function to handle the firing sequence with PID recovery
     */
    public void shoot(int ballNumber) {
        // Wait for PID to recover speed if it's too slow
        while (opModeIsActive() && Math.abs(shooter.getVelocity() - SHOOTER_TARGET) > 40) {
            telemetry.addData("Status", "Recovering for Ball " + ballNumber);
            telemetry.addData("Current Velocity", "%.2f", shooter.getVelocity());
            telemetry.update();
        }

        telemetry.addData("Status", "FIRING BALL " + ballNumber);
        telemetry.update();

        // Run agitator to feed the ball
        agitator.setVelocity(AGITATOR_TARGET);
        sleep(1200);
        agitator.setVelocity(0);
    }

    public void drive(double y, double x, double rx) {
        leftFront.setPower(y + x + rx);
        leftBack.setPower(y - x + rx);
        rightFront.setPower(y - x - rx);
        rightBack.setPower(y + x - rx);
    }

    public void stopRobot() {
        drive(0, 0, 0);
    }
}
