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

Motor::Motor(u_char idMotorLeft, u_char idMotorRight, u_char idTablet) :
		motorIdLeft(idMotorLeft), motorIdRight(idMotorRight), motorIdTablet(
				idTablet), targetVelocityLeft(0), currentVelocityLeft(0), targetDirectionLeft(
				Forwards), currentDirectionLeft(Forwards), targetVelocityRight(
				0), currentVelocityRight(0), targetDirectionRight(Forwards), currentDirectionRight(
				Forwards), targetVelocityTablet(0), currentVelocityTablet(0), targetDirectionTablet(
				Forwards), currentDirectionTablet(Forwards) {
}

void Motor::Init() {
	pinMode(MOTOR_POWER, OUTPUT);
	digitalWrite(MOTOR_POWER, HIGH);
	delay(1500);
	control.begin(SERVO_SET_Baudrate, SERVO_ControlPin); // Set Ardiuno Serial speed to factory default speed of 57600
	// Now that the Dynamixel is reset to factory setting we will program its Baudrate and ID
	Serial.print("\n\rmotorLeft init\n\r ID: ");
	Serial.print(motorIdLeft);
	Serial.print("\n\r");
	control.ledState(motorIdLeft, HIGH);                // Turn Dynamixel LED on
	control.endlessEnable(motorIdLeft, HIGH); // Turn Wheel mode OFF, must be on if using wheel mode
	control.torqueMax(motorIdLeft, 0x2FF); // Set Dynamixel to max torque limit

	// Now that the Dynamixel is reset to factory setting we will program its Baudrate and ID
	Serial.print("motorIdRight init\n\r ID: ");
	Serial.print(motorIdRight);
	Serial.print("\n\r");
	control.ledState(motorIdRight, HIGH);               // Turn Dynamixel LED on
	control.endlessEnable(motorIdRight, HIGH); // Turn Wheel mode OFF, must be on if using wheel mode
	control.torqueMax(motorIdRight, 0x2FF); // Set Dynamixel to max torque limit

	Serial.print("motorIdTablet init\n\r ID: ");
	Serial.print(motorIdTablet);
	Serial.print("\n\r");
	control.ledState(motorIdTablet, HIGH);              // Turn Dynamixel LED on
	control.endlessEnable(motorIdTablet, HIGH); // Turn Wheel mode OFF, must be on if using wheel mode
	control.torqueMax(motorIdTablet, 0x2FF); // Set Dynamixel to max torque limit
}

void Motor::setVelocity(u_short velocityLeft, u_short velocityRight, Direction directionLeft, Direction directionRight) {
	if (velocityLeft > MAX_USER_VELOCITY)
		velocityLeft = MAX_USER_VELOCITY;

	if (velocityRight > MAX_USER_VELOCITY)
		velocityRight = MAX_USER_VELOCITY;

	targetVelocityLeft = velocityLeft;
	targetDirectionLeft = directionLeft;
	targetVelocityRight = velocityRight;
	targetDirectionRight = directionRight;
}

void Motor::setTabletVelocity(u_short velocityTablet, Direction tabletDirection) {
	if (velocityTablet > MAX_USER_VELOCITY)
		velocityTablet = MAX_USER_VELOCITY;

	targetVelocityTablet = velocityTablet;
	targetDirectionTablet = tabletDirection;
}

void Motor::driveVeloctiy() {
	if (currentVelocityLeft != targetVelocityLeft || currentDirectionLeft != targetDirectionLeft) {
		currentVelocityLeft = targetVelocityLeft;
		currentDirectionLeft = targetDirectionLeft;
		if (currentDirectionLeft == Forwards)
			control.turn(motorIdLeft, LEFT, currentVelocityLeft);
		else
			control.turn(motorIdLeft, RIGHT, currentVelocityLeft);
	}
	if (currentVelocityRight != targetVelocityRight || currentDirectionRight != targetDirectionRight) {
		currentVelocityRight = targetVelocityRight;
		currentDirectionRight = targetDirectionRight;
		if (currentDirectionRight == Forwards)
			control.turn(motorIdRight, RIGHT, currentVelocityRight);
		else
			control.turn(motorIdRight, LEFT, currentVelocityRight);
	}
	if (currentVelocityTablet != targetVelocityTablet || currentDirectionTablet != targetDirectionTablet) {
		currentVelocityTablet = targetVelocityTablet;
		currentDirectionTablet = targetDirectionTablet;
		if (currentDirectionTablet == Forwards)
			control.turn(motorIdTablet, LEFT, currentVelocityTablet);
		else
			control.turn(motorIdTablet, RIGHT, currentVelocityTablet);
	}
}

void Motor::setID(int newMotorID) {
	Serial.print("Reset every possible baut rate => takes about 10 Seconds \n\r");
	delay(1000);
	control.begin(9600, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(19200, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(57600, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(115200, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(200000, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(250000, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(400000, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(500000, SERVO_ControlPin);
	control.reset(0xFE);
	delay(1000);
	control.begin(1000000, SERVO_ControlPin); // Set Ardiuno Serial speed and control pin
	control.reset(0xFE);
	delay(1000);
	Serial.print("\n\r finished Resetting - Set New ID\n\r");
	control.setID(0xFE, newMotorID); // Broadcast to all Dynamixel IDs(0xFE) and set with new ID
	control.ledState(newMotorID, true);                 // Turn Dynamixel LED on
	control.endlessEnable(newMotorID, true); // Turn Wheel mode OFF, must be on if using wheel mode
	control.torqueMax(newMotorID, 0x2FF);   // Set Dynamixel to max torque limit
	Serial.print("Motor-ID wurde gesetzt\n\r");
}
