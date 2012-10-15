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

StateMachine stateMachine; //Global StateMachine Object

void StateMachine::Init() {
	currentState = PhoneDisconnected;
}

void StateMachine::preHandle() {
	if (currentState != PhoneDisconnected) {
		usb.read();
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
		break;
	case DrivePosition:
		break;
	case DriveVelocity:
		break;
	case PositionReached:
		break;

	case PhoneConnect: {
		usb.reconnect();
		if (!usb.isConnected())
			requireState(PhoneDisconnected);
		else
			requireState(Idle);
		break;
	}

	case PhoneDisconnected: {
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

