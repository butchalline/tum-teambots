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

Sensor sensors;

Sensor::Sensor()
{

}

void Sensor::Init(){
	pinMode(SENSOR_BUBMER_REAR_LEFT,INPUT);
	pinMode(SENSOR_BUMBER_FRONT_LEFT, INPUT);
	pinMode(SENSOR_BUMBER_FRONT_MIDDLE, INPUT);
	pinMode(SENSOR_BUMBER_FRONT_RIGHT, INPUT);
	pinMode(SENSOR_BUMBER_REAR_RIGHT, INPUT);
	pinMode(SENSOR_INFRARET_SHARP, INPUT);
}
