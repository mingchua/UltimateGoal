package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class ShooterOpMode extends LinearOpMode {
    //defines member fields
//    private Gyroscope imu;
    private DcMotor theshooter;


    @Override
    public void runOpMode() {
//        imu = hardwareMap.get(Gyroscope.class, "imu");
        //assigns motor to member fields
        theshooter = hardwareMap.get(DcMotor.class, "shooterthing");
        //resets encoders to zero
        theshooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //set power ---> runs
       theshooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //shows status on driver station
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        //waits for driver to press play
        waitForStart();
        //while running
        while (opModeIsActive()) {
            theshooter.setPower(-this.gamepad1.left_stick_y);

//            telemetry.addData("frontLeftPower", (frontLeftPower));
//            telemetry.addData("frontRightPower", (frontRightPower));
//            telemetry.addData("backLeftPower", (backLeftPower));
//            telemetry.addData("backRightPower", (backRightPower
//            ));
//            telemetry.addData("frontLeft Position", frontLeft.getCurrentPosition());
//            telemetry.addData("frontRight Position", frontRight.getCurrentPosition());
//            telemetry.addData("backLeft Position", backLeft.getCurrentPosition());
//            telemetry.addData("backRight Position", backRight.getCurrentPosition());
//            telemetry.addData("Status", "Running");
//            telemetry.update();
        }
    }
}

