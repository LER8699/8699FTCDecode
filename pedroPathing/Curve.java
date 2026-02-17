public static class Paths {
    public PathChain Curve;

    public Paths(Follower follower) {
      Curve = follower.pathBuilder()
          .addPath(
            new BezierCurve(
              new Pose(56.000, 8.000),
            new Pose(95.726, 39.430),
            new Pose(72.328, 71.603)
            )
          )
          .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
          .build();
    }
  }
