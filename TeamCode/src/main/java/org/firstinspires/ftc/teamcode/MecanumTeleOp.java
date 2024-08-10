package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class MecanumTeleOp extends LinearOpMode {

    // Defining the servos used
    public Servo Reaper;
    public Servo Hood;
    public Servo Flipper;
    public Servo Claw;
    public CRServo Spin1;
    public CRServo Spin2;

    // This is used for rising or falling edge detectors,so you can detect if a button has been held down or not.
    Gamepad currentGamepad1 = new Gamepad();
    Gamepad currentGamepad2 = new Gamepad();
    Gamepad previousGamepad1 = new Gamepad();
    Gamepad previousGamepad2 = new Gamepad();

    // Defining variables
    // reaperPos is the position that the reaper will be in.
    int reaperPos;
    final double stow = 0.65;
    final double stack = 0.19;
    final double ground = 0.15;

    // Defining variables
    // FlipperPos is the position that the reaper will be in.
    int FlipperPos;
    final double hide = .22;
    final double intake = 0.42;

    final double hoodOpen = 0.57;
    final double hoodClosed = 0.4425;

//    final double spinin = 0;
//    final double spinout = 1;

    int clawPos;
    final double clawGrab = 0.5;
    final double clawRelease = .92;

    final double diffyPower = 0.75;

    // This is where the OpMode itself begins
    public void runOpMode() {

        // Telling the robot where to find the servos
        Reaper = hardwareMap.get(Servo.class, "reaper");
        Hood = hardwareMap.get(Servo.class, "hood");
        Flipper = hardwareMap.get(Servo.class, "flipper");
        Claw = hardwareMap.get(Servo.class, "claw");
        Spin1 = hardwareMap.get(CRServo.class, "spin1");
        Spin2 = hardwareMap.get(CRServo.class, "spin2");

        waitForStart();

        if (isStopRequested()) return;

        // This is the primary loop for teleOp
        while (opModeIsActive()) {

            // More code for edge detectors
            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);
            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            runMecanumDrive();

            runIntakeHood();

            runDiffy();

            runSpinner();

            // This is a rising edge detector. It makes sure that 1 is added to reaperPos once, instead
            // of every loop a is pressed.
            if (currentGamepad1.a && !previousGamepad1.a) {
                reaperPos++;
            }
            runReaper();

            if (currentGamepad2.y && !previousGamepad2.y){
                clawPos++;
            }
            runClaw();

            if (currentGamepad2.a && !previousGamepad2.a) {
                FlipperPos++;
            }
            runFlipper();

        }
    }


// Everything below this point is a separate method which contains the code for the main loop

    // This is a method containing all of the code needed for mecanum drive
    public void runMecanumDrive() {

        // Declaring the motors
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("leftFront");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("leftBack");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("rightFront");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("rightBack");

        //Reversing the motors for mecanum drive
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        //These are for mecanum drive
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
        double rx = gamepad1.right_stick_x;

        //This is basically limiting the speed of the drivetrain motors to be a maximum of 1
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        //This is setting the motor power to variables we previously defined
        frontLeftMotor.setPower(frontLeftPower);
        backLeftMotor.setPower(backLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backRightMotor.setPower(backRightPower);
    }

    // This method is for running the intake and the hood at the same time.
    public void runIntakeHood() {

        // Declaring intake motor
        DcMotor intake = hardwareMap.dcMotor.get("intake");

        // The power we want the intake to be run at
        double intakePower = 0.8;

        // This is setting the intake power when a bumper is pressed.
        if (gamepad1.right_bumper) {
            intake.setPower(intakePower);
        } else if (gamepad1.left_bumper) {
            intake.setPower(-intakePower);
        } else intake.setPower(0);

        // This means that, whenever the intake is active, the hood will be down.
        if (gamepad1.right_bumper) {
            Hood.setPosition(hoodOpen);
        } else Hood.setPosition(hoodClosed);
    }

        public void runClaw() {

            if (clawPos > 1) {
                clawPos = 0;
            }
            if (clawPos == 0) {
                Claw.setPosition(clawGrab);
            } else {
                Claw.setPosition(clawRelease);
            }
        }

        // This is a method for the reaper changing position.
        // This code is changing what position the reaper is at based on what value reaperPos has,
        // and resets if reaperPos gets above 2.
        public void runReaper (){

            if (reaperPos > 2) {
                reaperPos = 0;
            }
            if (reaperPos == 0) {
                Reaper.setPosition(stow);
            } else if (reaperPos == 1) {
                Reaper.setPosition(stack);
            } else {
                Reaper.setPosition(ground);
            }
        }

        // This is a method for the flipper changing position.
        // This code is changing what position the flipper is at based on what value FlipperPos has,
        // and resets if FlipperPos gets above 2.
        //I also copied thisssssss :3333
        public void runFlipper() {

            if (FlipperPos > 1) {
                FlipperPos = 0;
            }
            if (FlipperPos == 0) {
                Flipper.setPosition(hide);
            } else {
                Flipper.setPosition(intake);
            }
        }

    public void runSpinner() {

        // This is setting the claw intake power when a bumper is pressed for both sides.
        if (gamepad2.right_bumper) {
            Spin1.setPower(1);
            Spin2.setPower(-1);
        } else if (gamepad2.left_bumper) {
            Spin1.setPower(-1);
            Spin2.setPower(1);
        } else Spin1.setPower(0);
        Spin2.setPower(0);
    }

        public void runDiffy() {

            DcMotor diffy1 = hardwareMap.dcMotor.get("S1");
            DcMotor diffy2 = hardwareMap.dcMotor.get("S2");

            diffy1.setPower((gamepad2.left_stick_y) * (-diffyPower));
            diffy2.setPower((gamepad2.right_stick_y) * (diffyPower));
        }
    }

