package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.Arrays;
import java.util.List;

@Autonomous
public class autonomous extends LinearOpMode {
    Servo elbow;
    Servo claw;
    Servo trigger;
    private DcMotorEx shooter;
    static final double ELBOW_DOWN_POS = 0.85;
    static final double ELBOW_UP_POS = 0.3;
    static final double CLAW_OPEN_POS = 0.6;
    static final double CLAW_CLOSED_POS = 0.2;
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_QUAD_ELEMENT = "Quad";
    private static final String LABEL_SINGLE_ELEMENT = "Single";


    private static final String VUFORIA_KEY =
            " Ad3EPvH/////AAABmbK1phtEg0sQiAGa76LzfdZl0l0A7qe02SnMTDj33sg9HSuurm17y5syMCyvH4423t/ZKuLv8hVk3dDqwwipaeZA2oWgbHYL2IzQ+D4VboFMoy8Oc+3/GQcOAZZ+REmtWibAYMW9yOMzh8YFGs+Uf9f4AVmH40xE8kkLG3dBjaWujr/QNlVAIOp5XWshE9v6Y02VXJhRgZf81u5GoFTYATn0xTdsPTV8c5LspAQuL/J4GermCUTs/8aNKIKjHsfD0zgWgEHTiuyYaJmgqWYc2jfA1ngDb2OGfSsH9kNJxJftkQ4uk4gToAfPRDJCagQkQq6wpmXzx2pR/3rk9u6EXJAUEuSTZ9BvR/LyVuLzSORN ";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;


    @Override
    public void runOpMode() {

        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();
        }
        shooter = hardwareMap.get(DcMotorEx.class, "shooterthing");
        elbow = hardwareMap.get(Servo.class, "elbow");
        claw = hardwareMap.get(Servo.class, "claw");
        trigger = hardwareMap.get(Servo.class, "left_hand");

        shooter.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // We want to start the bot at x: 10, y: -8, heading: 90 degrees
        Pose2d startPose = new Pose2d(-63, -33, Math.toRadians(0));

        drive.setPoseEstimate(startPose);

        Trajectory trajfirst = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(-57, -33), Math.toRadians(0))
                .build();


        waitForStart();

        if(isStopRequested()) return;

        claw.setPosition(CLAW_CLOSED_POS);
        drive.followTrajectory(trajfirst);
        sleep(2000);
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        int rings = 0;
        if (updatedRecognitions != null) {
            telemetry.addData("# Object Detected", updatedRecognitions.size());

            // step through the list of recognitions and display boundary info.
            int i = 0;
            for (Recognition recognition : updatedRecognitions) {
                if (recognition.getLabel() == LABEL_QUAD_ELEMENT ){
                    rings = 4;
                } else if (recognition.getLabel() == LABEL_SINGLE_ELEMENT){
                    rings = 1;
                }
                telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                        recognition.getLeft(), recognition.getTop());
                telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                        recognition.getRight(), recognition.getBottom());
            }
            telemetry.update();
        }

        Trajectory traj1;
        if(rings == 4) {
            traj1 = drive.trajectoryBuilder(trajfirst.end())
                    .splineTo(new Vector2d(-24, -12), Math.toRadians(0))
                    .splineTo(new Vector2d(36, -60), Math.toRadians(180))
                    .build();
        } else if (rings == 1){
            traj1 = drive.trajectoryBuilder(trajfirst.end())
                    .splineTo(new Vector2d(-24, -12), Math.toRadians(0))
                    .splineTo(new Vector2d(18, -36), Math.toRadians(180))
                    .build();
        }else {
            traj1 = drive.trajectoryBuilder(trajfirst.end())
                    .splineTo(new Vector2d(-24, -12), Math.toRadians(0))
                    .splineTo(new Vector2d(12, -60), Math.toRadians(180))
                    .build();
        }
        Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                .splineTo(new Vector2d(0, -36), Math.toRadians(0))
                .build();



        drive.followTrajectory(traj1);
        elbow.setPosition(ELBOW_DOWN_POS);
        sleep(1000);
        claw.setPosition(CLAW_OPEN_POS);
        sleep(100);
        elbow.setPosition(ELBOW_UP_POS);
        drive.followTrajectory(traj2);
        sleep(500);
        shoot();
        shoot();
        shoot();

    }
    private void shoot() {
        shooter.setVelocity(-1250);
        while (shooter.getVelocity() > -1220 || shooter.getVelocity() < -1280) {
            sleep(100);
        }
        trigger.setPosition(0.5);
        sleep(500);
        trigger.setPosition(0.6);
    }
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_QUAD_ELEMENT, LABEL_SINGLE_ELEMENT);
    }
}
