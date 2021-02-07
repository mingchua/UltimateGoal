
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class PickUpArmOpMode extends LinearOpMode {
Servo elbow;
Servo claw;
static final double ELBOW_POS_1 = 0.25;
static final double ELBOW_POS_2 = 0.75;
static final double CLAW_POS_1 = 0.65;
static final double CLAW_POS_2 = 0.45;


    @Override
    public void runOpMode() throws InterruptedException {
        elbow = hardwareMap.get(Servo.class, "pick_up_elbow");
        claw = hardwareMap.get(Servo.class, "pick_up_claw");
        waitForStart();
        boolean lastybutton = false;
        double currentElbowPos = ELBOW_POS_1;
        boolean lastrightbumper = false;
        double currentClawPos = CLAW_POS_1;
        while (opModeIsActive()) {
            boolean thisybutton = gamepad1.y;
            if (thisybutton && ! lastybutton){
                currentElbowPos = (currentElbowPos == ELBOW_POS_1)? ELBOW_POS_2:ELBOW_POS_1;
                elbow.setPosition(currentElbowPos);
            }
            lastybutton = thisybutton;

            boolean thisrightbumper = gamepad1.right_bumper;
            if (thisrightbumper && ! lastrightbumper){
                currentClawPos = (currentClawPos == CLAW_POS_1)? CLAW_POS_2:CLAW_POS_1;
                claw.setPosition(currentClawPos);
            }
            lastrightbumper = thisrightbumper;
        }
    }
}
