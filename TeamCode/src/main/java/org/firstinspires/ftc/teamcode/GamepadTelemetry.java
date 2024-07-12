package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp()
public class GamepadTelemetry extends OpMode {
    @Override
    public void init() {
    }

    @Override
    public void loop() {
        double speedForward = -gamepad1.right_stick_y /2.0;
         if (gamepad1.a == true){
             speedForward = gamepad1.right_stick_y;
        }
    telemetry.addData("Speed", speedForward);
            telemetry.addData("Ystick", gamepad1.right_stick_y);
    }
}