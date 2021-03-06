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

/**
 * Motor
 */
#define MOTOR_ControlPin 0x02       // Control pin of buffer chip, NOTE: this does not matter becasue we are not using a half to full contorl buffer.
#define MOTOR_Baudrate 1000000  // Baud rate speed which the Dynamixel will be set too (1Mbps)
#define MOTOR_POWER 0x06

#define MOTOR_ID_LEFT 2
#define MOTOR_ID_RIGHT 1
#define MOTOR_ID_TABLET 4
#define MOTOR_TORGUE_USER_MAX 0x330 //Maximum torgue for user (can't be more then 0x3ff)
#define MOTOR_TORGUE_CONTROL_MAX 0x3FF //Maximum  for control (can't be more then 0x3ff)

/**
 * Tablet Positions
 */
#define TABLET_Horizontal 200
#define TABLET_Vertical 511
#define TABLET_Max_Back 810
#define TABLET_Max_Front 100

/**
 * Sensor
 */
#define SENSOR_BUMPER_REAR_RIGHT 	3 //Pos 0 in dataByte   0x01 | 0000 000X
#define SENSOR_BUMPER_FRONT_RIGHT 	4 //Pos 1 in dataByte  	0x02 | 0000 00X0
#define SENSOR_BUMPER_FRONT_MIDDLE	5 //Pos 2 in dataByte 	0x04 | 0000 0X00
#define SENSOR_BUMPER_FRONT_LEFT 	6 //Pos 3 in dataByte   0x08 | 0000 X000
#define SENSOR_BUMPER_REAR_LEFT 	7 //Pos 4 in dataByte   0x10 | 000X 0000

#define SENSOR_BUMPER_REAR_RIGHT_DATAFLAG 	0x01
#define SENSOR_BUMPER_FRONT_RIGHT_DATAFLAG 	0x02
#define SENSOR_BUMPER_FRONT_MIDDLE_DATAFLAG 0x04
#define SENSOR_BUMPER_FRONT_LEFT_DATAFLAG 	0x08
#define SENSOR_BUMPER_REAR_LEFT_DATAFLAG 	0x10

#define SENSOR_INFRARED_RIGHT 				A8

#define SENSOR_POTI_RIGHT					A0
#define SENSOR_POTI_LEFT					A1
#define SENSOR_POTI_TABLET					A2

#endif /* CONFIG_H_ */
