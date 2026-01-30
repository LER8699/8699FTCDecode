package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "RedPos3Auto")
public class RedPosition3Autonomous extends LinearOpMode {

    private DcMotor leftFront, rightFront, leftBack, rightBack, intake;
    private DcMotorEx shooter, agitator;

    // Shooter PIDF Constants
    final static double F = 13.5354;
    final static double P = 300.0;

    static final double SHOOTER_TARGET = 1800.0;
    static final double AGITATOR_TARGET = 447.4;

    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "LF");
        rightFront = hardwareMap.get(DcMotor.class, "RF");
        leftBack = hardwareMap.get(DcMotor.class, "LB");
        rightBack = hardwareMap.get(DcMotor.class, "RB");
        intake = hardwareMap.get(DcMotor.class, "intake");

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotorEx.class, "agitator");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        telemetry.addData("Status", "Red Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            
            // Start Shooter and Turn to face depot (Flipped to Positive Turn)
            shooter.setVelocity(SHOOTER_TARGET);
            drive(0, 0, 0.35); 
            sleep(400);

            stopRobot();
            sleep(200);
            
            // Go forward and turn more (Flipped to Positive Turn)
            drive(0.25, 0.0, 0.045); 
            sleep(800);
            stopRobot();

            sleep(1000);
            
            // Shoot
            for (int i = 1; i <= 3; i++) {
                shoot(i);
            }

            sleep(300);
            
            agitator.setVelocity(0);
            stopRobot();
            
            // Rotate to face straight at the row (Flipped to Positive Turn)
            drive(0, 0, 0.5); 
            sleep(700);

            stopRobot();

            // Strafe to the LEFT (Flipped from 0.66 to -0.66)
            sleep(300);
            drive(0, -0.66, 0); 
            sleep(650);
            
            stopRobot();
            
            // Start intake
            intake.setPower(1);
            
            // Drive towards nearest row (Forward Y remains positive)
            sleep(300);
            drive(0.25, 0, 0);
            sleep(4200);
            
            stopRobot();
            
            // Backs up from the row
            sleep(100);
            drive(-0.3, 0, 0);
            sleep(3000);
            
            stopRobot();
            intake.setPower(0);
            
            // Rotate back to face depot (Flipped to Negative Turn)
            sleep(100);
            drive(0, 0, -0.425); 
            sleep(700);
            
            stopRobot();
            
            // Back up and slightly rotate (Flipped turn from 0.07 to -0.07)
            sleep(100);
            drive(-0.425, 0.0, -0.07); 
            sleep(850);
            
            stopRobot();

            sleep(3000);
            
            // Shoot again
            for (int i = 1; i <= 3; i++) {
                shoot(i);
            }

            sleep(300);
            
            agitator.setVelocity(0);
            shooter.setVelocity(0);
            stopRobot();
            
            // Final nudge
            sleep(100);
            drive(0.8, 0, 0);
            sleep(200);
            
            stopRobot();
            shooter.setVelocity(0);
        }
    }

    public void shoot(int ballNumber) {
        while (opModeIsActive() && Math.abs(shooter.getVelocity() - SHOOTER_TARGET) > 40) {
            telemetry.addData("Status", "Recovering for Ball " + ballNumber);
            telemetry.update();
        }

        agitator.setVelocity(AGITATOR_TARGET);
        sleep(1400);
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
