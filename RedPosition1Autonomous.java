package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "RedPos2Auto")
public class RedPosition1Autonomous extends LinearOpMode {

    private DcMotor leftFront, rightFront, leftBack, rightBack;
    private DcMotor shooter, agitator;

    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "LF");
        rightFront = hardwareMap.get(DcMotor.class, "RF");
        leftBack = hardwareMap.get(DcMotor.class, "LB");
        rightBack = hardwareMap.get(DcMotor.class, "RB");

        shooter = hardwareMap.get(DcMotor.class, "shooter");
        agitator = hardwareMap.get(DcMotor.class, "agitator");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            drive(0.5, 0.65, 0);
            sleep(1000);

            stopRobot();
            sleep(200);

            drive(0, 0, -0.725);
            sleep(800);

            stopRobot();
            sleep(200);

            shooter.setPower(0.7);
            
            sleep(3000);

            for (int i = 1; i <= 3; i++) {
                telemetry.addData("Action", "Firing Ball " + i);
                telemetry.update();

                agitator.setPower(0.5);
                sleep(800);
                agitator.setPower(0);
                sleep(3250);
                
            }
            
            sleep(500);
            
            drive(0, 1, 0);
            
            sleep(500);
            
            shooter.setPower(0);
            agitator.setPower(0);
            
            stopRobot();
        }
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
