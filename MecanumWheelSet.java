package com.slhs7729.opmodes.sources;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.ArrayList;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by joseph on 11/1/16.
 *
 * If you are using this class as a main point for all Mecanum Wheels please read the following:
 *
 *      The following code will run through all of the wheels you have, please create all MecanumWheelSet's
 *      with an exact number of 4 wheels as this will be how the code decides movement
 */

public class MecanumWheelSet {

    public ArrayList<MecanumWheel> wheelSet = new ArrayList<MecanumWheel>();
    public ModeID opMode;

    public ModeID driveMode;
    public Telemetry telemetry;

    public MecanumWheelSet(ModeID opMode, ModeID driveMode) {
        this.opMode = opMode;
        this.driveMode = driveMode;
    }

    public ModeID getDriveMode() { return driveMode; }

    public void setDriveMode(ModeID driveMode) { this.driveMode = driveMode; }

    public void addMotor(String configName, HardwareMap hardwareMap, WheelID side, WheelID orientation) {
        MecanumWheel mecanumWheel = MecanumWheel.getMecanumWheel(hardwareMap, configName);
        if (side == WheelID.SIDE_LEFT) {
            mecanumWheel.setDirection(DcMotor.Direction.REVERSE);
            mecanumWheel.setSide(side);
        } else {
            mecanumWheel.setSide(side);
        }
        mecanumWheel.setOrientation(orientation);
        wheelSet.add(mecanumWheel);
    }

    public void addMotor(DcMotor dcMotor, WheelID side, WheelID orientation) {
        MecanumWheel mecanumWheel = MecanumWheel.getMecanumWheel(dcMotor);
        if (side == WheelID.SIDE_LEFT) {
            mecanumWheel.setDirection(DcMotor.Direction.REVERSE);
            mecanumWheel.setSide(side);
        } else {
            mecanumWheel.setSide(side);
        }
        mecanumWheel.setOrientation(orientation);
        wheelSet.add(mecanumWheel);
    }

    public void setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void setOpMode(ModeID mode) {
        this.opMode = mode;
    }

    public ModeID getOpMode() { return this.opMode; }

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

    public void move(float directionX, float directionY) {

        if (opMode == ModeID.TELE_OP) {

            float powerX = Range.clip(directionX, -1, 1);
            float powerY = Range.clip(directionY, -1, 1);

            powerX = (float)scaleInput(powerX);
            powerY =  (float)scaleInput(powerY);

            if (driveMode == ModeID.TANK) {
                for (int i = 0; i < wheelSet.size(); i++) {
                    WheelID side = wheelSet.get(i).getSide();
                    if (side == WheelID.SIDE_LEFT) {
                        wheelSet.get(i).setPower(directionY);
                    } else if (side == WheelID.SIDE_RIGHT) {
                        wheelSet.get(i).setPower(directionX);
                    } else {
                        return;
                    }
                }
            } else if (driveMode == ModeID.FREE_MOVE) {

                double powerA;
                double powerB;

                powerA = freeMoveScale(powerX, powerY, ModeID.X_SCALE);
                powerB = freeMoveScale(powerX, powerY, ModeID.Y_SCALE);

                for (int i = 0; i < wheelSet.size(); i++) {
                    WheelID side = wheelSet.get(i).getSide();
                    WheelID orient = wheelSet.get(i).getOrientation();
                    switch (orient) {
                        case ORIENT_FRONT:
                            if (side == WheelID.SIDE_LEFT) {
                                wheelSet.get(i).setPower(powerA);
                            } else if (side == WheelID.SIDE_RIGHT) {
                                wheelSet.get(i).setPower(powerB);
                            } else {
                                telemetry.addData("Text", "Program ended because wheel #" + i + " dd not have a side.");
                                System.exit(0);
                            }
                            break;
                        case ORIENT_BACK:
                            if (side == WheelID.SIDE_LEFT) {
                                wheelSet.get(i).setPower(powerB);
                            } else if (side == WheelID.SIDE_RIGHT) {
                                wheelSet.get(i).setPower(powerA);
                            } else {
                                telemetry.addData("Text", "Program ended because wheel #" + i + " dd not have a side.");
                                System.exit(0);
                            }
                            break;
                        case ORIENT_MIDDLE:
                            if (side == WheelID.SIDE_LEFT) {
                                wheelSet.get(i).setPower(0.0);
                            } else if (side == WheelID.SIDE_RIGHT) {
                                wheelSet.get(i).setPower(0.0);
                            } else {
                                telemetry.addData("Text", "Program ended because wheel #" + i + " dd not have a side.");
                                System.exit(0);
                            }
                            break;
                        default:
                            telemetry.addData("Text", "Program ended because wheel #" + i + " did not have a orientation.");
                            System.exit(0);
                            break;
                    }
                }
            } else {

                return;

            }

        } else {
            return;
        }

    }

    public double freeMoveScale(float x, float y, ModeID scale) {

        if (x != 0 && y != 0) {
            if (scale == ModeID.X_SCALE) {
                double tempX = Math.abs(x);
                double tempY = Math.abs(y);
                double angle = Math.atan(tempX / tempY);
                double scl = Math.sin(angle);
                if (x < 0) {
                    scl = scl * -1;
                }
                return x * scl;
            } else if (scale == ModeID.Y_SCALE) {
                double tempX = Math.abs(x);
                double tempY = Math.abs(y);
                double angle = Math.atan(tempX / tempY);
                double scl = Math.cos(angle);
                if (x < 0) {
                    scl = scl * -1;
                }
                return y * scl;
            } else {
                telemetry.addData("Text", "Program ended becuase of a error scaling");
                System.exit(0);
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void moveForward(double power) {

        if (power < 0.0) {
            power = -power;
        }

        if (opMode == ModeID.AUTONOMOUS) {
            for (int i = 0; i < wheelSet.size(); i++) {
                wheelSet.get(i).setPower(power);
            }
        } else  {
            return;
        }

    }

    public void moveLeft(double power) {

        if (opMode == ModeID.AUTONOMOUS) {
            for (int i = 0; i < wheelSet.size(); i++) {
                WheelID side = wheelSet.get(i).getSide();
                WheelID orient = wheelSet.get(i).getOrientation();
                switch (orient) {
                    case ORIENT_FRONT:
                        if (side == WheelID.SIDE_RIGHT) {
                            wheelSet.get(i).setPower(power);
                        } else if (side == WheelID.SIDE_LEFT) {
                            wheelSet.get(i).setPower(-power);
                        } else {
                            telemetry.addData("Text", "Program crashed because side was not set.");
                            System.exit(0);
                        }
                        break;
                    case ORIENT_MIDDLE:
                        if (side == WheelID.SIDE_RIGHT) {
                            wheelSet.get(i).setPower(0);
                        } else if (side == WheelID.SIDE_LEFT) {
                            wheelSet.get(i).setPower(0);
                        } else {
                            telemetry.addData("Text", "Program crashed because side was not set.");
                            System.exit(0);
                        }
                        break;
                    case ORIENT_BACK:
                        if (side == WheelID.SIDE_RIGHT) {
                            wheelSet.get(i).setPower(power);
                        } else if (side == WheelID.SIDE_LEFT) {
                            wheelSet.get(i).setPower(-power);
                        } else {
                            telemetry.addData("Text", "Program crashed because side was not set.");
                            System.exit(0);
                        }
                        break;
                    default:
                        telemetry.addData("Text", "Program crashed because orientation was not set.");
                        System.exit(0);
                        break;
                }
            }
        } else  {
            return;
        }

    }

    public void moveRight(double power) {

        if (opMode == ModeID.AUTONOMOUS) {
            for (int i = 0; i < wheelSet.size(); i++) {
                WheelID side = wheelSet.get(i).getSide();
                WheelID orient = wheelSet.get(i).getOrientation();
                switch (orient) {
                    case ORIENT_FRONT:
                        if (side == WheelID.SIDE_RIGHT) {
                            wheelSet.get(i).setPower(-power);
                        } else if (side == WheelID.SIDE_LEFT) {
                            wheelSet.get(i).setPower(power);
                        } else {
                            telemetry.addData("Text", "Program crashed because side was not set.");
                            System.exit(0);
                        }
                        break;
                    case ORIENT_MIDDLE:
                        if (side == WheelID.SIDE_RIGHT) {
                            wheelSet.get(i).setPower(0);
                        } else if (side == WheelID.SIDE_LEFT) {
                            wheelSet.get(i).setPower(0);
                        } else {
                            telemetry.addData("Text", "Program crashed because side was not set.");
                            System.exit(0);
                        }
                        break;
                    case ORIENT_BACK:
                        if (side == WheelID.SIDE_RIGHT) {
                            wheelSet.get(i).setPower(-power);
                        } else if (side == WheelID.SIDE_LEFT) {
                            wheelSet.get(i).setPower(power);
                        } else {
                            telemetry.addData("Text", "Program crashed because side was not set.");
                            System.exit(0);
                        }
                        break;
                    default:
                        telemetry.addData("Text", "Program crashed because orientation was not set.");
                        System.exit(0);
                        break;
                }
            }
        } else  {
            return;
        }

    }

    public void moveBackward(double power) {

        if (power > 0.0) {
            power = -power;
        }

        if (opMode == ModeID.AUTONOMOUS) {
            for (int i = 0; i < wheelSet.size(); i++) {
                wheelSet.get(i).setPower(power);
            }
        } else  {
            return;
        }

    }

}
