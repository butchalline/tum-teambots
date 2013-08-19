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


#include "sensor/Sensor.h"
#include "network/DataHandler.h"
#include "Config.h"

Sensor sensors;

Sensor::Sensor() : currentBumperState(0), currentDistance(0)
{
}

bool Sensor::bumperBumbs(u_char bumper)
{
	return digitalRead(bumper) == LOW;
}


void Sensor::checkAllBumpers()
{
	u_char bumpers = 0;
	if(bumperBumbs(SENSOR_BUMPER_FRONT_LEFT))
		bumpers |= SENSOR_BUMPER_FRONT_LEFT_DATAFLAG;
	if(bumperBumbs(SENSOR_BUMPER_FRONT_MIDDLE))
		bumpers |= SENSOR_BUMPER_FRONT_MIDDLE_DATAFLAG;
	if(bumperBumbs(SENSOR_BUMPER_FRONT_RIGHT))
		bumpers |= SENSOR_BUMPER_FRONT_RIGHT_DATAFLAG;
	if(bumperBumbs(SENSOR_BUMPER_REAR_RIGHT))
		bumpers |= SENSOR_BUMPER_REAR_RIGHT_DATAFLAG;
	if(bumperBumbs(SENSOR_BUMPER_REAR_LEFT))
		bumpers |= SENSOR_BUMPER_REAR_LEFT_DATAFLAG;

	currentBumperState = bumpers;
	if(currentBumperState > 0)
		dataHandler.sendBumperNotify(currentBumperState);
}

void Sensor::checkDistance()
{
	currentDistance = analogRead(SENSOR_INFRARED_RIGHT);
	dataHandler.sendDistance(currentDistance);
}

int Sensor::readPoti(u_char motor_ID)
{
	return 0;
}

void Sensor::Init(){
	pinMode(SENSOR_BUMPER_REAR_LEFT,INPUT);
	pinMode(SENSOR_BUMPER_FRONT_LEFT, INPUT);
	pinMode(SENSOR_BUMPER_FRONT_MIDDLE, INPUT);
	pinMode(SENSOR_BUMPER_FRONT_RIGHT, INPUT);
	pinMode(SENSOR_BUMPER_REAR_RIGHT, INPUT);
	pinMode(SENSOR_INFRARED_RIGHT, INPUT);
}
