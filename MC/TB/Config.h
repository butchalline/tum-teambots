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

#ifndef __CONFIG_H__
#define __CONFIG_H__

#include "Arduino.h"
#include "common/Types.h"

#define SERVO_ID 0x01               // ID of which we will set Dynamixel too
#define SERVO_ControlPin 0x02       // Control pin of buffer chip, NOTE: this does not matter becasue we are not using a half to full contorl buffer.
#define SERVO_SET_Baudrate 1000000  // Baud rate speed which the Dynamixel will be set too (1Mbps)
#define LED13 0x0D                  // Pin of Visual indication for runing "heart beat" using onboard LED
#define MOTOR_POWER 0x06

#define MAX_USER_VELOCITY 800 //can't be more then 1023 (0x3ff)

#define MOTOR_ID_LEFT 2
#define MOTOR_ID_RIGHT 1
#define MOTOR_ID_TABLET 4

#define INKR_SENSOR_LEFT_1	0x1E	//Pin 30 Definition of the incremental Sensors Inputs
#define INKR_SENSOR_LEFT_2	0x20	//Pin 32
#define INKR_SENSOR_LEFT_3	0x22	//Pin 34
#define INKR_SENSOR_LEFT_4	0x24	//Pin 36
#define INKR_SENSOR_LEFT_5	0x26	//Pin 38
#define INKR_SENSOR_LEFT_6	0x28	//Pin 40
#define INKR_SENSOR_LEFT_7	0x2A	//Pin 42
#define INKR_SENSOR_LEFT_8	0x2C	//Pin 44

#define INKR_SENSOR_RIGHT_1	0x1F	//Pin 31
#define INKR_SENSOR_RIGHT_2	0x21	//Pin 33
#define INKR_SENSOR_RIGHT_3	0x23	//Pin 35
#define INKR_SENSOR_RIGHT_4	0x25	//Pin 37
#define INKR_SENSOR_RIGHT_5	0x27	//Pin 39
#define INKR_SENSOR_RIGHT_6	0x29	//Pin 41
#define INKR_SENSOR_RIGHT_7	0x2B	//Pin 43
#define INKR_SENSOR_RIGHT_8	0x2D	//Pin 45




#endif /* CONFIG_H_ */
