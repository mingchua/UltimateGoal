package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.Vector;

@TeleOp
public class OpMode extends LinearOpMode {
    //defines member fields

    Servo elbow;
    Servo claw;

    enum Mode {
        DRIVER_CONTROL,
        AUTOMATIC_CONTROL,
        MOVING,
        SHOOTING,
        MOVING_TO_DROP,
        DROP,
        CLAW_OPEN,
    }

    Vector2d targetAVector = new Vector2d(0, 0);
    // The heading we want the bot to end on for targetA
    double targetAHeading = Math.toRadians(-27.5);

    Vector2d dropPos = new Vector2d(-60, -35);
    double dropHeading = Math.toRadians(180);

    OpMode.Mode currentMode = OpMode.Mode.DRIVER_CONTROL;

    private DcMotorEx frontLeft;
    private DcMotorEx frontRight;
    private DcMotorEx backLeft;
    private DcMotorEx backRight;
    private DcMotorEx transfer;
    private DcMotorEx shooter;
    static final double INCREMENT = 0.01;     // Amount to slew servo each CYCLE_MS cycle
    static final int CYCLE_MS = 50;     // Period of each cycle
    static final double MAX_POS = 1.0;     // Maximum rotational position
    static final double MIN_POS = 0.0;     // Minimum rotational position
    Servo servo;
    Servo trigger;

    private void shoot() {
        shooter.setVelocity(2500);
        while (shooter.getVelocity() < 2460 ||
                shooter.getVelocity() > 2540) {
            sleep(50);
        }
        trigger.setPosition(0.5);
        sleep(300);
        trigger.setPosition(0.6);
    }
    @Override
    public void runOpMode() {
//        imu = hardwareMap.get(Gyroscope.class, "imu");
        //assigns motor to member fields

        elbow = hardwareMap.get(Servo.class, "elbow");
        claw = hardwareMap.get(Servo.class, "claw");

        frontLeft = hardwareMap.get(DcMotorEx.class, "leftFront");
        frontRight = hardwareMap.get(DcMotorEx.class, "rightFront");
        backLeft = hardwareMap.get(DcMotorEx.class, "leftRear");
        backRight = hardwareMap.get(DcMotorEx.class, "rightRear");
        transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        shooter = hardwareMap.get(DcMotorEx.class, "shooterthing");
        trigger = hardwareMap.get(Servo.class, "left_hand");
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        //resets encoders to zero
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        transfer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //set power ---> runs
//        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // Set your initial pose to x: 10, y: 10, facing 90 degrees
        drive.setPoseEstimate(PoseStorage.currentPose);

        //shows status on driver station
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        //waits for driver to press play
        waitForStart();
        // run until the end of the match (driver presses STOP)
        //defines local variables
        double x = 0;
        double y = 0;
        double turning = 0;
        double righttrigger;
        double lefttrigger;
        long curTime = System.currentTimeMillis();
        int flywheelspeed = Constants.FLY_WHEEL_SPEED;

        double currentElbowPos = Constants.ELBOW_UP_POS;
        boolean lastrightbumper = false;
        double currentClawPos = Constants.CLAW_CLOSED_POS;
        trigger = hardwareMap.get(Servo.class, "left_hand");


        //while running
        while (opModeIsActive()) {

            // Make sure to call drive.update() on *every* loop
            // Increasing loop time by utilizing bulk reads and minimizing writes will increase your odometry accuracy
            drive.update();

            // Read pose
            Pose2d poseEstimate = drive.getPoseEstimate();

            righttrigger = this.gamepad1.right_trigger;
            lefttrigger = this.gamepad1.left_trigger;

            switch (currentMode) {
                case DRIVER_CONTROL:
                    x = gamepad1.left_stick_x;
                    y = gamepad1.left_stick_y;
                    Vector2d input = new Vector2d(
                            y * y * (y > 0 ? -1 : 1),
                            x * x * (x > 0 ? -1 : 1)
                    ).rotated(-poseEstimate.getHeading());

                    // Pass in the rotated input + right stick value for rotation
                    // Rotation is not part of the rotated input thus must be passed in separately
                    turning = gamepad1.right_stick_x;
                    drive.setWeightedDrivePower(
                            new Pose2d(
                                    input.getX(),
                                    input.getY(),
                                    turning * turning * (turning > 0 ? -1 : 1)));
                    if (righttrigger > 0.2) {
                        transfer.setPower(-1);
                    } else {
                        if (lefttrigger > 0.2) {
                            transfer.setVelocity(1200);
                        } else {
                            transfer.setVelocity(0);
                        }
                    }

                    boolean thisrightbumper = gamepad1.right_bumper;
                    if (thisrightbumper && ! lastrightbumper) {
                        if (currentElbowPos == Constants.ELBOW_DOWN_POS) {
                            claw.setPosition(Constants.CLAW_CLOSED_POS);
                            sleep(100);
                            elbow.setPosition(Constants.ELBOW_UP_POS);
                            currentElbowPos = Constants.ELBOW_UP_POS;
                        } else {
                            elbow.setPosition(Constants.ELBOW_DOWN_POS);
                            claw.setPosition(Constants.CLAW_OPEN_POS);
                            currentElbowPos = Constants.ELBOW_DOWN_POS;
                        }
                    }

                    lastrightbumper = thisrightbumper;

                    boolean triggerOn = gamepad1.y;
                    boolean flywheelOn = gamepad1.left_bumper;
                    //sets flywheel velocity to the current velocity set (flywheelspeed)
                    if (flywheelOn) {
                        shooter.setVelocity(flywheelspeed);
                    } else {
                        shooter.setVelocity(0.0);
                    }
                    if (triggerOn) {
                        trigger.setPosition(0.5);
                        sleep(50);
                        trigger.setPosition(0.6);
                    }

                    if (gamepad1.b) {
                        Trajectory trajP = drive.trajectoryBuilder(poseEstimate, true)
                                .splineTo(dropPos, dropHeading)
                                .build();

                        drive.followTrajectoryAsync(trajP);
                        currentMode = Mode.MOVING_TO_DROP;
                    }

                    if (gamepad1.a) {
                        Trajectory traj1 = drive.trajectoryBuilder(poseEstimate)
                                .splineTo(targetAVector, targetAHeading)
                                .build();

                        drive.followTrajectoryAsync(traj1);
                        shooter.setVelocity(2500);
                        currentMode = Mode.MOVING;

                    }
                    break;

                case MOVING_TO_DROP:
                    if (gamepad1.x) {
                        drive.cancelFollowing();
                        currentMode = Mode.DRIVER_CONTROL;
                    }
                    if (!drive.isBusy()) {
                        currentMode = Mode.DRIVER_CONTROL;
                    }
                    break;

                case DROP:
                    elbow.setPosition(Constants.ELBOW_DOWN_POS);
                    currentMode = Mode.CLAW_OPEN;
                    break;

                case CLAW_OPEN:
                    claw.setPosition(Constants.CLAW_OPEN_POS);
                    currentMode = Mode.DRIVER_CONTROL;
                    break;

                case MOVING:
                    if (gamepad1.x) {
                        drive.cancelFollowing();
                        currentMode = Mode.DRIVER_CONTROL;
                    }
                    if (!drive.isBusy()) {
                        currentMode = Mode.SHOOTING;
                    }
                    break;
                case SHOOTING:
                    shoot();
                    shoot();
                    shoot();
                    shooter.setVelocity(0);
                    currentMode = Mode.DRIVER_CONTROL;
                    break;
                case AUTOMATIC_CONTROL:
                    // If x is pressed, we break out of the automatic following
                    if (gamepad1.x) {
                        drive.cancelFollowing();
                        currentMode = Mode.DRIVER_CONTROL;
                    }

                    // If drive finishes its task, cede control to the driver
                    if (!drive.isBusy()) {
                        currentMode = Mode.DRIVER_CONTROL;
                    }
                    break;
            }
            telemetry.addData("encoders: ", "%d %d %d %d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition(), backLeft.getCurrentPosition(), backRight.getCurrentPosition());
            telemetry.update();
        }
    }
}






                                                     //        _
                                                    //   /\   | \   /
                                                   //   /  \  | /
                                                  //   /----\  \
            // Create a vector from the gamepad x/y inputs
            // Then, rotate that vector by the inverse of current heading
            // forward and backwards
            //assigns gamepads joysticks directions
//            boolean triggerOn = gamepad1.y;
//            boolean flywheelOn = gamepad1.left_bumper;

            //runs intake and transfer when right trigger is pressed beyond 0.2
            //if dpad up pressed, changes velocity by +100, if dpad down press, changes velocity by -100
//            if (this.gamepad1.dpad_up) {
//                flywheelspeed += 10;
//            }
//            if (this.gamepad1.dpad_down) {
//                flywheelspeed -= 10;
//            }

            //sets flywheel velocity to the current velocity set (flywheelspeed)
//            if (flywheelOn) {
//                shooter.setVelocity(flywheelspeed);
////            flywheelOn = !flywheelOn;
//            } else {
//                shooter.setVelocity(0.0);
//            }
//            if (triggerOn) {
//                servo.setPosition(0.5);
//                sleep(50);
//                servo.setPosition(0.6);
//            }
//            if (gamepad1.b) {
//                claw.setPosition(Constants.CLAW_CLOSED_POS);
//            } else if (gamepad1.x) {
//                claw.setPosition(Constants.CLAW_OPEN_POS);
//            }



            //logs for puny humans
            //sends power and position (degrees the wheels have spun) to driver station.
//            telemetry.addData("Flywheel Velocity", flywheelspeed);
//            telemetry.addData("inputs", "%.2f %.2f %.2f", x, y, turning);
//            telemetry.addData("transfer power",transfer.getPower());
//            telemetry.addData("wheels velocity", "%d %d %d %d", frontLeft.getVelocity(), frontRight.getVelocity(), backLeft.getVelocity(), backRight.getVelocity());
//            telemetry.addData("Servo Position", servo.getPosition());
//            telemetry.update();


// Pre road runner power multiplier:
//            x = -this.gamepad1.left_stick_x;
//            y = -this.gamepad1.left_stick_y;
//            rotation = -this.gamepad1.right_stick_x;

//            frontLeftPower = rotation - y + x;
//            frontRightPower = rotation + y + x;
//            backLeftPower = rotation - y - x;
//            backRightPower = rotation + y - x;
//
//            maxAbsPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
//            maxAbsPower = Math.max(Math.abs(backLeftPower), maxAbsPower);
//            maxAbsPower = Math.max(Math.abs(backRightPower), maxAbsPower);
//            if (maxAbsPower > 1) {
//                //maximum power value becomes > 1
//                frontLeftPower = frontLeftPower / maxAbsPower;
//                frontRightPower = frontRightPower / maxAbsPower;
//                backLeftPower = backLeftPower / maxAbsPower;
//                backRightPower = backRightPower / maxAbsPower;
//            }
//
//            frontLeftPower = frontLeftPower * maxPower;
//            frontRightPower = frontRightPower * maxPower;
//            backLeftPower = backLeftPower * maxPower;
//            backRightPower = backRightPower * maxPower;
//
//            frontLeft.setPower(frontLeftPower);
//            frontRight.setPower(frontRightPower);
//            backLeft.setPower(backLeftPower);
//            backRight.setPower(backRightPower);

//OCDCO:
//                    if (gamepad1.b) {
//                        Trajectory trajP = drive.trajectoryBuilder(poseEstimate)
//                                .splineTo(dropPos, dropHeading);
//                       .build();
//                        currentElbowPos = Constants.ELBOW_DOWN_POS
//                        claw.setPosition(Constants.CLAW_CLOSED_POS);
//                    }



