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

//Send direction from MC to SP

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
//======================================================


#define TB_TURN_ID					  				0x03
//------------------------------------------------------
#define TB_TURN_RIGHT				  				0x00
#define TB_TURN_LEFT				  				0x01
//======================================================


#define TB_POSITION_ID				  				0x04
//------------------------------------------------------
#define TB_GLOBAL_POSITION			  				0x00
#define TB_LOCAL_POSITION			  				0x01
//======================================================


#define TB_ERROR_ID					  				0x42
//------------------------------------------------------
#define TB_ERROR_TRACE								0x00
#define TB_ERROR_DEBUG								0x01
#define TB_ERROR_LOG								0x02
#define TB_ERROR_INFO								0x03
#define TB_ERROR_ERROR								0x04
//======================================================



struct TBVelocity {
	char direction; //-1 = backward | 1 = forward
	u_char speed;
};

union TBData {
	TBVelocity velocity;
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

