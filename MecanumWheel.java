package com.slhs7729.opmodes.sources;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by joseph on 11/1/16.
 */

public class MecanumWheel extends DcMotor {

    public static WheelID orientation;
    public static WheelID side;

    public MecanumWheel(DcMotorController controller, int portNumber) {
        super(controller, portNumber);
        this.controller = controller;
        this.portNumber = portNumber;
    }

    public void setSide(WheelID side) {
        this.side = side;
    }

    public void setOrientation(WheelID orientation) {
        this.orientation = orientation;
    }

    public WheelID getOrientation() { return this.orientation; };

    public WheelID getSide() { return this.side; };

    public static MecanumWheel getMecanumWheel(HardwareMap hardwareMap, String motorName) {

        DcMotor dcMotor;
        DcMotorController motorController;
        int portNumber;
        MecanumWheel mecanumWheel;

        dcMotor = hardwareMap.dcMotor.get(motorName);
        motorController = dcMotor.getController();
        portNumber = dcMotor.getPortNumber();

        mecanumWheel = new MecanumWheel(motorController, portNumber);
        return mecanumWheel;

    }

    public static MecanumWheel getMecanumWheel(DcMotor dcMotor) {

        DcMotorController motorController;
        int portNumber;
        MecanumWheel mecanumWheel;

        motorController = dcMotor.getController();
        portNumber = dcMotor.getPortNumber();

        mecanumWheel = new MecanumWheel(motorController, portNumber);
        return mecanumWheel;

    }
}