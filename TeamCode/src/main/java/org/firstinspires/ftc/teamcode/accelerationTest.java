package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import java.util.concurrent.TimeUnit;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous
public class accelerationTest extends LinearOpMode {

    private DcMotorEx frontLeft;
    private DcMotorEx frontRight;
    private DcMotorEx backLeft;
    private DcMotorEx backRight;
    double topSpeed;
    long lastUpdate;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotorEx.class, "leftFront");
        frontRight = hardwareMap.get(DcMotorEx.class, "rightFront");
        backLeft = hardwareMap.get(DcMotorEx.class, "leftRear");
        backRight = hardwareMap.get(DcMotorEx.class, "rightRear");

        frontLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        telemetry.addData("voltage", "%.5f volts", getBatteryVoltage());

        //timer
        long startTime = System.currentTimeMillis();

        // acceleration time
        frontLeft.setPower(1);
        frontRight.setPower(1);
        backLeft.setPower(1);
        backRight.setPower(1);
        topSpeed = 0;


        //telemetry.addData("Accelerating", frontLeft.getVelocity());


        while (frontLeft.getVelocity() < 2300) {
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        double acceleration = frontLeft.getVelocity() / elapsedTime;
        telemetry.addData("Acceleration", acceleration);
        telemetry.addData("time to accelerate", elapsedTime);


        // top speed
        lastUpdate = System.currentTimeMillis();
        while (System.currentTimeMillis() - lastUpdate < 500) {  //checking whether or not the speed has changed within the last .5 seconds
            if (frontLeft.getVelocity() > topSpeed) { //stop entering if() loop once speed has reached its limit (getVelocity is the same as topSpeed)
                topSpeed = frontLeft.getVelocity();
                lastUpdate = System.currentTimeMillis(); //updates 'lastUpdate' as long as the robot keeps accelerating
            }
        }

        telemetry.addData("Top Speed", topSpeed); //prints topSpeed under 'Top Speed' in driver station

        long deccel_start = System.currentTimeMillis();
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        while (frontLeft.getVelocity() > 0) {
        }

        long elapsedTime_2 = System.currentTimeMillis() - deccel_start;
        double deceleration = topSpeed / elapsedTime_2;

        telemetry.addData("time to decelerate", elapsedTime_2);
        telemetry.addData("deceleration", deceleration);
        telemetry.addData("End voltage", "%.5f volts", getBatteryVoltage());

        telemetry.update();

        while (true) {
            if (gamepad1.x || isStopRequested()) {
                break;
            }
        }
    }

    double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }
}
