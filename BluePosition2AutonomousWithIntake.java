package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "BluePos2Auto")
public class BluePosition2Autonomous extends LinearOpMode {

    private DcMotor leftFront, rightFront, leftBack, rightBack, intake;
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
        intake = hardwareMap.get(DcMotor.class, "intake");

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

        telemetry.addData("Status", "Blue Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // 1. Start shooter warmup immediately
            shooter.setVelocity(SHOOTER_TARGET);

            // 2. Drive to position (Forward/Backward stays the same)
            drive(-0.38, 0, 0);
            sleep(1750);
            stopRobot();
            sleep(1000);

            // Turn to face goal 
            drive(0, 0, -0.4);
            sleep(85);

            stopRobot();
            sleep(1000);

            // 3. Fire 3 balls
            for (int i = 1; i <= 3; i++) {
                shoot(i); 
            }
            
            sleep(300);
            
            // MIRRORED: Turn to face row (Flipped to Negative Turn)
            drive(0, 0, -0.4);
            sleep(85);
            
            stopRobot();
            
            drive(-0.3, 0.0, 0.0);
            sleep(800);
            
            stopRobot();
            
            // Rotate
            drive(0.0, 0.0, -0.295);
            sleep(800);
            
            stopRobot();
            
            intake.setPower(1);
            
            // Strafe 
            drive(0.0, -0.36, 0.0);
            sleep(800);
            
            stopRobot();
            
            // Drive towards row
            drive(0.25, 0, 0);
            sleep(3360);
            
            stopRobot();
            
            // Drive back from row
            drive(-0.25, 0, 0);
            sleep(3360);
            
            stopRobot();
            
            //Strafe back
            drive(0.0, 0.45, 0.0);
            sleep(800);
            
            stopRobot();
            
            intake.setPower(0);
            
            //Rotate back
            drive(0.0, 0.0, 0.42);
            sleep(800);
            
            stopRobot();
            
            // Slight turn adjustment
            drive(0, 0, -0.2);
            sleep(85);
            
            stopRobot();
            
            for (int i = 1; i <= 3; i++) {
                shoot(i); 
            }
            
            sleep(300);
            stopRobot();
            
            // 4. Final movement Strafe Left
            drive(0, -0.65, 0);
            sleep(800);

            // Shutdown
            shooter.setVelocity(0);
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
