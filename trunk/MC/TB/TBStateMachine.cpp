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
  currentState = PhoneDisconnected;
  requiredState = ;
}

void TBStateMachine::preHandle()
{
  if(currentState != PhoneDisconnected)
  {
  //usb.read();
  }
}

void TBStateMachine::postHandle()
{
  if(currentState != PhoneDisconnected)
  {
    usb.send();
  }
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
    case PhoneDisconnected:
    {
      if(usb.isConnected())
        this->requireState(Idle);
      else
        delay(1000);
    } break;
    case Error
    break;
  }
  this->postHandle();
}

TBState TBStateMachine::requireState(TBState state)
{
  
}

