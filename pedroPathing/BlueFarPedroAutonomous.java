package org.firstinspires.ftc.teamcode;
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
    follower.setStartingPose(new Pose(72, 8, Math.toRadians(90)));

    paths = new Paths(follower); // Build paths

    panelsTelemetry.debug("Status", "Initialized");
    panelsTelemetry.update(telemetry);
  }

  @Override
  public void loop() {
    follower.update(); // Update Pedro Pathing
    pathState = autonomousPathUpdate(); // Update autonomous state machine

    // Log values to Panels and Driver Station
    panelsTelemetry.debug("Path State", pathState);
    panelsTelemetry.debug("X", follower.getPose().getX());
    panelsTelemetry.debug("Y", follower.getPose().getY());
    panelsTelemetry.debug("Heading", follower.getPose().getHeading());
    panelsTelemetry.update(telemetry);
  }

  public static class Paths {
    public PathChain Path5;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;
    public PathChain Path5;
    public PathChain Path6;
    public PathChain Path7;
    public PathChain Path8;

    public Paths(Follower follower) {
      Path5 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(56.000, 8.000),
            new Pose(60.000, 16.413)
            )
          )
          .setConstantHeadingInterpolation(Math.toRadians(115))
          .build();

      Path2 = follower.pathBuilder()
          .addPath(
            new BezierCurve(
              new Pose(60.000, 16.413),
            new Pose(54.961, 37.114),
            new Pose(15.186, 36.376)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      Path3 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(15.186, 36.376),
            new Pose(60.339, 16.069)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(115))
          .build();

      Path4 = follower.pathBuilder()
          .addPath(
            new BezierCurve(
              new Pose(60.339, 16.069),
            new Pose(57.765, 60.957),
            new Pose(13.384, 59.025)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      Path5 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(13.384, 59.025),
            new Pose(60.498, 15.718)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(110))
          .build();

      Path6 = follower.pathBuilder()
          .addPath(
            new BezierCurve(
              new Pose(60.498, 15.718),
            new Pose(54.425, 88.894),
            new Pose(17.915, 84.833)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      Path7 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(17.915, 84.833),
            new Pose(59.353, 15.998)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(110))
          .build();

      Path8 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(59.353, 15.998),
            new Pose(47.781, 27.507)
            )
          )
          .setTangentHeadingInterpolation()
          .build();
    }
  }

public void autonomousPathUpdate() {
    switch (pathState) {
        case 0:
            follower.followPath(scorePreload);
            setPathState(1);
            break;
        case 1:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Score Preload */
                /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                follower.followPath(grabPickup1,true);
                setPathState(2);
            }
            break;
        case 2:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup1Pose's position */
            if(!follower.isBusy()) {
                /* Grab Sample */
                /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                follower.followPath(scorePickup1,true);
                setPathState(3);
            }
            break;
        case 3:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Score Sample */
                /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                follower.followPath(grabPickup2,true);
                setPathState(4);
            }
            break;
        case 4:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup2Pose's position */
            if(!follower.isBusy()) {
                /* Grab Sample */
                /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                follower.followPath(scorePickup2,true);
                setPathState(5);
            }
            break;
        case 5:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Score Sample */
                /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                follower.followPath(grabPickup3,true);
                setPathState(6);
            }
            break;
        case 6:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup3Pose's position */
            if(!follower.isBusy()) {
                /* Grab Sample */
                /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                follower.followPath(scorePickup3, true);
                setPathState(7);
            }
            break;
        case 7:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                setPathState(-1);
            }
            break;
    }
}

/** These change the states of the paths and actions. It will also reset the timers of the individual switches **/
public void setPathState(int pState) {
    pathState = pState;
    pathTimer.resetTimer();
}

/** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {
        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }
    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
    }
