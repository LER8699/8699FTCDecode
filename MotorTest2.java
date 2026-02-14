package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Motor ID Tester", group="Test")
public class MotorTest2 extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Press Play to start motor test.");
        telemetry.update();

        waitForStart();
        runtime.reset();

            while (opModeIsActive()) {
    
            DcMotor motor1 = null;
            String motorName = null;
            String[] motors = {"LF", "RF", "LB", "RB"};
    
            if (gamepad1.aWasPressed()) {
                motorName = motors[1];
                }

            if (gamepad1.bWasPressed()) {
                motorName = motors[2];
            }

            if (gamepad1.xWasPressed()) {
                motorName = motors[3];
            }

            if (gamepad1.yWasPressed()) {
                motorName = motors[4];
            }
                try {
                    motor1 = hardwareMap.get(DcMotor.class, motorName);

                    // Announce which motor is running
                    telemetry.addData("Motor: ", "Running: " + motorName);
                    telemetry.update();

                    motor1.setPower(1.0);

                    sleep(2000);

                    motor1.setPower(0.0);

                    sleep(500);

                } catch (Exception e) {
                    telemetry.addData("FAILURE", "Motor " + motorName + " not found.");
                    telemetry.update();
                    sleep(500);
                }
            }

        telemetry.addData("Status", "Test Complete. Waiting for Stop.");
        telemetry.update();
        while(opModeIsActive()) {
            idle();
        }
    }
}
