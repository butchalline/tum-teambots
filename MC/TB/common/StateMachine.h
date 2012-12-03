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

#ifndef __TBSTATEMACHINE_H__
#define __TBSTATEMACHINE_H__

#include "Config.h"

enum TBState {
	Idle = 0, DrivePosition = 1, DriveVelocity = 2, PositionReached = 3,
	/*...*/
	Error = 42, PhoneDisconnected = 43, PhoneConnect = 44
};

class StateMachine {
public:
	void Init();
	void Call();
	TBState requireState(TBState state);
	TBState getState();

private:
	void preHandle();
	void postHandle();

	void handleVelocity();

private:
	TBState currentState;
};

extern StateMachine stateMachine; //Global StateMachine Object

#endif /*__TBSTATEMACHINE_H__*/

