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

Motor::Motor(u_char idMotor1) :
		motorId1(idMotor1), targetVelocity(0), currentVelocity(0), targetDirection(Forwards), currentDirection(Forwards) {

}

void Motor::Init() {
	pinMode(MOTOR_POWER, OUTPUT);
	pinMode(SERVO_ControlPin, OUTPUT);
	digitalWrite(MOTOR_POWER, _ON);

	for (int i = 1; i < 0xFF; i++) { // This "for" loop will take about 20 Sec to compelet and is used to loop though all speeds that Dynamixel can be and send reset instuction
		long Baudrate_BPS = 0;
		Baudrate_BPS = 2000000 / (i + 1); // Calculate Baudrate as ber "Robotis e-manual"
		control.begin(Baudrate_BPS, SERVO_ControlPin); // Set Ardiuno Serial speed and control pin
		delay(10);
		control.reset(0xFE); // Broadcast to all Dynamixel IDs(0xFE is the ID for all Dynamixel to responed) and Reset Dynamixel to factory default
		delay(100);
	}

	if (motorId1 != 0) {
		// Now that the Dynamixel is reset to factory setting we will program its Baudrate and ID
		Serial.print("motorid1 init\n\r");
		control.begin(1000000, SERVO_ControlPin); // Set Ardiuno Serial speed to factory default speed of 57600
		control.setID(0xFE, SERVO_ID); // Broadcast to all Dynamixel IDs(0xFE) and set with new ID
		control.setBD(SERVO_ID, SERVO_SET_Baudrate); // Set Dynamixel to new serial speed
		control.begin(SERVO_SET_Baudrate, SERVO_ControlPin); // We now need to set Ardiuno to the new Baudrate speed
		control.ledState(SERVO_ID, _ON);                 // Turn Dynamixel LED on
		control.endlessEnable(SERVO_ID, _ON); // Turn Wheel mode OFF, must be on if using wheel mode
		control.torqueMax(SERVO_ID, 0x2FF); // Set Dynamixel to max torque limit
	}
}

void Motor::setVelocity(u_char velocity, Direction direction) {
	targetVelocity = velocity;
	targetDirection = direction;
}


void Motor::driveVeloctiy() {
	if(currentVelocity != targetVelocity || currentDirection != targetDirection) {
		currentVelocity = targetVelocity;
		currentDirection = targetDirection;
	}
	if (targetDirection == Forwards) {
		control.turn(motorId1, RIGHT, currentVelocity);
	} else {
		control.turn(motorId1, LEFT, currentVelocity);
	}
}
