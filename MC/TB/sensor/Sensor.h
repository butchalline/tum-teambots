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

#ifndef __SENSOR_H_
#define __SENSOR_H_

#include "Config.h"
#include "common/Types.h"

class Sensor {

public:
	Sensor();
	void Init();
	void checkAllBumpers();
	void checkDistance();
	u_char getCurrentBumperState();
	short observer_left();
	short observer_right();
	u_short get_poti_media_left();
	u_short get_poti_media_right();
	void write_poti_data_left(u_short potivalue);
	void write_poti_data_right(u_short potivalue);
	void checkPotiMedian();

private:
	u_char currentBumperState;
	u_short currentDistance;
	bool bumperBumbs(u_char bumper);
	int readPoti(u_char motor_ID);

	//poti_buffer
	u_short writePointer_left;
	u_short writePointer_right;
	u_short poti_Buf_left[5];
	u_short poti_Buf_right[5];


	u_char tmp_poti_Buf_left[3];
	u_char tmp_poti_Buf_right[3];


};

extern Sensor sensors;


#endif /* __SENSOR_H_ */
