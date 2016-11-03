/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.slhs7729.opmodes.sources;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class MecanumWheel_ExampleTeleOp extends OpMode {

	/*
	 * Note: the configuration of the servos is such that
	 * as the arm servo approaches 0, the arm position moves up (away from the floor).
	 * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
	 */
	// TETRIX VALUES.
	DcMotor motorFrontRight;
	DcMotor motorFrontLeft;
	DcMotor motorBackRight;
	DcMotor motorBackLeft;

	MecanumWheelSet mecanumWheelSetExample1;
	MecanumWheelSet mecanumWheelSetExample2;

	/**
	 * Constructor
	 */
	public MecanumWheel_ExampleTeleOp() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {


		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */

		/*
		 *	From here on the program is custom built. Use this as a base for your program.
		 *	There are two possible ways of configuring your bot. Names can be whatever you
		 *	would like within your program. These are just placeholders.
		 *
		 	* THIS IS THE FIRST EXAMPLE OF HOW
		 */

		motorFrontLeft = hardwareMap.dcMotor.get("motor_FrontLeft");
		motorFrontRight = hardwareMap.dcMotor.get("motor_FrontRight");
		motorBackLeft = hardwareMap.dcMotor.get("motor_BackLeft");
		motorBackRight = hardwareMap.dcMotor.get("motor_BackRight");

		mecanumWheelSetExample1 = new MecanumWheelSet(ModeID.TELE_OP, ModeID.TANK);
		mecanumWheelSetExample1.addMotor(motorBackLeft, WheelID.SIDE_LEFT, WheelID.ORIENT_BACK);
		mecanumWheelSetExample1.addMotor(motorFrontLeft, WheelID.SIDE_LEFT, WheelID.ORIENT_FRONT);
		mecanumWheelSetExample1.addMotor(motorBackRight, WheelID.SIDE_RIGHT, WheelID.ORIENT_BACK);
		mecanumWheelSetExample1.addMotor(motorFrontRight, WheelID.SIDE_RIGHT, WheelID.ORIENT_FRONT);

		/*
			*	THIS IS THE SECOND EXAMPLE OF USING THE MecanumWheelSet
		 */

		mecanumWheelSetExample2 = new MecanumWheelSet(ModeID.TELE_OP, ModeID.FREE_MOVE);
		mecanumWheelSetExample2.addMotor("motor_FrontLeft", hardwareMap, WheelID.SIDE_LEFT, WheelID.ORIENT_FRONT);
		mecanumWheelSetExample2.addMotor("motor_FrontRight", hardwareMap, WheelID.SIDE_RIGHT, WheelID.ORIENT_FRONT);
		mecanumWheelSetExample2.addMotor("motor_BackLeft", hardwareMap, WheelID.SIDE_LEFT, WheelID.ORIENT_BACK);
		mecanumWheelSetExample2.addMotor("motor_BackRight", hardwareMap, WheelID.SIDE_RIGHT, WheelID.ORIENT_BACK);
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		telemetry.addData("Text", "*** Robot Data***");


		/*
		 *	This is an example of how one would call upon the programming built into the
		 *	MecanumWheelSet class.
		 */

		//Using Buttons to change movement path
		if (gamepad1.x) {
			mecanumWheelSetExample1.setDriveMode(ModeID.TANK);
		} else if (gamepad1.b) {
			mecanumWheelSetExample1.setDriveMode(ModeID.FREE_MOVE);
		}



		if (mecanumWheelSetExample1.getDriveMode() == ModeID.TANK) {
			//Tank Drive
			float yPad1 = gamepad1.left_stick_y;
			float yPad2 = gamepad1.right_stick_y;

			mecanumWheelSetExample1.move(yPad1, yPad2);
		} else if (mecanumWheelSetExample1.getDriveMode() == ModeID.FREE_MOVE) {
			// Free Movement
			float directionX = gamepad1.left_stick_x;
			float directionY = gamepad1.left_stick_y;

			mecanumWheelSetExample1.move(directionX, directionY);
		}

	}
	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

    	
	/*
	 * This method scales the joystick input so for low joystick values, the 
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };
		
		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);
		
		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}

}
