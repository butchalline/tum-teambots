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

#if TB_DEBUG_MODE_ENABLED
	template<class T>
	class TBQueue {
		class element {
		public:
			T data;
			struct element* next;
		};
		element* current;
		element* addElement;
	public:
		void push(T item) {
			if(current == NULL) {
				current = new element();
				current->data = item;
				addElement = current;
				return;
			}
			addElement->next = new element();
			addElement = addElement->next;
			addElement->data = item;
		}

		void pop(T& out) {
			if(!hasNext())
				return;
			struct element* ptr = current;
			current = current->next;
			out = ptr->data;
			if(ptr == addElement)
				addElement = NULL;
			delete ptr;
			ptr = NULL;
		}

		bool hasNext() {
			return current != NULL;
		}
	};
#endif

enum TBState {
	Idle = 0,
	DrivePosition = 1,
	DriveVelocity = 2,
	PositionReached = 3,
	/*...*/
	Error = 42,
	PhoneDisconnected = 43,
	PhoneConnect = 44,
	DebugMode = 999
};

#if TB_DEBUG_MODE_ENABLED
enum DebugModeState {
	DebugDoNothing,
	DebugInit,
	DebugRunMotorLeft,
	DebugRunMotorRight,
	DebugRunMotorTablet,
	DebugRunMotorAll,
	DebugReadPoti,
	DebugTestEnvironment,
	DebugSetID_Left,
	DebugSetID_Right,
	DebugSetID_Tablet
};
#endif

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

#if TB_DEBUG_MODE_ENABLED
	void debugStateLoop();
	DebugModeState currentDebugState;
	TBQueue<DebugModeState> queueList;
#endif

private:
	TBState currentState;
};

extern StateMachine stateMachine; //Global StateMachine Object

#endif /*__TBSTATEMACHINE_H__*/

