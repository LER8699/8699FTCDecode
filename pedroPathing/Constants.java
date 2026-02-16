package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.ftc.localization.constants.ThreeWheelIMUConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            // weight in kg
            .mass(11)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.23, 0, 0.035, 0.02))
            .headingPIDFCoefficients(new PIDFCoefficients(2, 0, 0.01, 0.03))
            .forwardZeroPowerAcceleration(-51.767)
            .lateralZeroPowerAcceleration(-60.3197)
            .centripetalScaling(0.00335);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("RF")
            .rightRearMotorName("RB")
            .leftRearMotorName("LB")
            .leftFrontMotorName("LF")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(47.7349)
            .yVelocity(37.35259);

    // the 3 deadwheel + imu constraints when we get around to it
    public static ThreeWheelConstants localizerConstants = new ThreeWheelConstants()
            .forwardTicksToInches(0.00078)
            .strafeTicksToInches(0.000745)
            .turnTicksToInches(-0.00077)
            .leftPodY(1)
            .rightPodY(-1)
            .strafePodX(-2.5)
            .leftEncoder_HardwareMapName("agitator")
            .rightEncoder_HardwareMapName("intake")
            .strafeEncoder_HardwareMapName("centerDeadW")
            .leftEncoderDirection(Encoder.FORWARD)
            .rightEncoderDirection(Encoder.REVERSE)
            .strafeEncoderDirection(Encoder.REVERSE)

// when it comes to the encoder directions:t5
            //.leftEncoderDirection(Encoder.REVERSE)
            //.rightEncoderDirection(Encoder.FORWARD)
            /// / and/or:
            //.strafeEncoderDirection(Encoder.REVERSE)

            // Offset from center of robot (inches)
            .leftPodY(3.7814961)
            .rightPodY(-3.7814961)
            .strafePodX(-7.244);

    // .forwardTicksToInches(multiplier)
    // .strafeTicksToInches(multiplier)
    // .turnTicksToInches(multiplier)

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .threeWheelLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
