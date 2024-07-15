package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class MecanumTeleOp extends LinearOpMode {

    //Defining the servos used
    public Servo Reaper;
    public Servo Hood;

    //This is used for rising or falling edge detectors,so you can detect if a button has been held down or not.
    Gamepad currentGamepad1 = new Gamepad();
    Gamepad currentGamepad2 = new Gamepad();
    Gamepad previousGamepad1 = new Gamepad();
    Gamepad previousGamepad2 = new Gamepad();


    //Defining variables
    //reaperPos is the position that the reaper will be in.
    int reaperPos;
    double stow = 0.7;
    double stack = 0.3;
    double ground = .15;

    public void runOpMode() {

        //Telling the robot where to find the servos
        Reaper = hardwareMap.get(Servo.class, "reaper");
        Hood = hardwareMap.get(Servo.class,"hood");

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            //Declaring the motors
            DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
            DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
            DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
            DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
            DcMotor intake = hardwareMap.dcMotor.get("intake");

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

            //More code for edge detectors
            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);
            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            //This is setting the motor power to variables we previously defined
            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            //This is setting the intake power when a bumper is pressed.
            if (gamepad1.right_bumper) {
                intake.setPower(.8);
            }
            else if (gamepad1.left_bumper) {
                intake.setPower(-0.8);
            }
            else intake.setPower(0);

            //This means that, whenever the intake is active, the hood will be down.
            if (gamepad1.right_bumper) {
                Hood.setPosition(.5);
            }
            else Hood.setPosition(.4325);

            //This is a rising edge detector. It cycles between 3 states when a is pressed, adding one to reaperPos after each press. When reaperPos is above 3, it gets set back to 1.
            //This code is changing what position the reaper is at based on what value reaperPos has.
            if (currentGamepad1.a && !previousGamepad1.a) {
                reaperPos++;
            }
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
        }
    }
