package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutoDriveAndShoot")
public class SimpleAutonomous extends LinearOpMode {

    private DcMotor leftFront, rightFront, leftBack, rightBack;
    private DcMotorEx shooter;
    private DcMotor agitator;

    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "LF");
        rightFront = hardwareMap.get(DcMotor.class, "RF");
        leftBack = hardwareMap.get(DcMotor.class, "LB");
        rightBack = hardwareMap.get(DcMotor.class, "RB");

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotor.class, "agitator");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        telemetry.log().setCapacity(25);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        if (opModeIsActive()) {

            drive(0.5, 0.5, 0);
            sleep(1000);

            stopRobot();
            sleep(200);

            drive(0, 0, -0.8);
            sleep(800);

            stopRobot();
            sleep(200);

            shooter.setVelocity(2000);
            
            for (int i = 1; i <= 3; i++) {
                long recoveryStart = System.currentTimeMillis();
                while (opModeIsActive() && System.currentTimeMillis() - recoveryStart < 1500) {
                    printShooterStats(i);
                }

                telemetry.addData("Action", "Firing Ball " + i);
                telemetry.update();
                
                agitator.setPower(0.5);
                sleep(800); 
                agitator.setPower(0);
            }
            
            stopRobot();
            shooter.setPower(0);
            agitator.setPower(0);
        }
    }

    public void drive(double y, double x, double rx) {
        leftFront.setPower(y + x + rx);
        leftBack.setPower(y - x + rx);
        rightFront.setPower(y - x - rx);
        rightBack.setPower(y + x - rx);
    }
    
    private void printShooterStats(int ballNum) {
        String stats = String.format("Ball %d | Vel: %.0f | Pwr: %.2f", 
                                      ballNum, 
                                      shooter.getVelocity(), 
                                      shooter.getPower());
        
        telemetry.log().add(stats);
        
        telemetry.addData("CURRENT Ball", ballNum);
        telemetry.addData("CURRENT Velocity", shooter.getVelocity());
        telemetry.update();
    }

    public void stopRobot() {
        drive(0, 0, 0);
    }
}
