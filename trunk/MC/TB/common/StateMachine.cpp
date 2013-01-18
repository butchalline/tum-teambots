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

#include "common/StateMachine.h"
#include "network/Usb.h"
#include "network/Data.h"
#include "motion/Motor.h"

StateMachine stateMachine; //Global StateMachine Object
TBFrame* receiveFrame;

void StateMachine::Init() {
	currentState = Idle;//PhoneDisconnected;
	receiveFrame = new TBFrame();
}

void StateMachine::handleVelocity() {
	if(currentState != DriveVelocity)
		return;

	if(receiveFrame->head.SubId == TB_VELOCITY_FORWARD) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4, receiveFrame->data.velocity.speedRight * 4, motors.Forwards, motors.Forwards);
	}
	else if(receiveFrame->head.SubId == TB_VELOCITY_BACKWARD) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4, receiveFrame->data.velocity.speedRight * 4, motors.Backwards, motors.Backwards);
	}
	else if(receiveFrame->head.SubId == TB_VELOCITY_TURN_LEFT) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4, receiveFrame->data.velocity.speedRight * 4, motors.Backwards, motors.Forwards);
	}
	else if(receiveFrame->head.SubId == TB_VELOCITY_TURN_RIGHT) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4, receiveFrame->data.velocity.speedRight * 4, motors.Forwards, motors.Backwards);
	}

	return;
}

void StateMachine::preHandle() {
	if (currentState != PhoneDisconnected && usb.sizeData() > 0) {
		u_char bytes = sizeof(TBHeader) - sizeof(u_char); //Head - Primary ID
		char* tmp = (char*)receiveFrame;
		*tmp = usb.read();

		switch(receiveFrame->head.Id) {
		case TB_COMMAND_ID:
			break;
		case TB_VELOCITY_ID:
			bytes += sizeof(TBVelocity);
			for(int i = 0; i < bytes; ++i) { //read rest
				++tmp;
				*tmp = usb.read();
			}
			handleVelocity();
			break;
//		case TB_TURN_ID:
//			break;
		case TB_POSITION_ID:
			break;
		case TB_ERROR_ID:
			break;
		}
	}
}

void StateMachine::postHandle() {
	if (currentState != PhoneDisconnected) {
		usb.send();
	}
}


void StateMachine::Call() {
	this->preHandle();
	switch (currentState) {
	case Idle:
		delay(500);
		Serial.print("Idle\n\r");
		//requireState(DriveVelocity);
		break;
	case DrivePosition:
		break;
	case DriveVelocity:
		Serial.print("Drive Velocity\n\r");
		motors.driveVeloctiy();
		break;
	case PositionReached:
		break;

	case PhoneConnect: {
		Serial.print("Try Connect\n\r");
		usb.reconnect();
		if (!usb.isConnected())
			requireState(PhoneDisconnected);
		else
		{
			Serial.print("Connected\n\r");
			requireState(Idle);
		}
		break;
	}

	case PhoneDisconnected: {
		Serial.print("Phone Disconnected\n\r");
		delay(1000);
		this->requireState(PhoneConnect);
	}
		break;
	case Error:
		break;
	}
	this->postHandle();
}

TBState StateMachine::requireState(TBState state) {
	switch (currentState) {
	case Idle:
		switch (state) {
		case DriveVelocity:
			currentState = DriveVelocity;
			break;
		default:
			//TODO: Log Error
			break;
		}
		return currentState;
	case DrivePosition:
		return currentState;
	case DriveVelocity:
		return currentState;
	case PositionReached:
		return currentState;

	case PhoneConnect: {
		switch (state) {
		case Idle:
			currentState = Idle;
			break;
		case PhoneDisconnected:
			currentState = PhoneDisconnected;
			break;
		default:
			//TODO: Log Error
			break;
		}
		return currentState;
	}

	case PhoneDisconnected: {
		switch (state) {
		case PhoneConnect:
			currentState = PhoneConnect;
			break;
		default:
			//TODO: Log Error
			break;
		}
		return currentState;
	}

	case Error:
		return currentState;
	default:
		return currentState;
	}
}

