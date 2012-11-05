/*
 * TeamBots is a Student-Project of the Technical University Munich - 2012
 * Members:
 * Niklas Boehme, Matthias Freysoldt, Aaron Frueh, Artur Lohrer, Alexander Reimann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "Motor.h"

Motor motors;

Motor::Motor(u_char idMotor1, u_char idMotor2) :
		motorIdLeft(idMotor1), motorIdRight(idMotor2), targetVelocityLeft(0), currentVelocityLeft(-1), targetDirectionLeft(Forwards), currentDirectionLeft(Forwards),
		targetVelocityRight(0), currentVelocityRight(-1), targetDirectionRight(Forwards), currentDirectionRight(Forwards) {

}

void Motor::Init() {
	pinMode(MOTOR_POWER, OUTPUT);
	digitalWrite(MOTOR_POWER, HIGH);

	control.begin(SERVO_SET_Baudrate, SERVO_ControlPin); // Set Ardiuno Serial speed to factory default speed of 57600

	if (motorIdLeft != 0) {
		// Now that the Dynamixel is reset to factory setting we will program its Baudrate and ID
		Serial.print("\n\rmotorid1 init\n\r");
		control.ledState(motorIdLeft, HIGH);                 // Turn Dynamixel LED on
		control.endlessEnable(motorIdLeft, HIGH); // Turn Wheel mode OFF, must be on if using wheel mode
		control.torqueMax(motorIdLeft, 0x2FF); // Set Dynamixel to max torque limit
	}

	if (motorIdRight != 0) {
		// Now that the Dynamixel is reset to factory setting we will program its Baudrate and ID
		Serial.print("motorIdRight init\n\r");
		control.ledState(motorIdRight, HIGH);                 // Turn Dynamixel LED on
		control.endlessEnable(motorIdRight, HIGH); // Turn Wheel mode OFF, must be on if using wheel mode
		control.torqueMax(motorIdRight, 0x2FF); // Set Dynamixel to max torque limit
	}
}

void Motor::setVelocity(u_short velocityLeft,  u_short velocityRight, Direction directionLeft, Direction directionRight) {
	targetVelocityLeft = velocityLeft;
	targetDirectionLeft = directionLeft;
	targetVelocityRight = velocityRight;
	targetDirectionRight = directionRight;
}


void Motor::driveVeloctiy() {
	if(!(currentVelocityLeft != targetVelocityLeft || currentDirectionLeft != targetDirectionLeft || currentVelocityRight != targetVelocityRight || currentDirectionRight != targetDirectionRight))
		return;
	currentVelocityLeft = targetVelocityLeft;
	currentDirectionLeft = targetDirectionLeft;
	currentVelocityRight = targetVelocityRight;
	currentDirectionRight = targetDirectionRight;
	Serial.print("Motor Drive: ");
	if (targetDirectionLeft == Forwards) {
		control.turn(motorIdLeft, RIGHT, currentVelocityLeft);
		Serial.print("MotorLeft Forwards ");
		Serial.print(currentVelocityLeft);
		Serial.print("\n\r");
	} else {
		control.turn(motorIdLeft, LEFT, currentVelocityLeft);
		Serial.print("MotorLeft Backwards ");
		Serial.print(currentVelocityLeft);
		Serial.print("\n\r");
	}

	if (targetDirectionRight == Forwards) {
		control.turn(motorIdRight, RIGHT, currentVelocityRight);
		Serial.print("MotorRight Forwards ");
		Serial.print(currentVelocityRight);
		Serial.print("\n\r");
	} else {
		control.turn(motorIdRight, LEFT, currentVelocityRight);
		Serial.print("MotorRight Backwards ");
		Serial.print(currentVelocityRight);
		Serial.print("\n\r");
	}
}
void Motor::readPosition(){

	int posReturnValueRight = control.readPosition(motorIdRight);
	int posReturnValueLeft = control.readPosition(motorIdLeft);

	if (posReturnValueRight < 0 || posReturnValueLeft < 0){ //return failure
		Serial.print("Motor right returns failure ");
		Serial.print(posReturnValueRight);
		Serial.print("\n\r");
		Serial.print("Motor left returns failure ");
		Serial.print(posReturnValueLeft);
		Serial.print("\n\r");
		return;
	}
	Serial.print("motor pos right: ");
	Serial.print(posReturnValueRight);
	Serial.print("\n\r");
	Serial.print("motor pos left: ");
	Serial.print(posReturnValueLeft);
	Serial.print("\n\r");
	return;
}
