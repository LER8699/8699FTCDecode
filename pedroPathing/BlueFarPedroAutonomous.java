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
        follower.setStartingPose(new Pose(56.01259842519684, 6.122834645669277, Math.toRadians(90)));

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
    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;

 public static class Paths {
    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;

    public Paths(Follower follower) {
      Path1 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(56.013, 6.123),
            new Pose(56.227, 6.746)
            )
          )
          .setConstantHeadingInterpolation(Math.toRadians(90))
          .build();

      Path2 = follower.pathBuilder()
          .addPath(
            new BezierCurve(
              new Pose(56.227, 6.746),
            new Pose(58.507, 10.431),
            new Pose(57.994, 10.859),
            new Pose(58.392, 11.786),
            new Pose(58.790, 12.713),
            new Pose(59.188, 13.640),
            new Pose(59.587, 14.567),
            new Pose(59.985, 15.493),
            new Pose(60.383, 16.420),
            new Pose(59.194, 17.347),
            new Pose(60.499, 18.274)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(120))
          .build();

      Path3 = follower.pathBuilder()
          .addPath(
            new BezierCurve(
              new Pose(60.499, 18.274),
            new Pose(46.535, 38.582),
            new Pose(18.257, 36.309)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      Path4 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(18.257, 36.309),
            new Pose(60.397, 18.381)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(120))
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
