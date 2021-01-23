
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class ShooterOpMode extends LinearOpMode {
    //defines member fields
//    private Gyroscope imu;

    private DcMotorEx theShooter;
    static final double INCREMENT = 0.01;     // Amount to slew servo each CYCLE_MS cycle
    static final int CYCLE_MS = 50;     // Period of each cycle
    static final double MAX_POS = 1.0;     // Maximum rotational position
    static final double MIN_POS = 0.0;     // Minimum rotational position

    // Define class members
    Servo servo;
    double position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
    boolean rampUp = true;

    @Override
    public void runOpMode() {
//        imu = hardwareMap.get(Gyroscope.class, "imu");
        //assigns motor to member fields
        theShooter = hardwareMap.get(DcMotorEx.class, "shooterthing");
        servo = hardwareMap.get(Servo.class, "left_hand");
        //resets encoders to zero
        theShooter.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //set power ---> runs
        theShooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        //shows status on driver station
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        //waits for driver to press play
        waitForStart();
        //while running
        while (opModeIsActive()) {
            // check to see if we need to move the servo.
            long curTime = System.currentTimeMillis();
            boolean triggerOn = gamepad1.y;
            boolean flywheelOn = gamepad1.left_bumper;

            if (flywheelOn) {
                theShooter.setVelocity(-1600);
//            flywheelOn = !flywheelOn;
            } else {
                theShooter.setVelocity(0.0);
            }
            if (triggerOn) {
                servo.setPosition(0.3);
                sleep(500);
                servo.setPosition(0.6);
            }

            telemetry.addData("Servo Position", servo.getPosition());
            telemetry.addData("Status", "Running");
            telemetry.update();

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

