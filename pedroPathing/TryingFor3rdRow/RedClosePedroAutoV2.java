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

@Autonomous(name = "RED: Close Autonomous V2", group = "Autonomous", preselectTeleOp="DriveAndShooter")
@Configurable
public class RedClosePedroAutoV2 extends OpMode {
    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private Timer pathTimer;
    private int pathState;
    private Paths paths;

    private DcMotorEx leftFront, rightFront, leftBack, rightBack;
    private DcMotor intake, agitator;
    private DcMotorEx shooter;
    private Limelight3A limelight;

    final static double F = 13.5354;
    final static double P = 300.0;
    final static double MAX_WHEEL_VELOCITY = 2580.0;
    final int TARGET_TAG_ID = 24;

    double totalError = 0.0;
    double lastError = 0.0;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        leftFront = hardwareMap.get(DcMotorEx.class, "LF");
        rightFront = hardwareMap.get(DcMotorEx.class, "RF");
        leftBack = hardwareMap.get(DcMotorEx.class, "LB");
        rightBack = hardwareMap.get(DcMotorEx.class, "RB");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

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
        follower.setStartingPose(new Pose(122.696, 122.293, Math.toRadians(45)));

        paths = new Paths(follower);
        pathTimer = new Timer();
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
        panelsTelemetry.update(telemetry);
    }

    private boolean isAligningState() {
        return (pathState == 1 || pathState == 5 || pathState == 8 || pathState == 11) && !follower.isBusy();
    }

    public void autonomousPathUpdate() {
        double targetVelocity = getVelocityFromDistance() - 45.0;
        if (targetVelocity != 0) shooter.setVelocity(targetVelocity);

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
            case 0: // Start -> Initial Shooting Position
                follower.followPath(paths.ShootOneInitial, 1, true);
                setPathState(1);
                break;

            case 1: // Initial Shoot
                if (!follower.isBusy()) {
                    if (alignWithLimelight(tx, foundTag) || pathTimer.getElapsedTimeSeconds() > 1.5) {
                        stopDrive();
                        runAgitator();
                        if (pathTimer.getElapsedTimeSeconds() > 4.3) {
                            agitator.setPower(0);
                            intake.setPower(1);
                            follower.followPath(paths.AlignOne, 1, true);
                            setPathState(2);
                        }
                    }
                }
                break;

            case 2: // Align One
                if (!follower.isBusy()) {
                    follower.followPath(paths.CollectOne, 0.4, true);
                    setPathState(3);
                }
                break;

            case 3: // Collect One -> Tap Gate
                if (!follower.isBusy()) {
                    follower.followPath(paths.TapGate, 1, true);
                    setPathState(4);
                }
                break;

            case 4: // Post-Tap -> Shooting Position 1
                if (!follower.isBusy()) {
                    follower.followPath(paths.ShootOne, 1, true);
                    setPathState(5);
                }
                break;

            case 5: // Shooting One
                if (!follower.isBusy()) {
                    if (alignWithLimelight(tx, foundTag) || pathTimer.getElapsedTimeSeconds() > 1.5) {
                        stopDrive();
                        intake.setPower(0);
                        runAgitator();
                        if (pathTimer.getElapsedTimeSeconds() > 4.3) {
                            agitator.setPower(0);
                            intake.setPower(1);
                            follower.followPath(paths.AlignTwo, 1, true);
                            setPathState(6);
                        }
                    }
                }
                break;

            case 6: // Align Two -> Collect Two
                if (!follower.isBusy()) {
                    follower.followPath(paths.CollectTwo, 0.4, true);
                    setPathState(7);
                }
                break;

            case 7: // Shoot Two Position
                if (!follower.isBusy()) {
                    follower.followPath(paths.ShootTwo, 1, true);
                    setPathState(8);
                }
                break;

            case 8: // Shooting Two
                if (!follower.isBusy()) {
                    if (alignWithLimelight(tx, foundTag) || pathTimer.getElapsedTimeSeconds() > 1.5) {
                        stopDrive();
                        intake.setPower(0);
                        runAgitator();
                        if (pathTimer.getElapsedTimeSeconds() > 4.3) {
                            agitator.setPower(0);
                            intake.setPower(1);
                            follower.followPath(paths.AlignThree, 1, true);
                            setPathState(9);
                        }
                    }
                }
                break;

            case 9: // Align Three -> Collect Three
                if (!follower.isBusy()) {
                    follower.followPath(paths.CollectThree, 0.4, true);
                    setPathState(10);
                }
                break;

            case 10: // Shoot Three Position
                if (!follower.isBusy()) {
                    follower.followPath(paths.ShootThree, 1, true);
                    setPathState(11);
                }
                break;

            case 11: // Shooting Three
                if (!follower.isBusy()) {
                    if (alignWithLimelight(tx, foundTag) || pathTimer.getElapsedTimeSeconds() > 1.5) {
                        stopDrive();
                        intake.setPower(0);
                        runAgitator();
                        if (pathTimer.getElapsedTimeSeconds() > 4.3) {
                            agitator.setPower(0);
                            follower.followPath(paths.Leave, 1, true);
                            setPathState(12);
                        }
                    }
                }
                break;
        }
    }

    private void runAgitator() {
        double time = pathTimer.getElapsedTimeSeconds();
        if (time % 0.7 < 0.35) agitator.setPower(1);
        else agitator.setPower(0);
    }

    public boolean alignWithLimelight(double tx, boolean foundTag) {
        if (foundTag && Math.abs(tx) > 1.0) {
            double turnPower = GetPerfectTurn(tx);
            double leftV = turnPower * MAX_WHEEL_VELOCITY;
            double rightV = -turnPower * MAX_WHEEL_VELOCITY;
            leftFront.setVelocity(leftV); leftBack.setVelocity(leftV);
            rightFront.setVelocity(rightV); rightBack.setVelocity(rightV);
            return false;
        }
        return true;
    }

    public void stopDrive() {
        leftFront.setVelocity(0); leftBack.setVelocity(0);
        rightFront.setVelocity(0); rightBack.setVelocity(0);
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
        totalError = 0; lastError = 0;
    }

    public double getDistanceFromAprilTag() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double ty = result.getTy();
            return (71.12 - 23.0) / Math.tan(Math.toRadians(20.0 + ty));
        }
        return 0.0;
    }

    public int getVelocityFromDistance() {
        double dist = getDistanceFromAprilTag();
        return (dist != 0.0) ? (int) ((1.75534 * dist) + 1242.90722) : 1412;
    }

    public double GetPerfectTurn(double xOffset) {
        double kP = 0.06, kI = 0.00001, kD = 0.01;
        totalError += xOffset;
        double power = (kP * xOffset) + (kI * totalError) + (kD * (xOffset - lastError));
        lastError = xOffset;
        return power;
    }

    public static class Paths {
        public PathChain ShootOneInitial, AlignOne, CollectOne, TapGate, ShootOne, AlignTwo, CollectTwo, ShootTwo, AlignThree, CollectThree, ShootThree, Leave;

        public Paths(Follower follower) {
            ShootOneInitial = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(122.696, 122.293), new Pose(91.888, 91.626)))
                    .setConstantHeadingInterpolation(Math.toRadians(45))
                    .build();

            AlignOne = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(91.888, 91.626), new Pose(91.645, 58.645)))
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                    .build();

            CollectOne = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(91.645, 58.645), new Pose(124.972, 58.869)))
                    .setConstantHeadingInterpolation(Math.toRadians(0))
                    .build();

            TapGate = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(124.972, 58.869), new Pose(128.224, 70.383)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            ShootOne = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(128.224, 70.383), new Pose(72.150, 71.439)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(45))
                    .build();

            AlignTwo = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(72.150, 71.439), new Pose(92.168, 83.533)))
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                    .build();

            CollectTwo = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(92.168, 83.533), new Pose(125.327, 82.412)))
                    .setConstantHeadingInterpolation(Math.toRadians(0))
                    .build();

            ShootTwo = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(125.327, 82.412), new Pose(92.112, 91.626)))
                    .setConstantHeadingInterpolation(Math.toRadians(45))
                    .build();

            AlignThree = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(92.112, 91.626), new Pose(92.131, 34.243)))
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                    .build();

            CollectThree = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(92.131, 34.243), new Pose(125.243, 34.019)))
                    .setConstantHeadingInterpolation(Math.toRadians(0))
                    .build();

            ShootThree = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(125.243, 34.019), new Pose(92.112, 91.626)))
                    .setConstantHeadingInterpolation(Math.toRadians(45))
                    .build();

            Leave = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(92.112, 91.626), new Pose(95.841, 71.869)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                    .build();
        }
    }
}