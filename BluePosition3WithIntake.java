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
            drive(.07, .35, 0);
            sleep(1000);

            stopRobot();
            sleep(200);
            
            //Turn to face depot
            drive(0, 0, -0.155);
            sleep(800);

            stopRobot();
            sleep(200);
            
            //Go forward to account for new gate and turn more
            drive(0.2, 0.0, -0.025);
            sleep(850);
            stopRobot();

            shooter.setVelocity(SHOOTER_TARGET);

            sleep(3000);
            
            //Shoot
            for (int i = 1; i <= 3; i++) {
                shoot(i);
            }

            sleep(300);
            

            agitator.setVelocity(0);
            stopRobot();
            
            //Rotate to face stright at the loading zone
            drive(0, 0, -0.45);
            sleep(800);

            stopRobot();

            shooter.setVelocity(0);

            //Drive towards loading zone
            sleep(300);
            drive(.55, 0, 0);
            sleep(500);
            
            //Start intake
            intake.setPower(1);
            
            sleep(1300);
            
            stopRobot();
            
            sleep(300);
            
            //While intaking artifacts rotate and strafe to collect both
            drive(0.15, -0.1, -0.2);
            
            sleep(1500);
            
            stopRobot();
            
            sleep(650);
            
            intake.setPower(0);
            
            sleep(300);
            drive(-0.25, 0, 0);
            sleep(700);
            
            drive(0, 0, 0.45);
            sleep(600);
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
