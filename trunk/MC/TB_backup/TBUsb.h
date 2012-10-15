
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
 
 
#ifndef __TBUSB_H__
#define __TBUSB_H__
 
#include "TBTypes.h"

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

 
 class TBUsb
 {
   public:
     TBUsb();
     u_char read();
     void send();
     bool putData(u_char data);
     bool putData(u_char* data, u_char size);
     bool isConnected();
     void reconnect();
     int sizeData();
   private:
     void write(const u_char* data, u_char size);
   
     u_char amountSendBytes;
     u_char sendOffset;
     u_char putPointer;
     u_char sendBuf[255];
     AndroidAccessory android;
 };
 
 extern TBUsb usb;
 
 #endif /* __TBUSB_H__ */

