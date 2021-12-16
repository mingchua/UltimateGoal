package org.firstinspires.ftc.teamcode;

//Imports stuff we don't get scary red errors

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

//TeleOp :)
@TeleOp
public class OpModeFF extends LinearOpMode {


    private DcMotorEx foreforeArm;
    private DcMotorEx foreArm;
    private DcMotorEx backArm;
    private DcMotorEx intake;


    @Override
    public void runOpMode() {

        foreforeArm = hardwareMap.get(DcMotorEx.class, "foreforearm");
        foreArm = hardwareMap.get(DcMotorEx.class, "forearm");
        backArm = hardwareMap.get(DcMotorEx.class, "backarm");
        intake = hardwareMap.get(DcMotorEx.class, "intake");

        //Sets drivetrain motors up
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Shows status on driver control station
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        //Waits for start
        waitForStart();

        //Sets some values

        //backArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //While the control program is active, the following will run
        while (opModeIsActive()) {
            // Increasing loop time by utilizing bulk reads and minimizing writes will increase your odometry accuracy
            drive.update();

            double x = 0;
            double y = 0;
            double turning = 0;
            boolean BackArmUp = this.gamepad2.y;
            boolean BackArmDown = this.gamepad2.a;
            boolean ForeArmUp = this.gamepad2.dpad_up;
            boolean ForeArmDown = this.gamepad2.dpad_down;
            boolean FFArmUp = this.gamepad2.dpad_left;
            boolean FFArmDown = this.gamepad2.dpad_right;
            boolean intakeRun = this.gamepad1.x;
            boolean outakeRun = this.gamepad1.b;


            //Controls drivetrain
            x = gamepad1.left_stick_x;
            y = gamepad1.left_stick_y;
            Vector2d input = new Vector2d(
                    y * y * (y > 0 ? 1 : -1),
                    x * x * (x > 0 ? 1 : -1));

            turning = gamepad1.right_stick_x;
            drive.setWeightedDrivePower(
                    new Pose2d(
                            input.getX(),
                            input.getY(),
                            turning * turning * (turning > 0 ? 1 : -1)));
            //Arm program :)
            if (BackArmUp) {
                //backArm.setVelocity(300);
                backArm.setPower(0.6);
            }
            if (BackArmDown) {
                backArm.setPower(-0.6);
            }
            if (!BackArmUp && !BackArmDown) {
                backArm.setPower(0);
            }
            if (ForeArmUp) {
                foreArm.setPower(0.5);
            }
            if (ForeArmDown) {
                foreArm.setPower(-0.5);
            }
            if (!ForeArmUp && !ForeArmDown) {
                foreArm.setPower(0);
            }
            if (FFArmUp) {
                foreforeArm.setPower(0.3);
            }
            if (FFArmDown) {
                foreforeArm.setPower(-0.3);
            }
            if (!FFArmDown && !FFArmUp) {
                foreforeArm.setPower(0);
            }
            if (intakeRun) {
                intake.setPower(0.3);
            }
            if (outakeRun) {
                intake.setPower(-0.3);
            }
            if (!intakeRun && !outakeRun) {
                intake.setPower(0);
            }
        }
    }
}



