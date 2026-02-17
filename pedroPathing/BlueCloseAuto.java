package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

@Autonomous(name = "BLUE: Close Autonomous", group = "Autonomous")
@Configurable
public class BlueClosePedroAuto extends OpMode {
    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private Timer pathTimer, actionTimer;
    private int pathState;
    private Paths paths;

    private DcMotor intake;
    private DcMotorEx shooter;
    private DcMotor agitator;
    private Limelight3A limelight;

    // Shooter Constants
    final static double F = 13.5354;
    final static double P = 300.0;
    final static double SHOOTER_TARGET = 1412.0;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Hardware Mapping
        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        agitator = hardwareMap.get(DcMotor.class, "agitator");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Motor Directions and Modes
        intake.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.REVERSE);
        agitator.setDirection(DcMotor.Direction.REVERSE);

        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        // Limelight Setup
        limelight.pipelineSwitch(0);
        limelight.start();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(21.3, 122.2, Math.toRadians(135)));

        paths = new Paths(follower);
        pathTimer = new Timer();
        actionTimer = new Timer();

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("Shooter Velocity", shooter.getVelocity());
        panelsTelemetry.update(telemetry);
    }

    // Path definitions remain the same
    public static class Paths {
        public PathChain ShootInitial, CollectOne, ShootOne, AlignTwo, CollectTwo, ShootTwo, Leave;
        public Paths(Follower follower) {
            ShootInital = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(21.3, 122.2), new Pose(58.948, 84.529)))
                    .setConstantHeadingInterpolation(Math.toRadians(135))
                    .build();

      CollectOne = follower.pathBuilder()
          .addPath(new BezierLine(new Pose(58.948, 84.529), new Pose(19.301, 83.973)))
          .setTangentHeadingInterpolation()
          .build();

      ShootOne = follower.pathBuilder()
          .addPath(new BezierLine(new Pose(19.301, 83.973),new Pose(59.855, 84.076)))
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
          .build();

      AlignTwo = follower.pathBuilder()
          .addPath(new BezierLine(new Pose(59.855, 84.076),new Pose(59.953, 59.573)))
          .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
          .build();

      CollectTwo = follower.pathBuilder()
          .addPath(new BezierLine(new Pose(59.953, 59.573),new Pose(19.301, 59.751)))
          .setTangentHeadingInterpolation()
          .build();

      ShootTwo = follower.pathBuilder()
          .addPath(new BezierLine(new Pose(19.301, 59.751),new Pose(59.855, 84.076)))
          .setConstantHeadingInterpolation(Math.toRadians(135))
          .build();

      Leave = follower.pathBuilder()
          .addPath(new BezierLine(new Pose(59.855, 84.076),new Pose(46.024, 70.178)))
          .setTangentHeadingInterpolation()
          .build();
        }
    }

    public void autonomousPathUpdate() {
        double targetVelocity = getVelocityFromDistance();

        if (targetVelocity != 0) {
            shooter.setVelocity(targetVelocity);
        }

        switch (pathState) {
            case 0:
                // Start warming up shooter immediately
                shooter.setVelocity(targetVelocity);
                follower.followPath(paths.ShootInital);
                setPathState(1);
                break;

            case 1:
                // Wait for path and enough time to pass
                if (!follower.isBusy() && !(pathTimer.getElapsedTimeSeconds() >= 3.5)) {
                    agitator.setPower(1);
                }

                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() >= 3.5) {
                    agitator.setPower(0);
                    intake.setPower(1);
                    follower.followPath(paths.CollectOne, 0.5, true);
                    setPathState(2);
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    intake.setPower(0);
                    follower.followPath(paths.ShootOne);
                    setPathState(3);
                }
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
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
        return 0;
    }
}
