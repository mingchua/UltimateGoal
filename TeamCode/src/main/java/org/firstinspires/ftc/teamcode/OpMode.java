package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp
public class OpMode extends LinearOpMode {
    //defines member fields

    Servo elbow;
    Servo claw;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotorEx transfer;
    private DcMotorEx shooter;
    static final double INCREMENT = 0.01;     // Amount to slew servo each CYCLE_MS cycle
    static final int CYCLE_MS = 50;     // Period of each cycle
    static final double MAX_POS = 1.0;     // Maximum rotational position
    static final double MIN_POS = 0.0;     // Minimum rotational position
    Servo servo;
    double position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
    boolean rampUp = true;
    @Override
    public void runOpMode() {
//        imu = hardwareMap.get(Gyroscope.class, "imu");
        //assigns motor to member fields

        elbow = hardwareMap.get(Servo.class, "elbow");
        claw = hardwareMap.get(Servo.class, "claw");

        frontLeft = hardwareMap.get(DcMotor.class, "leftFront");
        frontRight = hardwareMap.get(DcMotor.class, "rightFront");
        backLeft = hardwareMap.get(DcMotor.class, "leftRear");
        backRight = hardwareMap.get(DcMotor.class, "rightRear");
        transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        shooter = hardwareMap.get(DcMotorEx.class, "shooterthing");
        servo = hardwareMap.get(Servo.class, "left_hand");
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
//        shooter.setDirection(DcMotorSimple.Direction.REVERSE);

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

        double currentElbowPos = Constants.ELBOW_DOWN_POS;
        boolean lastrightbumper = false;
        double currentClawPos = Constants.CLAW_CLOSED_POS;

        //while running
        while (opModeIsActive()) {

            // Make sure to call drive.update() on *every* loop
            // Increasing loop time by utilizing bulk reads and minimizing writes will increase your odometry accuracy
            drive.update();

            // Read pose
            Pose2d poseEstimate = drive.getPoseEstimate();

            // Create a vector from the gamepad x/y inputs
            // Then, rotate that vector by the inverse of current heading
            x = gamepad1.left_stick_x;
            y = gamepad1.left_stick_y;
            Vector2d input = new Vector2d(
                    y * y * (y > 0 ? -1 : 1),
                    x * x * (x > 0 ? -1 : 1)
            );//.rotated(-poseEstimate.getHeading());

            // Pass in the rotated input + right stick value for rotation
            // Rotation is not part of the rotated input thus must be passed in separately
            turning = gamepad1.right_stick_x;
            drive.setWeightedDrivePower(
                    new Pose2d(
                            input.getX(),
                            input.getY(),
                            turning * turning * (turning > 0 ? -1 : 1)
                    )
            );

            // forward and backwards
            //assigns gamepads joysticks directions

            righttrigger = this.gamepad1.right_trigger;
            lefttrigger = this.gamepad1.left_trigger;
            boolean triggerOn = gamepad1.y;
            boolean flywheelOn = gamepad1.left_bumper;

            //runs intake and transfer when right trigger is pressed beyond 0.2
            if (righttrigger > 0.2) {
                transfer.setPower(-1);
            } else {
                if (lefttrigger > 0.2) {
                    transfer.setVelocity(1200);
                } else {
                    transfer.setVelocity(0);
                }
            }

            //if dpad up pressed, changes velocity by +100, if dpad down press, changes velocity by -100
            if (this.gamepad1.dpad_up) {
                flywheelspeed += 10;
            }
            if (this.gamepad1.dpad_down) {
                flywheelspeed -= 10;
            }

            //sets flywheel velocity to the current velocity set (flywheelspeed)
            if (flywheelOn) {
                shooter.setVelocity(flywheelspeed);
//            flywheelOn = !flywheelOn;
            } else {
                shooter.setVelocity(0.0);
            }
            if (triggerOn) {
                servo.setPosition(0.5);
                sleep(50);
                servo.setPosition(0.6);
            }
            if (gamepad1.b) {
                claw.setPosition(Constants.CLAW_CLOSED_POS);
            } else if (gamepad1.x) {
                claw.setPosition(Constants.CLAW_OPEN_POS);
            }

            boolean thisrightbumper = gamepad1.right_bumper;
            if (thisrightbumper && ! lastrightbumper){
                currentElbowPos = (currentElbowPos == Constants.ELBOW_DOWN_POS)? Constants.ELBOW_UP_POS : Constants.ELBOW_DOWN_POS;
                elbow.setPosition(currentElbowPos);
            }
            lastrightbumper = thisrightbumper;

            //logs for puny humans
            //sends power and position (degrees the wheels have spun) to driver station.
            telemetry.addData("Flywheel Velocity", flywheelspeed);
            telemetry.addData("flywheel current velocity", shooter.getVelocity());
            telemetry.addData("turning", turning);
            telemetry.addData("transfer power",transfer.getVelocity());
            telemetry.addData("frontLeft Position", frontLeft.getCurrentPosition());
            telemetry.addData("frontRight Position", frontRight.getCurrentPosition());
            telemetry.addData("backLeft Position", backLeft.getCurrentPosition());
            telemetry.addData("backRight Position", backRight.getCurrentPosition());
            telemetry.addData("Servo Position", servo.getPosition());
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}

//Archive of code that was replaced by road runner. Let's get an f

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



