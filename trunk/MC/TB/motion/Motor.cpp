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
		targetVelocityRight(0), currentVelocityRight(-1), targetDirectionRight(Forwards), currentDirectionRight(Forwards), velocityControlerLeft(Left), velocityControlerRight(Right) {

}

void Motor::Init() {
	pinMode(MOTOR_POWER, OUTPUT);
	digitalWrite(MOTOR_POWER, HIGH);
	delay(1500);
	control.begin(SERVO_SET_Baudrate, SERVO_ControlPin); // Set Ardiuno Serial speed to factory default speed of 57600

	if (motorIdLeft != 0) {
		// Now that the Dynamixel is reset to factory setting we will program its Baudrate and ID
		Serial.print("\n\rmotorLeft init\n\r");
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
	if(velocityLeft > MAX_USER_VELOCITY)
		velocityLeft = MAX_USER_VELOCITY;
	if(velocityRight > MAX_USER_VELOCITY)
		velocityRight = MAX_USER_VELOCITY;
	targetVelocityLeft = velocityLeft;
	targetDirectionLeft = directionLeft;
	targetVelocityRight = velocityRight;
	targetDirectionRight = directionRight;
}


void Motor::driveVeloctiy() {
//	if(!(currentVelocityLeft != targetVelocityLeft || currentDirectionLeft != targetDirectionLeft || currentVelocityRight != targetVelocityRight || currentDirectionRight != targetDirectionRight))
//		return;
	currentVelocityLeft = targetVelocityLeft;
	currentDirectionLeft = targetDirectionLeft;
	currentVelocityRight = targetVelocityRight;
	currentDirectionRight = targetDirectionRight;
	if (targetDirectionLeft == Forwards) {
		control.turn(motorIdLeft, LEFT, velocityControlerLeft.controlVel(currentVelocityLeft));
		Serial.print("MotorLeft Forwards ");
		Serial.print(currentVelocityLeft);
		Serial.print("\n\r");
	} else {
		control.turn(motorIdLeft, RIGHT, velocityControlerLeft.controlVel(currentVelocityLeft));
		Serial.print("MotorLeft Backwards ");
		Serial.print(currentVelocityLeft);
		Serial.print("\n\r");
	}

	if (targetDirectionRight == Forwards) {
		control.turn(motorIdRight, RIGHT, velocityControlerRight.controlVel(currentVelocityRight));
		Serial.print("MotorRight Forwards ");
		Serial.print(currentVelocityRight);
		Serial.print("\n\r");
	} else {
		control.turn(motorIdRight, LEFT, velocityControlerRight.controlVel(currentVelocityRight));
		Serial.print("MotorRight Backwards ");
		Serial.print(currentVelocityRight);
		Serial.print("\n\r");
	}
}
void Motor::readPosition(){
	Serial.print("Funktion read Position startet\n");


	//Niklas code zum Test Serial read
	int Temperature, Voltage, Position, Ping;
	digitalWrite(0x06, HIGH);
	delay(1000);
	//pinMode(0x02, OUTPUT);
	//pinMode(0x03, OUTPUT);
	digitalWrite(0x03, LOW);
	control.begin(1000000,0x02);
	delay(1000);
	control.endlessEnable(SERVO_ID, HIGH);
	control.torqueMax(SERVO_ID, 0x2FF);
	control.turn(SERVO_ID,LEFT,0x200);

//	control.begin(1000000,0x02);  // Inicialize the servo at 1Mbps and Pin Control 2

	delay(1000);
	control.turn(SERVO_ID,RIGHT,0x200);
	delay(500);
	control.turn(SERVO_ID,LEFT,0x200);
	delay(1000);
	control.turn(SERVO_ID,RIGHT,0x200);
	delay(3000);
	control.turn(SERVO_ID,LEFT,0x200);
	delay(7000);
	control.turn(SERVO_ID,RIGHT,0x200);
//	  Temperature = control.readTemperature(0x01); // Request and Print the Temperature
//	  Voltage = control.readVoltage(0x01);         // Request and Print the Voltage
//	  Position = control.readPosition(0x01);       // Request and Print the Position
//	  Ping = control.ping(0x01);

//	  Temperature = control.move(SERVO_ID,random(200,800));  // Move the Servo radomly from 200 to 800
//
	  control.end();                 // End Servo Comunication
	 //Serial.begin(9600);              // Begin Serial Comunication

	  Serial.print(" *** Temperature: ");   // Print the variables in the Serial Monitor
	  Serial.print(Temperature);
	  Serial.print(" Celcius  Voltage: ");
	  Serial.print(Voltage);
	  Serial.print("   Ping: ");
	  Serial.print(Ping);
	  Serial.print("  Volts   Position: ");
	  Serial.print(Position);
	  Serial.println(" of 1023 resolution");

	 //Serial.end();                     // End the Serial Comunication
	 control.begin(1000000,0x2);         // Begin Servo Comunication

//	delay(1000);
//	control.ledState(SERVO_ID, true);
//	 delay(1000);
//	 control.ledState(SERVO_ID, false);
//
//	 control.turn(SERVO_ID,RIGHT,0x3FF);
//	  delay(1000);
//	  control.ledState(SERVO_ID, true);
//	   delay(1000);
//	  control.ledState(SERVO_ID, false);
//
//	  control.turn(SERVO_ID,RIGHT,0x3FF);
//ende niklas code

//	int posReturnValueRight = control.readPosition(motorIdRight);
//	int posReturnValueLeft = control.readPosition(motorIdLeft);

//	if (posReturnValueRight < 0 || posReturnValueLeft < 0){ //return failure
//		Serial.print("Motor right returns failure ");
//		Serial.print(posReturnValueRight);
//		Serial.print("\n\r");
//		Serial.print("Motor left returns failure ");
//		Serial.print(posReturnValueLeft);
//		Serial.print("\n\r");
//		return;
//	}
//	Serial.print("motor pos right: ");
//	Serial.print(posReturnValueRight);
//	Serial.print("\n\r");
//	Serial.print("motor pos left: ");
//	Serial.print(posReturnValueLeft);
//	Serial.print("\n\r");
//	return;

}

void Motor::setID(int newMotorID){

delay(1000);                                           // Give time for Dynamixel to start on power-up

	 for (int i=1; i<0xFF; i++){                            // This "for" loop will take about 20 Sec to compelet and is used to loop though all speeds that Dynamixel can be and send reset instuction
	    long Baudrate_BPS = 0;
	    Baudrate_BPS  = 2000000 / (i + 1);                   // Calculate Baudrate as ber "Robotis e-manual"
	      control.begin(Baudrate_BPS ,SERVO_ControlPin);   // Set Ardiuno Serial speed and control pin
	      delay(10);
	        control.reset(0xFE);                           // Broadcast to all Dynamixel IDs(0xFE is the ID for all Dynamixel to responed) and Reset Dynamixel to factory default
	        delay(100);
	 }
	 control.begin(1000000,SERVO_ControlPin);                  // Set Ardiuno Serial speed to factory default speed of 57600
	  control.setID(0xFE,SERVO_ID);                           // Broadcast to all Dynamixel IDs(0xFE) and set with new ID
	  control.setBD(newMotorID,SERVO_SET_Baudrate);             // Set Dynamixel to new serial speed
	  control.begin(SERVO_SET_Baudrate,SERVO_ControlPin);     // We now need to set Ardiuno to the new Baudrate speed
	  control.ledState(newMotorID, true);                         // Turn Dynamixel LED on
	  control.endlessEnable(newMotorID, true);                   // Turn Wheel mode OFF, must be on if using wheel mode
	  control.torqueMax(newMotorID, 0x2FF);                     // Set Dynamixel to max torque limit
	  Serial.print("Motor-ID wurde gesetzt");
	  control.turn(newMotorID,LEFT,0x3FF);              // Comman for Wheel mode, Move left at max speed
	  delay(5000);
	  }
