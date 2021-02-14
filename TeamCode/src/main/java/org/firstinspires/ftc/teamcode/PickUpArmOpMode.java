
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class PickUpArmOpMode extends LinearOpMode {
Servo elbow;
Servo claw;
static final double ELBOW_POS_1 = 0.85;
static final double ELBOW_POS_2 = 0.3;
static final double CLAW_POS_1 = 0.6;
static final double CLAW_POS_2 = 0.2;


    @Override
    public void runOpMode() throws InterruptedException {
        elbow = hardwareMap.get(Servo.class, "elbow");
        claw = hardwareMap.get(Servo.class, "claw");
        waitForStart();
        double currentElbowPos = ELBOW_POS_1;
        boolean lastrightbumper = false;
        double currentClawPos = CLAW_POS_1;
        while (opModeIsActive()) {
            if (gamepad1.b) {
                claw.setPosition(CLAW_POS_1);
            } else if (gamepad1.x) {
                claw.setPosition(CLAW_POS_2);
            }

            boolean thisrightbumper = gamepad1.right_bumper;
            if (thisrightbumper && ! lastrightbumper){
                currentElbowPos = (currentElbowPos == ELBOW_POS_1)? ELBOW_POS_2:ELBOW_POS_1;
                elbow.setPosition(currentElbowPos);
            }
            lastrightbumper = thisrightbumper;
        }
    }
}
