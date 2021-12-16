package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import static org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit.MILLIAMPS;

@TeleOp
public class ArmTest extends LinearOpMode {
    private DcMotorEx frontLeft;
    private DcMotorEx forefore;
    private DcMotorEx foreArm;
    private DcMotorEx backArm;

    @Override
    public void runOpMode() {

        double lift = 0;
        double lift2 = 0;
        double lift3 = 0;

        frontLeft = hardwareMap.get(DcMotorEx.class, "leftFront");
        forefore = hardwareMap.get(DcMotorEx.class, "forefore");
        foreArm = hardwareMap.get(DcMotorEx.class, "foreArm");
        backArm = hardwareMap.get(DcMotorEx.class, "backArm");

        waitForStart();

        while (opModeIsActive()) {
            lift = gamepad1.left_stick_x;
            lift2 = gamepad1.right_stick_x;
            lift3 = gamepad1.right_stick_y;

            backArm.setPower(lift);
            foreArm.setPower(lift2*0.8); //>0.5?150:0
            forefore.setPower(lift3*0.5);
            //foreArm.setPower(Math.min(lift2, 0.2));

            double foreCurrent = foreArm.getCurrent(CurrentUnit.MILLIAMPS);
            double foreVelo = foreArm.getVelocity();
            double backCurrent = backArm.getCurrent(CurrentUnit.MILLIAMPS);
            double backVelo = backArm.getVelocity();
            double ffcurrent = forefore.getCurrent(CurrentUnit.MILLIAMPS);
            double ffvelo = forefore.getVelocity();

            telemetry.addData("Forearm Current", foreCurrent);
            telemetry.addData("Forearm Velocity", foreVelo);
            telemetry.addData("Forearm Power", foreArm.getPower());
            telemetry.addData("Back arm Current", backCurrent);
            telemetry.addData("Back arm Velocity", backVelo);
            telemetry.addData("Fore Fore arm current", ffcurrent);
            telemetry.addData("Fore Fore arm velocity", ffvelo);
            telemetry.update();

        }
    }
}


