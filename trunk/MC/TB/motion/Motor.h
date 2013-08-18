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

#ifndef __TBMOTOR_H__
#define __TBMOTOR_H__

#include <Dynamixel_Serial.h>
#include "Config.h"
//#include "Control.h"

class Motor {
public:
	enum Direction {
		Forwards,
		Backwards
	};

	Motor(u_char = MOTOR_ID_LEFT, u_char = MOTOR_ID_RIGHT, u_char = MOTOR_ID_TABLET); // if id != 0 then Motor
	void Init();
	void setVelocity(u_short velocityRight, u_short velocityLeft, Direction directionRight = Forwards, Direction direchtionLeft = Forwards);
	void setTabletPosition(u_short, u_short);
	void driveVeloctiy();
#if TB_DEBUG_MODE_ENABLED
	void setID(int newMotorID);
#endif
private:
	DynamixelClass control;
	u_char motorIdLeft;
	u_char motorIdRight;
	u_char motorIdTablet;

	u_short targetVelocityLeft;
	u_short currentVelocityLeft;
	Direction targetDirectionLeft;
	Direction currentDirectionLeft;

	u_short targetVelocityRight;
	u_short currentVelocityRight;
	Direction targetDirectionRight;
	Direction currentDirectionRight;

	u_short targetPositionTablet;
	u_short currentPositionTablet;
	u_short targetVelocityTablet;
	u_short currentVelocityTablet;
};

extern Motor motors;

#endif
