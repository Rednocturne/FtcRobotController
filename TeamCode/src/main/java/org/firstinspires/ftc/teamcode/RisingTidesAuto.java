package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import androidx.annotation.NonNull;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.ParallelAction;

// Non-RR imports
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.MecanumDrive;

@Config
@Autonomous(name = "RisingTidesAuto", group = "Autonomous")
public  class RisingTidesAuto extends LinearOpMode {}

// intake class
public static class intake {
    private DcMotorEx intake;

    public intake(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public class intakeOn implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            intake.setPower(0.8);
            return false;
        }
    }
    public Action intakeOn() {
        return new intakeOn();
    }

    public class intakeOff implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            intake.setPower(0);
            return false;
        }
    }
    public Action intakeOff() {
        return new intakeOff();
    }


}

// reaper class
public static class reaper {
    private static Servo reaper;

    static final double stow = 0.7;
    final double stack = 0.18;
    static final double ground = 0.11;

    public reaper(HardwareMap hardwareMap) {
        Servo reaper = hardwareMap.get(Servo.class, "reaper");
    }
    public static class reaperStow implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            reaper.setPosition(stow);
            return false;
        }
    }
    public static Action reaperStow() {
        return new reaperStow();
    }
    public static class reaperStack implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            reaper.setPosition(stack);
            return false;
        }
    }
    public static Action reaperStack() {
        return new reaperStack();
    }
    public static class reaperGround implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            reaper.setPosition(ground);
            return false;
        }
    }
    public static Action reaperGround() {
        return new reaperGround();
    }
}
public static class hood {
    private Servo hood;

    final double hoodOpen = 0.57;
    final double hoodClose =  0.4425;

    public hood(HardwareMap hardwareMap) {
        hood = hardwareMap.get(Servo.class, "hood");
    }
    public class hoodOpen implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            hood.setPosition(hoodOpen);
            return false;
        }
    }
    public Action hoodOpen() {
        return new hoodOpen();
    }

    public class hoodClose implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            hood.setPosition(hoodClose);
            return false;
        }
    }
    public Action hoodClose() {
        return new hoodClose();
    }

}
@Override
public void runOpMode() {
    // instantiate your MecanumDrive at a particular pose.
    MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(11.8, 61.7, Math.toRadians(90)));
    // make a Claw instance
    intake intake = new intake(hardwareMap);
    // make a Lift instance
    hood hood = new hood(hardwareMap);

    // vision here that outputs position
    int visionOutputPosition = 1;

    Action trajectoryAction1;
    Action trajectoryAction2;
    Action trajectoryAction3;

    trajectoryAction1 = drive.actionBuilder(drive.pose)
    .setTangent(0)
            .splineTo(new Vector2d(50, 0), Math.PI / 2)
            .waitSeconds(2)
            .lineToX(30)
            .build();
    Actions.runBlocking(hood.hoodClose());
    Actions.runBlocking(reaper.reaperStow());

    while (!isStopRequested() && !opModeIsActive()) {
        int position = visionOutputPosition;
        telemetry.addData("Position during Init", position);
        telemetry.update();
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
