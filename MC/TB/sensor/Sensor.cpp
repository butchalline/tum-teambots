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
	 writePointer_left = 0;
	 writePointer_right = 0;
	 for( int i =0; i<3;i++){
	 poti_Buf_left[i] = 0;
	 poti_Buf_right[i] = 0;
	 tmp_poti_Buf_left[i] = 0;
	 tmp_poti_Buf_right[i] = 0;
	 }
	 poti_Buf_left[3] = 0;
	 poti_Buf_right[3] = 0;
	 poti_Buf_left[4] = 0;
	 poti_Buf_right[4] = 0;

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

u_char Sensor::getCurrentBumperState()
{
	return currentBumperState;
}

void Sensor::Init(){
	pinMode(SENSOR_BUMPER_REAR_LEFT,INPUT);
	pinMode(SENSOR_BUMPER_FRONT_LEFT, INPUT);
	pinMode(SENSOR_BUMPER_FRONT_MIDDLE, INPUT);
	pinMode(SENSOR_BUMPER_FRONT_RIGHT, INPUT);
	pinMode(SENSOR_BUMPER_REAR_RIGHT, INPUT);
	pinMode(SENSOR_INFRARED_RIGHT, INPUT);
}

void Sensor::write_poti_data_left(u_short potivalue){

	if (writePointer_left == 5)
		writePointer_left = 0;
	poti_Buf_left[writePointer_left++] = potivalue;
}

void Sensor::write_poti_data_right(u_short potivalue){

	if (writePointer_right == 5)
		writePointer_right = 0;
	poti_Buf_right[writePointer_right++] = potivalue;
}

u_short Sensor::get_poti_media_left(){
	tmp_poti_Buf_left[0] = 0;
	tmp_poti_Buf_left[1] = 0;
	tmp_poti_Buf_left[2] = 0;
	for(u_char i = 0;i<5;i++){
		if(poti_Buf_left[i]> poti_Buf_left[tmp_poti_Buf_left[0]])
			tmp_poti_Buf_left[0] = i;
	}
	for(u_char k = 0;k<5;k++){
		if(k != tmp_poti_Buf_left[0] && poti_Buf_left[k]> poti_Buf_left[tmp_poti_Buf_left[1]])
			tmp_poti_Buf_left[1] = k;
	}
	for(u_char l = 0;l<5;l++){
		if(l != tmp_poti_Buf_left[0] && l != tmp_poti_Buf_left[1] && poti_Buf_left[l]> poti_Buf_left[tmp_poti_Buf_left[2]])
			tmp_poti_Buf_left[2] = l;
	}
	return poti_Buf_left[tmp_poti_Buf_left[2]];
}

u_short Sensor::get_poti_media_right(){
	tmp_poti_Buf_right[0] = 0;
	tmp_poti_Buf_right[1] = 0;
	tmp_poti_Buf_right[2] = 0;
	for(u_char i = 0;i<5;i++){
		if(poti_Buf_right[i]> poti_Buf_right[tmp_poti_Buf_right[0]])
			tmp_poti_Buf_right[0] = i;
	}
	for(u_char k = 0;k<5;k++){
		if(k != tmp_poti_Buf_right[0] && poti_Buf_right[k]> poti_Buf_right[tmp_poti_Buf_right[1]])
			tmp_poti_Buf_right[1] = k;
	}
	for(u_char l = 0;l<5;l++){
		if(l != tmp_poti_Buf_right[0] && l != tmp_poti_Buf_right[1] && poti_Buf_right[l]> poti_Buf_right[tmp_poti_Buf_right[2]])
			tmp_poti_Buf_right[2] = l;
	}
	return poti_Buf_right[tmp_poti_Buf_right[2]];
}

void Sensor::checkPotiMedian(){
	write_poti_data_left(analogRead(SENSOR_POTI_LEFT));
	write_poti_data_right(analogRead(SENSOR_POTI_RIGHT));
	dataHandler.sendPotiMedian(get_poti_media_left(),get_poti_media_right());
}
