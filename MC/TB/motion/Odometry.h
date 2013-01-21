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

#ifndef __ODOMETRY_H__
#define __ODOMETRY_H__

#include <Dynamixel_Serial.h>
#include "Config.h"

class Odometry {
public:

void Init();
void getInkr();
void calcPosition();
void update(unsigned long deltaTime);

unsigned short actVelocityRight;	//Geschwindigkeit in mm/sec
unsigned short actVelocityLeft;		//Geschwindigkeit in mm/sec

private:
	u_char inkrRight;
	u_char inkrLeft;
	u_char prevInkrRight;
	u_char prevInkrLeft;
	char deltaInkrLeft;		//Deltastrecke in inkrementen
	char deltaInkrRight;
	short currX;
	short currY;
	short prevX;
	short prevY;
	float currAngle;
	float prevAngle;
	short roundsLeft;
	short roundsRight;
	short distLeft;		//delta Strecke in mm
	short distRight;
	unsigned long timeStep;

};

extern Odometry odometry;

#endif
