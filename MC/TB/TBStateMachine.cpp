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
 
#include "TBStateMachine.h"

TBStateMachine stateMachine; //Global StateMachine Object

void TBStateMachine::Init()
{
  
}

void TBStateMachine::preHandle()
{
  //usb.receive(iBuf, iSize);
}

void TBStateMachine::postHandle()
{
  //usb.send(oBuf, oSize);
}

void TBStateMachine::Call()
{
  this->preHandle();
  switch(currentState)
  {
    case Idle:
    break;
    case DrivePosition:
    break;
    case DriveVelocity:
    break;
    case PositionReached:
    break;
  }
  this->postHandle();
}

TBState TBStateMachine::requireState(TBState state)
{
  
}
