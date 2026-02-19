package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;

@Autonomous(name = "BLUE: Close Autonomous", group = "Autonomous")
@Configurable
public class BlueClosePedroAuto extends OpMode {
    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private Timer pathTimer;
    private int pathState;
    private Paths paths;

    // Direct Motor Control
    private DcMotorEx leftFront, rightFront, leftBack, rightBack;
    private DcMotor intake, agitator;
    private DcMotorEx shooter;
    private Limelight3A limelight;

    // Shooter & Alignment Constants
    final static double F = 13.5354;
    final static double P = 300.0;
    final static double MAX_WHEEL_VELOCITY = 2580.0; // Ticks per second
    final int TARGET_TAG_ID = 20; // Blue goal tag

    double totalError = 0.0;
    double lastError = 0.0;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Drive Motors Mapping
        leftFront = hardwareMap.get(DcMotorEx.class, "LF");
        rightFront = hardwareMap.get(DcMotorEx.class, "RF");
        leftBack = hardwareMap.get(DcMotorEx.class, "LB");
        rightBack = hardwareMap.get(DcMotorEx.class, "RB");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Peripheral Mapping
        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotor.class, "agitator");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        intake.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(P, 0, 0, F));

        limelight.pipelineSwitch(0);
        limelight.start();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(21.3, 122.2, Math.toRadians(135)));

        paths = new Paths(follower);
        pathTimer = new Timer();

        panelsTelemetry.debug("Status", "Initialized with Direct Drive Control");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        if (isAligningState()) {
            follower.updatePose();
        } else {
            follower.update();
        }

        autonomousPathUpdate();

        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("Shooter Velocity", shooter.getVelocity());
        panelsTelemetry.update(telemetry);
    }

    private boolean isAligningState() {
        return (pathState == 1 || pathState == 4 || pathState == 7) && !follower.isBusy();
    }

    public void autonomousPathUpdate() {
        double targetVelocity = getVelocityFromDistance() - 45.0;
        if (targetVelocity != 0) {
            shooter.setVelocity(targetVelocity);
        }

        LLResult result = limelight.getLatestResult();
        double tx = 0;
        boolean foundTag = false;
        if (result != null && result.isValid()) {
            for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                if (tag.getFiducialId() == TARGET_TAG_ID) {
                    tx = tag.getTargetXDegrees();
                    foundTag = true;
                }
            }
        }

        switch (pathState) {
            case 0:
                // Starting movement with adjusted power/hold
                follower.followPath(paths.ShootInitial, 0.5, true);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    if (alignWithLimelight(tx, foundTag) || pathTimer.getElapsedTimeSeconds() > 2.0) {
                        stopDrive();

                        double time = pathTimer.getElapsedTimeSeconds();
                        // 0.7s cycle: 0.35s open, 0.35s closed
                        if (time % 0.7 < 0.35) {
                            agitator.setPower(1);
                        } else {
                            agitator.setPower(0);
                        }

                        // Exit after 3 pulses + align buffer
                        if (time > 4.8) {
                            agitator.setPower(0);
                            intake.setPower(1);
                            follower.followPath(paths.AlignOne, 0.5, true);
                            setPathState(2);
                        }
                    }
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(paths.CollectOne, 0.5, true);
                    setPathState(3);
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    // Holding path at 0.5 power to ensure stability before shooting
                    follower.followPath(paths.ShootOne, 0.5, true);
                    setPathState(4);
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    if (alignWithLimelight(tx, foundTag) || pathTimer.getElapsedTimeSeconds() > 2.0) {
                        stopDrive();
                        intake.setPower(0);

                        double time = pathTimer.getElapsedTimeSeconds();
                        if (time % 0.7 < 0.35) {
                            agitator.setPower(1);
                        } else {
                            agitator.setPower(0);
                        }

                        if (time > 4.8) {
                            agitator.setPower(0);
                            intake.setPower(1);
                            follower.followPath(paths.AlignTwo, 0.5, true);
                            setPathState(5);
                        }
                    }
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    follower.followPath(paths.CollectTwo, 0.5, true);
                    setPathState(6);
                }
                break;

            case 6:
                if (!follower.isBusy()) {
                    follower.followPath(paths.ShootTwo, 0.5, true);
                    setPathState(7);
                }
                break;

            case 7:
                if (!follower.isBusy()) {
                    if (alignWithLimelight(tx, foundTag) || pathTimer.getElapsedTimeSeconds() > 2.5) {
                        stopDrive();
                        intake.setPower(0);

                        double time = pathTimer.getElapsedTimeSeconds();
                        if (time % 0.7 < 0.35) {
                            agitator.setPower(1);
                        } else {
                            agitator.setPower(0);
                        }

                        if (time > 5.3) {
                            agitator.setPower(0);
                            follower.followPath(paths.Leave, 1, true);
                            setPathState(8);
                        }
                    }
                }
                break;
        }
    }

    public boolean alignWithLimelight(double tx, boolean foundTag) {
        if (foundTag && Math.abs(tx) > 1.0) {
            double turnPower = GetPefectTurn(tx);
            double leftV = turnPower * MAX_WHEEL_VELOCITY;
            double rightV = -turnPower * MAX_WHEEL_VELOCITY;

            leftFront.setVelocity(leftV);
            leftBack.setVelocity(leftV);
            rightFront.setVelocity(rightV);
            rightBack.setVelocity(rightV);
            return false;
        }
        return true;
    }

    public void stopDrive() {
        leftFront.setVelocity(0);
        leftBack.setVelocity(0);
        rightFront.setVelocity(0);
        rightBack.setVelocity(0);
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
        totalError = 0;
        lastError = 0;
    }

    public double getDistanceFromAprilTag() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double ty = result.getTy();
            double mountAngle = 20.0;
            double lensHeight = 23.0;
            double tagHeight = 71.12;
            return (tagHeight - lensHeight) / Math.tan(Math.toRadians(mountAngle + ty));
        }
        return 0.0;
    }

    public int getVelocityFromDistance() {
        double dist = getDistanceFromAprilTag();
        if (dist != 0.0) {
            return (int) ((1.75534 * dist) + 1242.90722);
        }
        return 1412;
    }

    public double GetPefectTurn(double xOffset) {
        double kP = 0.045;
        double kI = 0.00001;
        double kD = 0.;
        double error = xOffset;
        totalError += error;
        double power = (kP * error) + (kI * totalError) + (kD * (error - lastError));
        lastError = error;
        return power;
    }

    public static class Paths {
        public PathChain ShootInitial, AlignOne, CollectOne, ShootOne, AlignTwo, CollectTwo, ShootTwo, Leave;

        public Paths(Follower follower) {
            ShootInitial = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(21.304, 122.293),new Pose(51.000, 92.000)))
                    .setConstantHeadingInterpolation(Math.toRadians(135))
                    .build();

            AlignOne = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(51.000, 92.000),new Pose(51.049, 83.293)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            CollectOne = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(51.049, 83.293),new Pose(19.1, 82.765)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            ShootOne = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(19.1, 82.765),new Pose(51.000, 92.000)))
                    .setConstantHeadingInterpolation(Math.toRadians(140))
                    .build();

            AlignTwo = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(51.000, 92.000),new Pose(51.031, 59.776)))
                    .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
                    .build();

            CollectTwo = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(51.031, 59.776),new Pose(17.537, 59.723)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            ShootTwo = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(17.537, 59.723),new Pose(51.000, 92.000)))
                    .setConstantHeadingInterpolation(Math.toRadians(140))
                    .build();

            Leave = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(51.000, 92.000),new Pose(37.858, 77.863)))
                    .setTangentHeadingInterpolation()
                    .build();
        }
    }
}