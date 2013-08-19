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

#ifndef __TBDATA_H__
#define __TBDATA_H__

#include "Config.h"

#define TB_COMMAND_ID			  	  				0x00
//------------------------------------------------------
#define TB_COMMAND_REQUESTSTATE_VELOCITYDRIVE	  	0x00
#define TB_COMMAND_REQUESTSTATE_POSITIONDRIVE		0x01
#define TB_COMMAND_REQUESTSTATE_TURN				0x02
#define TB_COMMAND_REQUESTSTATE_STOP				0x03
#define TB_COMMAND_RESET			  				0x04
//======================================================

#define TB_VELOCITY_ID				  				0x01
//------------------------------------------------------
#define TB_VELOCITY_FORWARD			  				0x00 //Target Velocity
#define TB_VELOCITY_BACKWARD	      				0x01 //Target Velocity
#define TB_VELOCITY_TURN_LEFT						0x02 //Right Forwards | Left Backwards
#define TB_VELOCITY_TURN_RIGHT						0x03 //Left Forwards  | Right Backwards
//======================================================


#define TB_POSITION_ID					      		0x02
//------------------------------------------------------
#define TB_POSITION_FORWARD							0x00 //in millimeters
#define TB_POSITION_BACKWARD						0x01 //in millimeters
#define TB_POSITION_TURN_RIGHT		  				0x02
#define TB_POSITION_TURN_LEFT		  				0x03
//======================================================

#define TB_DATA_ID									0x03
//------------------------------------------------------
#define TB_DATA_INFRARED							0x00  //char distance left | char distance middle | char distance right
#define TB_DATA_POSITION							0x01  //short X | short Y | short Angle * 100
#define TB_DATA_BUMPERS								0x02
#define TB_DATA_DISTANCE							0x03
#define TB_DATA_MEDIAN								0x04
//======================================================

#define TB_STATUS_ID								0x04
//------------------------------------------------------
#define TB_STATUS_IDLE_MODE							0x00
#define TB_STATUS_VELOCITY_MODE						0x01
#define TB_STATUS_POSITION_MODE						0x02
#define TB_STATUS_POSITION_REACHED					0x03
#define TB_STATUS_ANGLE_REACHED						0x04
//======================================================


#define TB_ERROR_ID					  				0x42
//------------------------------------------------------
#define TB_ERROR_TRACE								0x00
#define TB_ERROR_DEBUG								0x01
#define TB_ERROR_LOG								0x02
#define TB_ERROR_INFO								0x03
#define TB_ERROR_ERROR								0x04
#define TB_ERROR_NOT_ALLOWED						0x05
//======================================================



struct TBVelocity {
	u_char speedLeft;
	u_char speedRight;
};

struct TBInfrared {
	u_char distance_left; //distance in cm
	u_char distance_middle;
	u_char distance_right;
};

struct TBPosition {
	short x;
	short y;
	short angle;
};


struct TBBumpers
{
	u_char bumpers;
};

struct TBDistance
{
	u_short distanceRight;
};

struct TBTabletPosition
{
	u_char tabletPosition; //Vertical = 0; max back = 90
};

struct TBPotiMedian
{
	u_short medianLeft;
	u_short medianRight;
};

union TBData {
	TBVelocity velocity;
	TBInfrared infraredData;
	TBPosition positionData;
	TBBumpers bumperData;
	TBDistance distanceData;
	TBTabletPosition tabletPosition;
	TBPotiMedian potiMedian;
};

struct TBHeader {
	u_char  Id; //defined above
	u_char  SubId;
	u_short TimeStamp; //in 10ms
};

struct TBFrame {
	TBHeader head;
	TBData data;
};



#endif /*__TBDATA_H__ */

