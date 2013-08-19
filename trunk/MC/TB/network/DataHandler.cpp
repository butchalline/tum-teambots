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

#include "network/DataHandler.h"

DataHandler dataHandler;

void DataHandler::setTimeStamp(TBFrame& frame)
{
	frame.head.TimeStamp = millis() / 10;
}


void DataHandler::sendPosition(short x, short y, short angle)
{
	TBFrame frame;
	setTimeStamp(frame);
	frame.head.Id = TB_DATA_ID;
	frame.head.SubId = TB_DATA_POSITION;
	frame.data.positionData.x = x;
	frame.data.positionData.y = y;
	frame.data.positionData.angle = angle;

	usb.putData((unsigned char*)&frame, sizeof(TBHeader) + sizeof(TBPosition));
}

void DataHandler::sendBumperNotify(u_char bumpers)
{
	TBFrame frame;
	setTimeStamp(frame);
	frame.head.Id = TB_DATA_ID;
	frame.head.SubId = TB_DATA_BUMPERS;
	frame.data.bumperData.bumpers = bumpers;

	usb.putData((unsigned char*)&frame, sizeof(TBHeader) + sizeof(TBBumpers));
}

void DataHandler::sendDistance(u_short currentDistanceRight)
{
	TBFrame frame;
	setTimeStamp(frame);
	frame.head.Id = TB_DATA_ID;
	frame.head.SubId = TB_DATA_DISTANCE;
	frame.data.distanceData.distanceRight = currentDistanceRight;
}

void DataHandler::sendPotiMedian(u_short median_left, u_short median_right){
	TBFrame frame;
	setTimeStamp(frame);
	frame.head.Id = TB_DATA_ID;
	frame.head.SubId = TB_DATA_MEDIAN;
	frame.data.potiMedian.medianLeft = median_left;
	frame.data.potiMedian.medianRight = median_right;

}
