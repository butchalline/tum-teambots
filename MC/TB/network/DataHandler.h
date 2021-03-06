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

#ifndef __DATAHANDLER_H__
#define __DATAHANDLER_H__

#include "network/Usb.h"
#include "network/Data.h"

class DataHandler {

public:
	DataHandler() {}
	void sendPosition(short x, short y, short angle);
	void sendBumperNotify(u_char id);
	void sendDistance(u_short currentDistanceRight);
	void sendPotiMedian(u_short median_left, u_short median_right);

private:
	void setTimeStamp(TBFrame& frame);

};

extern DataHandler dataHandler;


#endif /* DATAHANDLER_H_ */
