public static class Paths {
    public PathChain Path1;
    public PathChain Path2;

    public Paths(Follower follower) {
      Path1 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(56.000, 8.000),
            new Pose(72.781, 71.603)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
          .build();

      Path2 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(72.781, 71.603),
            new Pose(56.301, 7.446)
            )
          )
          .setTangentHeadingInterpolation()
          .build();
    }
  }
