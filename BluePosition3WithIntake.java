package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "BluePos3Auto")
public class BluePosition3Autonomous extends LinearOpMode {

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

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            
            //Drive slightly right and forward
            //drive(.07, 0.0, 0);
            //sleep(1000);

            //stopRobot();
            //sleep(200);
            
            //Turn to face depot
            shooter.setVelocity(SHOOTER_TARGET);
            drive(0, 0, -0.35);
            sleep(400);

            stopRobot();
            sleep(200);
            
            //Go forward to account for new gate and turn more
            drive(0.25, 0.0, -0.045);
            sleep(800);
            stopRobot();

            sleep(1000);
            
            //Shoot
            for (int i = 1; i <= 3; i++) {
                shoot(i);
            }

            sleep(300);
            

            agitator.setVelocity(0);
            stopRobot();
            
            //Rotate to face stright at the row
            drive(0, 0, -0.5);
            sleep(700);

            stopRobot();

            //Strafe to the right
            sleep(300);
            drive(0, 0.66, 0);
            sleep(650);
            
            stopRobot();
            
            //Start intake
            intake.setPower(1);
            
            //Drive towards nearest row
            sleep(300);
            drive(0.25, 0, 0);
            sleep(4200);
            
            stopRobot();
            
            //Backs up from the row
            sleep(100);
            drive(-0.3, 0, 0);
            sleep(3000);
            
            stopRobot();
            intake.setPower(0);
            
            //Rotate back to face depot
            sleep(100);
            drive(0, 0, 0.425);
            sleep(700);
            
            stopRobot();
            
            //Back up and slightly rotate
            sleep(100);
            drive(-0.425, 0.0, 0.07);
            sleep(850);
            
            stopRobot();

            sleep(3000);
            
            //Shoot
            for (int i = 1; i <= 3; i++) {
                shoot(i);
            }

            sleep(300);
            

            agitator.setVelocity(0);
            shooter.setVelocity(0);
            stopRobot();
            
            sleep(100);
            drive(0.8, 0, 0);
            sleep(200);
            
            stopRobot();
            shooter.setVelocity(0);
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
