package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SingleWheelTest {

    // Hardware
    private DcMotorEx deadWheel;

    // References to HardwareMap and Telemetry
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    // Calibration Constants (Adjust these!)
    private static final double TICKS_PER_REV = 8192; // REV Through Bore Encoder
    private static final double WHEEL_DIAMETER = 2; // 2 inches (35mm)
    private static final double TICKS_PER_INCH = TICKS_PER_REV / (WHEEL_DIAMETER * Math.PI);

    // Constructor
    public SingleWheelTest(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }

    // Initialize the dead wheel
    public void init() {
        // "agitator" must match the name you put in the Driver Hub config
        deadWheel = hardwareMap.get(DcMotorEx.class, "agitator");

        // Reset encoder to zero
        deadWheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        deadWheel.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addLine("Single Wheel Test Initialized");
        telemetry.update();
    }

    // Update telemetry with current readings
    public void updateTelemetry() {
        int currentTicks = deadWheel.getCurrentPosition();
        double inches = currentTicks / TICKS_PER_INCH;

        telemetry.addData("Raw Ticks", currentTicks);
        telemetry.addData("Inches Traveled", "%.2f", inches);
        telemetry.addLine("\n--- Instructions ---");
        telemetry.addLine("Push the robot 24 inches manually.");
        telemetry.addLine("Check if 'Inches Traveled' matches your ruler.");
    }

    // Get current position in ticks
    public int getCurrentTicks() {
        return deadWheel.getCurrentPosition();
    }

    // Get current position in inches
    public double getInches() {
        return getCurrentTicks() / TICKS_PER_INCH;
    }

    // Reset the encoder
    public void resetEncoder() {
        deadWheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        deadWheel.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }
}
