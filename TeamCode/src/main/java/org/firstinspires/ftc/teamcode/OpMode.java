package org.firstinspires.ftc.teamcode;

import android.nfc.tech.TagTechnology;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp
public class OpMode extends LinearOpMode {
    //defines member fields
//    private Gyroscope imu;
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
        frontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        backLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        backRight = hardwareMap.get(DcMotor.class, "BackRight");
        transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        shooter = hardwareMap.get(DcMotorEx.class, "shooterthing");
        servo = hardwareMap.get(Servo.class, "left_hand");
        //resets encoders to zero
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        transfer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //set power ---> runs
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        //shows status on driver station
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        //waits for driver to press play
        waitForStart();
        // run until the end of the match (driver presses STOP)
        //defines local variables
        double x = 0;
        double y = 0;
        double rotation = 0;
        double frontLeftPower;
        double frontRightPower;
        double backLeftPower;
        double backRightPower;
        double maxAbsPower;
        double maxPower = 0.4;
        double righttrigger;
        boolean rightbumper;
        long curTime = System.currentTimeMillis();
        int flywheelspeed = -1100;
        //while running
        while (opModeIsActive()) {
            // forward and backwards
            //assigns gamepads joysticks directions
            x = -this.gamepad1.left_stick_x;
            y = -this.gamepad1.left_stick_y;
            rotation = -this.gamepad1.right_stick_x;
            righttrigger = this.gamepad1.right_trigger;
            rightbumper = this.gamepad1.right_bumper;
            boolean triggerOn = gamepad1.y;
            boolean flywheelOn = gamepad1.left_bumper;

            frontLeftPower = rotation - y + x;
            frontRightPower = rotation + y + x;
            backLeftPower = rotation - y - x;
            backRightPower = rotation + y - x;

            maxAbsPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            maxAbsPower = Math.max(Math.abs(backLeftPower), maxAbsPower);
            maxAbsPower = Math.max(Math.abs(backRightPower), maxAbsPower);
            if (maxAbsPower > 1) {
                //maximum power value becomes > 1
                frontLeftPower = frontLeftPower / maxAbsPower;
                frontRightPower = frontRightPower / maxAbsPower;
                backLeftPower = backLeftPower / maxAbsPower;
                backRightPower = backRightPower / maxAbsPower;
            }

            frontLeftPower = frontLeftPower * maxPower;
            frontRightPower = frontRightPower * maxPower;
            backLeftPower = backLeftPower * maxPower;
            backRightPower = backRightPower * maxPower;

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            //runs intake and transfer when right trigger is pressed beyond 0.2, puny human
            if (righttrigger > 0.2) {
                transfer.setPower(-1);
            } else {
                if (rightbumper == true) {
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
                sleep(500);
                servo.setPosition(0.6);
            }

            //logs for the best humans
            //sends power and position (degrees the wheels have spun) to driver station.
            telemetry.addData("Flywheel Velocity", flywheelspeed);
            telemetry.addData("transfer power",transfer.getVelocity());
            telemetry.addData("frontLeftPower", (frontLeftPower));
            telemetry.addData("frontRightPower", (frontRightPower));
            telemetry.addData("backLeftPower", (backLeftPower));
            telemetry.addData("backRightPower", (backRightPower));
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

