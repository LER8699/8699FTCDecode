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

@Autonomous(name = "BLUE: PedroPathing Far Autonomous", group = "Autonomous")
@Configurable // Panels
public class BlueFarPedroAutonomous extends OpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private Timer pathTimer, actionTimer, opmodeTimer; // Pedro Pathing Timers
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class


    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(55.3196850, 9.36062992, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths

        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        follower.update(); // Update Pedro Pathing
        autonomousPathUpdate(); // Update autonomous state machine

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }

public static class Paths {
    public PathChain InitialShot;
    public PathChain Path2;
    public PathChain CollectOne;
    public PathChain ShootOne;
    public PathChain Path5;
    public PathChain CollectTwo;
    public PathChain ShootTwo;
    public PathChain Path8;
    public PathChain CollectThree;
    public PathChain ShootThree;

    public Paths(Follower follower) {
      InitialShot = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(56.000, 8.000),
            new Pose(64.844, 17.178)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(115.5))
          .build();

      Path2 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(64.844, 17.178),
            new Pose(64.677, 36.009)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(115.5), Math.toRadians(180))
          .build();

      CollectOne = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(64.677, 36.009),
            new Pose(12.165, 36.244)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      ShootOne = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(12.165, 36.244),
            new Pose(64.918, 17.282)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(115.5))
          .build();

      Path5 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(64.918, 17.282),
            new Pose(64.176, 59.187)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(115.5), Math.toRadians(180))
          .build();

      CollectTwo = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(64.176, 59.187),
            new Pose(15.973, 59.724)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      ShootTwo = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(15.973, 59.724),
            new Pose(64.978, 17.031)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(115.5))
          .build();

      Path8 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(64.978, 17.031),
            new Pose(63.521, 83.200)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(115.5), Math.toRadians(180))
          .build();

      CollectThree = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(63.521, 83.200),
            new Pose(18.238, 83.104)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      ShootThree = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(18.238, 83.104),
            new Pose(65.027, 17.517)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(115.5))
          .build();
    }
  }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(paths.Path1);
                setPathState(1);
                break;
            case 1:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Preload */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                    follower.followPath(paths.Path2);
                    setPathState(2);
                }
                break;
            case 2:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup1Pose's position */
                if (!follower.isBusy()) {
                    /* Grab Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                    follower.followPath(paths.Path3);
                    setPathState(3);
                }
                break;
            case 3:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                    follower.followPath(paths.Path4);
                    setPathState(4);
                }
                break;
        }
    }

    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }
}
