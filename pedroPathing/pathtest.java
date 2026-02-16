public static class Paths {
    public PathChain Path1;

    public Paths(Follower follower) {
      Path1 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(38.539, 33.625),
            new Pose(105.209, 33.279)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(null), Math.toRadians(null))
          .build();
    }
  }
