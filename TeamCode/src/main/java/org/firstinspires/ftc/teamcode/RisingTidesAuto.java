package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;

// Non-RR imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Config
@Autonomous(name = "RisingTidesAuto", group = "Autonomous")
public class RisingTidesAuto extends LinearOpMode {

    // intake class
    public  class Intake {
        private DcMotorEx intake;

        public Intake(HardwareMap hardwareMap) {
            intake = hardwareMap.get(DcMotorEx.class, "intake");
            intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intake.setDirection(DcMotorSimple.Direction.FORWARD);
        }

        public class IntakeOn implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                intake.setPower(0.8);
                return false;
            }
        }

        public Action intakeOn() {
            return new IntakeOn();
        }

        public class IntakeOff implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                intake.setPower(0);
                return false;
            }
        }

        public Action intakeOff() {
            return new IntakeOff();
        }
    }

    // reaper class
    public class Reaper {
        private  Servo reaper;

         final double stow = 0.7;
         final double stack = 0.18;
         final double ground = 0.11;

        public Reaper(HardwareMap hardwareMap) {
            reaper = hardwareMap.get(Servo.class, "reaper");
        }

        public class ReaperStow implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                reaper.setPosition(stow);
                return false;
            }
        }

        public  Action reaperStow() {
            return new ReaperStow();
        }

        public  class ReaperStack implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                reaper.setPosition(stack);
                return false;
            }
        }

        public Action reaperStack() {
            return new ReaperStack();
        }

        public class ReaperGround implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                reaper.setPosition(ground);
                return false;
            }
        }

        public Action reaperGround() {
            return new ReaperGround();
        }
    }

    // hood class
    public class Hood {
        private Servo hood;

        final double hoodOpen = 0.57;
        final double hoodClose = 0.4425;

        public Hood(HardwareMap hardwareMap) {
            hood = hardwareMap.get(Servo.class, "hood");
        }

        public class HoodOpen implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                hood.setPosition(hoodOpen);
                return false;
            }
        }

        public Action hoodOpen() {
            return new HoodOpen();
        }

        public class HoodClose implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                hood.setPosition(hoodClose);
                return false;
            }
        }

        public Action hoodClose() {
            return new HoodClose();
        }

    }

    @Override
    public void runOpMode() {
        // instantiate your MecanumDrive at a particular pose.
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(11.8, 61.7, Math.toRadians(90)));
        // make a Intake instance
        Intake intake = new Intake(hardwareMap);
        // make a Hood instance
        Hood hood = new Hood(hardwareMap);
        // make a Reaper Instance
        Reaper reaper = new Reaper(hardwareMap);

        // vision here that outputs position
        int visionOutputPosition = 1;

        Action trajectoryAction1;
        Action trajectoryAction2;
        Action trajectoryAction3;
        Action trajectoryActionCloseOut;
        Action sleepAction;

        trajectoryAction1 = drive.actionBuilder(drive.pose)
                .setTangent(0)
                .splineTo(new Vector2d(50, 61.7), Math.PI / 2)
                .waitSeconds(2)
                .lineToX(30)
                .build();
        trajectoryAction2 = drive.actionBuilder(drive.pose)
                .build();
        trajectoryAction3 = drive.actionBuilder(drive.pose)
                .build();
        trajectoryActionCloseOut = drive.actionBuilder(drive.pose)
                .build();
        sleepAction = drive.actionBuilder(drive.pose)
                .waitSeconds(1)
                .build();

        while (!isStopRequested() && !opModeIsActive()) {
            int position = visionOutputPosition;
            telemetry.addData("Position during Init", position);
            telemetry.update();
            reaper.reaperStow();
            hood.hoodClose();
        }
        int startPosition = visionOutputPosition;
        telemetry.addData("Starting Position", startPosition);
        telemetry.update();
        waitForStart();


        if (isStopRequested()) return;


        Action trajectoryActionChosen;
        if (startPosition == 1) {
            trajectoryActionChosen = trajectoryAction1;
        } else if (startPosition == 2) {
            trajectoryActionChosen = trajectoryAction2;
        } else {
            trajectoryActionChosen = trajectoryAction3;
        }

        Actions.runBlocking(
                new SequentialAction(
                        trajectoryActionChosen,
                        reaper.reaperStack(),
                        intake.intakeOn(),
                        hood.hoodOpen(),
                        reaper.reaperStow(),
                        intake.intakeOff(),
                        hood.hoodClose(),
                        trajectoryActionCloseOut
                )
        );
    }
}
