
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class ShooterOpMode extends LinearOpMode {
    //defines member fields
//    private Gyroscope imu;
    
    private DcMotor theShooter;
    static final double INCREMENT   = 0.01;     // amount to slew servo each CYCLE_MS cycle
    static final int    CYCLE_MS    =   50;     // period of each cycle
    static final double MAX_POS     =  1.0;     // Maximum rotational position
    static final double MIN_POS     =  0.0;     // Minimum rotational position

    // Define class members
    Servo   servo;
    double  position = (MAX_POS - MIN_POS) / 2; // Start at halfway position
    boolean rampUp = true;


    @Override
    public void runOpMode() {
//        imu = hardwareMap.get(Gyroscope.class, "imu");
        //assigns motor to member fields
        theShooter = hardwareMap.get(DcMotor.class, "shooterthing");
        servo = hardwareMap.get(Servo.class, "left_hand");
        //resets encoders to zero
        theShooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //set power ---> runs
       theShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //shows status on driver station
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        //waits for driver to press play
        waitForStart();
        //while running

        double tgtPower = 0;
        while (opModeIsActive()) {
            //tgtPower = -this.gamepad1.left_stick_y;

            // check to see if we need to move the servo.
            if(gamepad1.y) {
                // move to 0 degrees.
                servo.setPosition(0.27);
                sleep(500);
                servo.setPosition(0.39);
            } else if (gamepad1.x || gamepad1.b) {
                // move to 90 degrees.
                servo.setPosition(0.45);
            } else if (gamepad1.a) {
                // move to 180 degrees.
                servo.setPosition(0.39);
            }

            theShooter.setPower(-this.gamepad1.left_stick_y);

            telemetry.addData("Servo Position", servo.getPosition());
            //telemetry.addData("Target Power", tgtPower);
            telemetry.addData("Status", "Running");
            telemetry.update();
        }


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


