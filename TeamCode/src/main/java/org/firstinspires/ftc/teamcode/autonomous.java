package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

public class autonomous extends LinearOpMode {
    Servo elbow;
    Servo claw;
    static final double ELBOW_DOWN_POS = 0.85;
    static final double ELBOW_UP_POS = 0.3;
    static final double CLAW_CLOSED_POS = 0.6;
    static final double CLAW_OPEN_POS = 0.2;


    @Override
    public void runOpMode() {
        elbow = hardwareMap.get(Servo.class, "elbow");
        claw = hardwareMap.get(Servo.class, "claw");
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // We want to start the bot at x: 10, y: -8, heading: 90 degrees
        Pose2d startPose = new Pose2d(24, -60, Math.toRadians(0));

        drive.setPoseEstimate(startPose);

        Trajectory traj1 = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(16, -12), Math.toRadians(0))
                .splineTo(new Vector2d(36, 36), Math.toRadians(0))
                .build();

        Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                .splineTo(new Vector2d(36, 12), Math.toRadians(0))
                .build();

        drive.followTrajectory(traj1);
        elbow.setPosition(ELBOW_DOWN_POS);
        sleep(200);
        claw.setPosition(CLAW_OPEN_POS);
        sleep(100);
        elbow.setPosition(ELBOW_UP_POS);
        drive.followTrajectory(traj2);
    }
}
