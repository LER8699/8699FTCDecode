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
        initAgitator = hardwareMap.get(DcMotorEx.class, "agitator");

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
            
            //Drive slightly LEFT and forward (was right)
            drive(0.07, -0.35, 0); 
            sleep(1000);

            stopRobot();
            sleep(200);
            
            //Turn to face depot (Positive turn for Red)
            drive(0, 0, 0.155); 
            sleep(800);

            stopRobot();
            sleep(200);
            
            // Go forward and turn slightly
            drive(0.2, 0.0, 0.025); 
            sleep(850);
            stopRobot();

            shooter.setVelocity(SHOOTER_TARGET);
            sleep(3000);
            
            // Shoot
            for (int i = 1; i <= 3; i++) {
                shoot(i);
            }

            sleep(300);
            
            agitator.setVelocity(0);
            stopRobot();
            
            //Rotate to face loading zone (Positive turn for Red)
            drive(0, 0, 0.45); 
            sleep(700);

            stopRobot();
            shooter.setVelocity(0);

            //Strafe to the RIGHT (Negative X)
            sleep(300);
            drive(0, -0.3, 0); 
            sleep(1300);
            
            stopRobot();
            
            // Start intake
            intake.setPower(1);
            
            //Drive towards nearest row
            sleep(300);
            drive(0.3, 0, 0);
            sleep(3500);
            
            stopRobot();
            intake.setPower(0);
            
            // Backs up from the row
            sleep(100);
            drive(-0.3, 0, 0);
            sleep(3000);
            
            stopRobot();
            
            //Rotate back (Negative turn for Red)
            sleep(100);
            drive(0, 0, -0.45);
            sleep(700);
            
            stopRobot();
            
            //Back up and slightly rotate
            sleep(100);
            drive(-0.25, 0.0, 0.025);
            sleep(850);
            
            stopRobot();
        
            shooter.setVelocity(SHOOTER_TARGET);
            sleep(3000);
            
            for (int i = 1; i <= 3; i++) {
                shoot(i);
            }

            sleep(300);
            agitator.setVelocity(0);
            stopRobot();
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
