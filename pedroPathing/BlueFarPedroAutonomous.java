public static class Paths {
    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;

    public Paths(Follower follower) {
      Path1 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(58.280, 7.710),
            new Pose(58.268, 8.107)
            )
          )
          .setConstantHeadingInterpolation(Math.toRadians(90))
          .build();

      Path2 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(58.268, 8.107),
            new Pose(62.087, 18.501)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(115))
          .build();

      Path3 = follower.pathBuilder()
          .addPath(
            new BezierCurve(
              new Pose(62.087, 18.501),
            new Pose(46.542, 38.589),
            new Pose(17.123, 35.175)
            )
          )
          .setTangentHeadingInterpolation()
          .build();

      Path4 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(17.123, 35.175),
            new Pose(62.211, 18.608)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(115))
          .build();
    }
  }
