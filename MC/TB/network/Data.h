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
#define TBUSB_MC_Status               0x0000 /*Data = 1 Byte | Status Msg |*/
#define TBUSB_MC_Error                0x0001 /*Data = 1 Byte | Error Msg | */
#define TBUSB_MC_MotorsEncoderDiff    0x0002 /*Data = 4 Byte | MotorLeftDiff | MotorRightDiff |*/

//Send direction from SP to MC
#define TBUSB_SP_RequieredState       0x8000 /*Data = 1 Byte | requiredState |*/
#define TBUSB_SP_TargetPosition       0x8001 /*Data = 4 Byte | */
#define TBUSB_SP_TargetVelocity       0x8002 /* */
#define TBUSB_SP_ResetMC              0x8100 /*Data = 0 Byte */


struct TBVelocity {
	char speed;
};

union TBData {
	TBVelocity velocity;
};

struct TBHeader {
	u_short Id; //defined above
	u_short TimeStamp; //in 10ms
};

struct TBFrame {
	TBHeader head;
	TBData data;
};



#endif /*__TBDATA_H__/

