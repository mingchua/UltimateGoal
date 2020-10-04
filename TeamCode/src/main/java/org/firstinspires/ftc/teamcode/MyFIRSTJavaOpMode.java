package org.firstinspires.ftc.teamcode;

import android.nfc.tech.TagTechnology;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gyroscope;

@TeleOp
public class MyFIRSTJavaOpMode extends LinearOpMode {
    //defines member fields
//    private Gyroscope imu;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() {
//        imu = hardwareMap.get(Gyroscope.class, "imu");
        //assigns motor to member fields
        frontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        backLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        backRight = hardwareMap.get(DcMotor.class, "BackRight");
        //resets encoders to zero
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //set power ---> runs
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        double maxPower = 0.5;
        //while running
        while (opModeIsActive()) {
            // forward and backwards
            //assigns gamepads joysticks directions
            x = -this.gamepad1.left_stick_x;
            y = -this.gamepad1.left_stick_y;
            rotation = -this.gamepad1.right_stick_x;

            frontLeftPower = rotation - y - x;
            frontRightPower = rotation + y + x;
            backLeftPower = rotation - y + x;
            backRightPower = rotation + y - x;

            maxAbsPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            maxAbsPower = Math.max(Math.abs(backLeftPower), maxAbsPower);
            maxAbsPower = Math.max(Math.abs(backRightPower), maxAbsPower);
            if (maxAbsPower > 1) {
                //maximum power value becomes 1
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
            //logs for puny humans
            //sends power and position (degrees the wheels have spun) to driver station.
            telemetry.addData("frontLeftPower", (frontLeftPower));
            telemetry.addData("frontRightPower", (frontRightPower));
            telemetry.addData("backLeftPower", (backLeftPower));
            telemetry.addData("backRightPower", (backRightPower));
            telemetry.addData("frontLeft Position", frontLeft.getCurrentPosition());
            telemetry.addData("frontRight Position", frontRight.getCurrentPosition());
            telemetry.addData("backLeft Position", backLeft.getCurrentPosition());
            telemetry.addData("backRight Position", backRight.getCurrentPosition());
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}

