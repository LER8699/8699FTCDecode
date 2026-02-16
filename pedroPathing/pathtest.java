public static class Paths {
    public PathChain Path1;

    public Paths(Follower follower) {
      Path1 = follower.pathBuilder()
          .addPath(
            new BezierLine(
              new Pose(72.554, 22.967),
            new Pose(71.874, 71.603)
            )
          )
          .setTangentHeadingInterpolation()
          .build();
    }
  }
