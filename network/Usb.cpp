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

#include "network/Usb.h"

// accessory descriptor. It's how Arduino identifies itself to Android
#define APPLICATION_NAME "ArduinoBlinkLED" // the app on your phone
#define ACCESSORY_NAME "Mega_ADK" // your Arduino board
#define COMPANY_NAME "Arduino SA"

// make up anything you want for these
#define VERSIONN_NUMBER "1.0"
#define SERIAL_NUMBER "1"
#define URL "http://labs.arduino.cc/adk/ADK_count" // the URL of your app online
Usb usb;

Usb::Usb() :
		amountSendBytes(0), sendOffset(0), putPointer(0), android(COMPANY_NAME,
				APPLICATION_NAME, ACCESSORY_NAME, VERSIONN_NUMBER, URL,
				SERIAL_NUMBER) {
}

u_char Usb::read() {
	return android.read();
}

void Usb::send() {
	if (amountSendBytes > 0) {
		if (putPointer < sendOffset) //buffer overflow
				{
			byte sendBytes = 255 - sendOffset;
			android.write(&sendBuf[sendOffset], sendBytes);
			sendOffset = 0;
			amountSendBytes -= sendBytes;
		}
		android.write(&sendBuf[sendOffset], amountSendBytes);
		sendOffset = putPointer;
		amountSendBytes = 0;
	}
}

void Usb::reconnect() {
	android.powerOn();
}

bool Usb::putData(u_char data) {
	if (putPointer + 1 == sendOffset)
		return false;
	sendBuf[putPointer++] = data;
	++amountSendBytes;
	return true;
}

bool Usb::putData(u_char* data, u_char size) {
	if (putPointer + size > sendOffset)
		return false;
	for (int i = 0; i < size; i++) {
		sendBuf[putPointer++] = data[i];
		++amountSendBytes;
	}
	return true;
}

bool Usb::isConnected() {
	return android.isConnected();
}

int Usb::sizeData() {
	return android.available();
}

