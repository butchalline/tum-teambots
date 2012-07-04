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
 
#include "TBUsb.h"
 
TBUsb::TBUsb() : amountSendBytes(0), sendOffset(0), putPointer(0)
{
}

void TBUsb::Init(const char* manufacturer, const char* model)
{
  android(manufacturer, model);
}

int TBUsb::read()
{
  return android.read();
}

void TBUsb::send()
{
  if(amountSendBytes > 0)
  {
    if(putPointer < sendOffset) //buffer overflow
    {
      byte sendBytes = 255 - sendOffset;
      android.write(sendBuf[sendOffset], sendBytes);
      sendOffset = 0;
      amountSendBytes -= sendBytes;      
    }
    android.write(sendBuf[sendOffset], amountSendBytes);
    sendOffset = putPointer;
    amountSendBytes = 0;
  }
}

void TBUsb::reconnect()
{
}

boolean TBUsb::putData(byte data)
{
  sendBuf[putPointer++] = data;
  ++amountSendBytes;
}

boolean TBUsb::putData(byte* data, byte size)
{
  for(int i = 0; i < size; i++)
  {
    sendBuf[putPointer++] = data[i];
    ++amountSendBytes:
  }
}

boolean TBUsb::isConnected()
{
  return android.connected();  
}

int TBUsb::sizeData()
{
  return android.available();  
}

