package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "RedPos3Auto")
public class RedPosition3Autonomous extends LinearOpMode {

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

            drive(.07, -.3, 0);
            sleep(1000);

            stopRobot();
            sleep(200);

            drive(0, 0, 0.17);
            sleep(800);

            stopRobot();
            sleep(200);

            shooter.setPower(0.8);

            sleep(3000);

            for (int i = 1; i <= 3; i++) {
                telemetry.addData("Action", "Firing Ball " + i);
                telemetry.update();

                agitator.setPower(0.5);
                sleep(800);
                agitator.setPower(0);
                sleep(4000);
            }

            stopRobot();
            shooter.setPower(0);
            agitator.setPower(0);
            drive(.3, 0, 0);
            sleep(1000);
            
            
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
