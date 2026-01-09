package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Motor ID Tester", group="Test")
public class TestMotors extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Press Play to start motor test.");
        telemetry.update();

        waitForStart();
        runtime.reset();

        if (opModeIsActive()) {
            for (int i = 1; i <= 8; i++) {
                if (!opModeIsActive()) break;

                DcMotor motor1 = null;
                String motorName = "motor" + i;

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
        }

        telemetry.addData("Status", "Test Complete. Waiting for Stop.");
        telemetry.update();
        while(opModeIsActive()) {
            idle();
        }
    }
}