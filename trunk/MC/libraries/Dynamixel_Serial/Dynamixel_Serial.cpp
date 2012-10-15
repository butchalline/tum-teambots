/*
17 Jan 2012
	J.Teda
-Updated to work with Arduino 1.0

04 Dec 2011
	J.Teda
- Added turn write to registor
- have to amout of cide used for left and right selection


30 Nov 2011
	J.Teda 
-Added Return Delay, how long before status frames are sent after instuctions

27 Nov 2011
	J.Teda 
-Added load read, how much load/force is on the servo
-Changed how Dynamixel frame(s) reads are done and added error detection
-Fixed position read
 
11 Nov 2011
	J.Teda 
-Fixed Error in High and Low registy writing
-Fixed Error for Torque Max control 
-Added Alarm control (alarmShutdown)
-Added Torque Enable 
 
10 Nov 2011
	J.Teda  
-Checksum calculation changes to be on line of code.

 Dynamixel.cpp - Ax-12+ Half Duplex USART Comunication
 Copyright (c) 2011 Savage Electronics.
 Created by Savage on 27/01/11.
 
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,  
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 
 */

#include <stdlib.h>
#include <stdio.h>
#include <inttypes.h>
#include "Arduino.h"
#include "wiring_private.h"
#include "HardwareSerial.h"
#include "Dynamixel_Serial.h"

#if ARDUINO >= 100		
  #include "Arduino.h"	//used for newer versions of Ardiuno ( 1.0 or greater )
#else
  #include "wiring.h" 	//used for older versions of Ardiuno
#endif


// Private Methods //////////////////////////////////////////////////////////////

int DynamixelClass::read_error(void)
{
int Length_Read;

	Time_Counter = READ_TIME_OUT + millis(); 					// Setup time out error
	
    while(Serial.available() < 5 ) {							// Wait for header data, ID Length and error data from Dynamixel									
			if ( millis() >= Time_Counter) {
				return(-254);									// time out error , exit with fauilt code
			}					
		if( Serial.peek() != 0xFF )	{					
			Serial.read(); 										// Clear Present Byte so next Byte can be read as we are looking for header (start of Frame) data "0xFF"							
		} 

	}		
		Incoming_Byte = Serial.read();
		if (Incoming_Byte == 0xFF & Serial.peek() == 0xFF){		// check that there are 2 "0xFF" header data
			Serial.read(); 										// clear 2nd 0xFF
			Serial.read();                    					// ID sent from Dynamixel
			Length_Read = Serial.read();						// Frame Length
			Serial.read();
			if( Error_Byte != 0 ) {   							// See if error code was sent from Dynamixel	
				return (Error_Byte*(-1));					
				}
				
					Time_Counter = READ_TIME_OUT + millis(); 	// Setup time out error
					while(Serial.available() < Length_Read - 1)	// Wait loop for rest of data from Dynamixel			
					{					
						if ( millis() >= Time_Counter){
						return(-253);							// time out error , exit with fauilt code
						}
					}
									
		}else{
			return(-252);										//2nd Header data not recived, exit with fauilt code
			}
	return (0x00);											 	// No Ax Response
}

// Public Methods //////////////////////////////////////////////////////////////

void DynamixelClass::begin(long baud)
{	
	Serial.begin(baud);
	
}

void DynamixelClass::begin(long baud, unsigned char D_Pin)
{	
	pinMode(D_Pin,OUTPUT);
	Direction_Pin = D_Pin;
	Serial.begin(baud);
	
}	

void DynamixelClass::end()
{
	Serial.end();
}

int DynamixelClass::reset(unsigned char ID)
{
	Checksum = ~(ID + AX_RESET_LENGTH + AX_RESET);    // Bitwise not, then "AND" with HEX "FF" so only lower 8 bits are use      
	
	digitalWrite(Direction_Pin,HIGH);
	Serial.write(AX_START);                     
	Serial.write(AX_START);
	Serial.write(ID);
	Serial.write(AX_RESET_LENGTH);
	Serial.write(AX_RESET);    
	Serial.write(Checksum);
	delayMicroseconds(TX_DELAY_TIME);
	digitalWrite(Direction_Pin,LOW);
    
    return (read_error());  
}

int DynamixelClass::ping(unsigned char ID)
{
    Checksum = (~(ID + AX_READ_DATA + AX_PING));  
	
	digitalWrite(Direction_Pin,HIGH);
	Serial.write(AX_START);                     
	Serial.write(AX_START);
	Serial.write(ID);
	Serial.write(AX_READ_DATA);
	Serial.write(AX_PING);    
	Serial.write(Checksum);
	delayMicroseconds(TX_DELAY_TIME);
	digitalWrite(Direction_Pin,LOW);
    
    return (read_error());              
}

int DynamixelClass::returnDelay(unsigned char ID,unsigned char ReturnDelay)
{
    Checksum = ~(ID + AX_RETURN_LENGTH + AX_WRITE_DATA + AX_RETURN_DELAY_TIME + (ReturnDelay/2)); 
 	
    digitalWrite(Direction_Pin,HIGH);      // Set Tx Mode
    Serial.write(AX_START);                 // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
	Serial.write(AX_RETURN_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_RETURN_DELAY_TIME);
    Serial.write(ReturnDelay/2);
    Serial.write(Checksum);
    delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);      // Set Rx Mode
    
    return (read_error());                // Return the read error
}

int DynamixelClass::setID(unsigned char ID, unsigned char New_ID)
{    
    Checksum = ~(ID + AX_ID_LENGTH + AX_WRITE_DATA + AX_ID + New_ID);  
	
    digitalWrite(Direction_Pin,HIGH);     // Set Tx Mode
    Serial.write(AX_START);                // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
	Serial.write(AX_ID_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_ID);
    Serial.write(New_ID);
    Serial.write(Checksum);
	delayMicroseconds(TX_DELAY_TIME);
	digitalWrite(Direction_Pin,LOW);      // Set Rx Mode
    
    return (read_error());                // Return the read error
}

int DynamixelClass::setBD(unsigned char ID, long Baud)
{    
	unsigned char Baud_Rate = (2000000/Baud) - 1;
//	unsigned char Baud_Rate = (Baud);
    Checksum = ~(ID + AX_BD_LENGTH + AX_WRITE_DATA + AX_BAUD_RATE + Baud_Rate); 
 	
    digitalWrite(Direction_Pin,HIGH);      // Set Tx Mode
    Serial.write(AX_START);                 // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
	Serial.write(AX_BD_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_BAUD_RATE);
    Serial.write(Baud_Rate);
    Serial.write(Checksum);
    delayMicroseconds(TX_BD_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);      // Set Rx Mode
    
    return (read_error());                // Return the read error
}

int DynamixelClass::move(unsigned char ID, int Position)
{
    char Position_H,Position_L;
	Position_L = Position & 0xFF;
    Position_H = (Position >> 8) & 0xFF;           // 16 bits - 2 x 8 bits variables

    
    Checksum = ~(ID + AX_GOAL_LENGTH + AX_WRITE_DATA + AX_GOAL_POSITION_L + Position_L + Position_H);

   	digitalWrite(Direction_Pin,HIGH);      // Set Tx Mode
    Serial.write(AX_START);                 // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_GOAL_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_GOAL_POSITION_L);
    Serial.write(Position_L);
    Serial.write(Position_H);
    Serial.write(Checksum);
	delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);       // Set Rx Mode
	
    return (read_error());                 // Return the read error
}

int DynamixelClass::moveSpeed(unsigned char ID, int Position, int Speed)
{
    char Position_H,Position_L,Speed_H,Speed_L;
	Position_L = Position & 0xFF;
    Position_H = Position >> 8;
	Speed_L = Speed & 0xFF;
    Speed_H = Speed >> 8;
    
	Serial.flush();	
    
   Checksum = ~(ID + AX_GOAL_SP_LENGTH + AX_WRITE_DATA + AX_GOAL_POSITION_L + Position_L + Position_H + Speed_L + Speed_H);

	
    digitalWrite(Direction_Pin,HIGH);     // Set Tx Mode
    Serial.write(AX_START);                // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_GOAL_SP_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_GOAL_POSITION_L);
    Serial.write(Position_L);
    Serial.write(Position_H);
    Serial.write(Speed_L);
    Serial.write(Speed_H);
    Serial.write(Checksum);
    delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
    
    return (read_error());               // Return the read error
}

int DynamixelClass::endlessEnable(unsigned char ID, bool Status)
{
 if ( Status == 1) {	
	  char AX_CW_AL_L = 0;
	  Checksum = ~(ID + AX_GOAL_LENGTH + AX_WRITE_DATA + AX_CCW_ANGLE_LIMIT_L );
	
	  // Changing the CCW Angle Limits for Full Rotation.
	
	  digitalWrite(Direction_Pin,HIGH);     // Set Tx Mode
      Serial.write(AX_START);                // Send Instructions over Serial
      Serial.write(AX_START);
      Serial.write(ID);
      Serial.write(AX_GOAL_LENGTH);
      Serial.write(AX_WRITE_DATA);
      Serial.write(AX_CCW_ANGLE_LIMIT_L );
      Serial.write(AX_CW_AL_L);
      Serial.write(AX_CW_AL_L);
      Serial.write(Checksum);
      delayMicroseconds(TX_DELAY_TIME);
      digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
	return(read_error());
 }
 else
 {
	 turn(ID,0,0);
	 long CCW_Limit = 1023;
	 char CCW_Limit_H,CCW_Limit_L;
	 CCW_Limit_L = CCW_Limit & 0xFF;
	 CCW_Limit_H = CCW_Limit >> 8;           // 16 bits - 2 x 8 bits variables

	 
	 Checksum = ~(ID + AX_GOAL_LENGTH + AX_WRITE_DATA + AX_CCW_ANGLE_LIMIT_L +CCW_Limit_L + CCW_Limit_H);

	 
	 digitalWrite(Direction_Pin,HIGH);      // Set Tx Mode
	 Serial.write(AX_START);                 // Send Instructions over Serial
	 Serial.write(AX_START);
	 Serial.write(ID);
	 Serial.write(AX_GOAL_LENGTH);
	 Serial.write(AX_WRITE_DATA);
	 Serial.write(AX_CCW_ANGLE_LIMIT_L);
	 Serial.write(CCW_Limit_L);
	 Serial.write(CCW_Limit_H);
	 Serial.write(Checksum);
	 delayMicroseconds(TX_DELAY_TIME);
	 digitalWrite(Direction_Pin,LOW);       // Set Rx Mode
	 
	 return (read_error());                 // Return the read error
  }
 } 

int DynamixelClass::turn(unsigned char ID, bool SIDE, int Speed)
{	
	char Speed_H,Speed_L;
	Speed_L = Speed & 0xFF;	
		if (SIDE == 0){                          // Move Left                     
			Speed_H = Speed >> 8;
			}
		else if (SIDE == 1){					// Move Right
			Speed_H = (Speed >> 8)+4;	
			}	
			
			Checksum = ~(ID + AX_SPEED_LENGTH + AX_WRITE_DATA + AX_GOAL_SPEED_L + Speed_L + Speed_H);
			
			digitalWrite(Direction_Pin,HIGH);     // Set Tx Mode
			Serial.write(AX_START);                // Send Instructions over Serial
			Serial.write(AX_START);
			Serial.write(ID);
			Serial.write(AX_SPEED_LENGTH);
			Serial.write(AX_WRITE_DATA);
			Serial.write(AX_GOAL_SPEED_L);
			Serial.write(Speed_L);
			Serial.write(Speed_H);
			Serial.write(Checksum);
			delayMicroseconds(TX_DELAY_TIME);
			digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
			
			return(read_error());               // Return the read error		

}

int DynamixelClass::turnRW(unsigned char ID, bool SIDE, int Speed)
{	
	char Speed_H,Speed_L;
	Speed_L = Speed & 0xFF; 
		if (SIDE == 0){                          // Move Left
			Speed_H = Speed >> 8;
		}
		else if (SIDE == 1)						// Move Right
		{   
			Speed_H = (Speed >> 8)+4;
		}	
			Checksum = ~(ID + AX_SPEED_LENGTH + AX_REG_WRITE + AX_GOAL_SPEED_L + Speed_L + Speed_H);
			
			digitalWrite(Direction_Pin,HIGH);     // Set Tx Mode
			Serial.write(AX_START);                // Send Instructions over Serial
			Serial.write(AX_START);
			Serial.write(ID);
			Serial.write(AX_SPEED_LENGTH);
			Serial.write(AX_REG_WRITE);
			Serial.write(AX_GOAL_SPEED_L);
			Serial.write(Speed_L);
			Serial.write(Speed_H);
			Serial.write(Checksum);
			delayMicroseconds(TX_DELAY_TIME);
			digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
			
			return(read_error());               // Return the read error		

}


int DynamixelClass::moveRW(unsigned char ID, int Position)
{

    char Position_H,Position_L;
	Position_L = Position & 0xFF;
    Position_H = Position >> 8;           // 16 bits - 2 x 8 bits variables

    
    Checksum = ~(ID + AX_GOAL_LENGTH + AX_REG_WRITE + AX_GOAL_POSITION_L + Position_L + Position_H);
    
	digitalWrite(Direction_Pin,HIGH);      // Set Tx Mode
    Serial.write(AX_START);                 // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_GOAL_LENGTH);
    Serial.write(AX_REG_WRITE);
    Serial.write(AX_GOAL_POSITION_L);
    Serial.write(Position_L);
    Serial.write(Position_H);
    Serial.write(Checksum);
	delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);       // Set Rx Mode
	
    return (read_error());                 // Return the read error
}

int DynamixelClass::moveSpeedRW(unsigned char ID, int Position, int Speed)
{
    char Position_H,Position_L,Speed_H,Speed_L;
	Position_L = Position & 0xFF; 
    Position_H = Position >> 8;  
    Speed_L = Speed & 0xFF;	
    Speed_H = Speed >> 8;
	
    Serial.flush();
	
    Checksum = ~(ID + AX_GOAL_SP_LENGTH + AX_REG_WRITE + AX_GOAL_POSITION_L + Position_L + Position_H + Speed_L + Speed_H);
	
    digitalWrite(Direction_Pin,HIGH);     // Set Tx Mode
    Serial.write(AX_START);                // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_GOAL_SP_LENGTH);
    Serial.write(AX_REG_WRITE);
    Serial.write(AX_GOAL_POSITION_L);
    Serial.write(Position_L);
    Serial.write(Position_H);
    Serial.write(Speed_L);
    Serial.write(Speed_H);
    Serial.write(Checksum);
    delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
    
    return (read_error());               // Return the read error
}

int DynamixelClass::action(unsigned char ID)
{	
	Checksum = ~(ID + AX_ACTION_LENGTH + AX_ACTION);
	
    digitalWrite(Direction_Pin,HIGH);     // Set Tx Mode
    Serial.write(AX_START);                // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_ACTION_LENGTH);
    Serial.write(AX_ACTION);
    Serial.write(Checksum);
	delayMicroseconds(TX_DELAY_TIME);
	digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
	
	return (read_error()); 				// Return the read error
}

int DynamixelClass::torqueMax( unsigned char ID, int Status)
{
    char TorqueMax_L,TorqueMax_H;
	TorqueMax_L = Status & 0xFF;      	
    TorqueMax_H = Status >> 8;    
	
	Checksum = ~(ID + AX_TORQUE_MAX_LENGTH + AX_WRITE_DATA + AX_MAX_TORQUE_L + TorqueMax_L + TorqueMax_H);
	
    digitalWrite(Direction_Pin,HIGH);   // Set Tx Mode
    Serial.write(AX_START);              // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_TORQUE_MAX_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_MAX_TORQUE_L);
    Serial.write(TorqueMax_L);
    Serial.write(TorqueMax_H);
    Serial.write(Checksum);
    delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);    // Set Rx Mode
    
    return (read_error());              // Return the read error
}

int DynamixelClass::torqueEnable(unsigned char ID, bool Set)
{
    Checksum = ~(ID + AX_LED_LENGTH + AX_WRITE_DATA + AX_TORQUE_ENABLE + Set);
    
    digitalWrite(Direction_Pin,HIGH);   // Set Tx Mode
    Serial.write(AX_START);              // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_LED_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_TORQUE_ENABLE);
    Serial.write(Set);
    Serial.write(Checksum);
    delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);    // Set Rx Mode
    
    return (read_error());              // Return the read error
}

int DynamixelClass::ledState(unsigned char ID, bool Status)
{    
    Checksum = ~(ID + AX_LED_LENGTH + AX_WRITE_DATA + AX_LED + Status);

    
    digitalWrite(Direction_Pin,HIGH);   // Set Tx Mode
    Serial.write(AX_START);              // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_LED_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_LED);
    Serial.write(Status);
    Serial.write(Checksum);
    delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);    // Set Rx Mode
    
    return (read_error());              // Return the read error
}

int DynamixelClass::alarmShutdown(unsigned char  ID,int Set)
{
  Checksum = ~(ID + AX_ALARM_LENGTH + AX_WRITE_DATA + AX_ALARM_SHUTDOWN + Set);

    
    digitalWrite(Direction_Pin,HIGH);   // Set Tx Mode
    Serial.write(AX_START);              // Send Instructions over Serial
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_ALARM_LENGTH);
    Serial.write(AX_WRITE_DATA);
    Serial.write(AX_ALARM_SHUTDOWN);
    Serial.write(Set);
    Serial.write(Checksum);
    delayMicroseconds(TX_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);    // Set Rx Mode

}

int DynamixelClass::readTemperature(unsigned char ID)
{	
		int ID_Read;
		int Length_Read;
		int Para1_Read;
		int ChechS_Read;
		
	Checksum = ~(ID + AX_TEM_LENGTH  + AX_READ_DATA + AX_PRESENT_TEMPERATURE + AX_BYTE_READ);
    
    digitalWrite(Direction_Pin,HIGH);
	Serial.flush();	
    Serial.write(AX_START);
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_TEM_LENGTH);
    Serial.write(AX_READ_DATA);
    Serial.write(AX_PRESENT_TEMPERATURE);
    Serial.write(AX_BYTE_READ);
    Serial.write(Checksum);	
	delayMicroseconds(TX_READ_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
	
	Temperature_Byte = 0;
	Time_Counter = READ_TIME_OUT + millis(); 					// Setup time out error
	
    while(Serial.available() < 5 ) {							// Wait for header data, ID Length and error data from Dynamixel									
			if ( millis() >= Time_Counter) {
				return(-254);									// time out error , exit with fauilt code
			}					
		if( Serial.peek() != 0xFF )	{					
			Serial.read(); 										// Clear Present Byte so next Byte can be read as we are looking for header (start of Frame) data "0xFF"							
		} 

	}		
	
		Incoming_Byte = Serial.read();
		if (Incoming_Byte == 0xFF & Serial.peek() == 0xFF){		// check that there are 2 "0xFF" header data
			Serial.read(); 										// clear 2nd 0xFF
			ID_Read = Serial.read();                    		// ID sent from Dynamixel
			Length_Read = Serial.read();						// Frame Length
			Error_Byte = Serial.read();
			if( Error_Byte != 0 ) {   		// See if error code was sent from Dynamixel	
				return (Error_Byte*(-1));					
				}
				
					Time_Counter = READ_TIME_OUT + millis(); 	// Setup time out error
					while(Serial.available() < Length_Read - 1)	// Wait loop for rest of data from Dynamixel			
					{					
						if ( millis() >= Time_Counter){
						return(-253);							// time out error , exit with fauilt code
						}
					}
									
		}else{
			return(-252);											//2nd Header data not recived, exit with fauilt code
			}
		
		Para1_Read = Serial.read();							// Voltage Value  
		ChechS_Read = Serial.read();
			if ( ((~(ID_Read + Length_Read + Para1_Read)) & 0xFF) != ChechS_Read & 0xFF){	// Check sum of the recived data
				return(-251);	
			}		
			
			Temperature_Byte = Para1_Read;
	return (Temperature_Byte);               // Returns the read temperature
}

int DynamixelClass::readPosition(unsigned char ID)
{	
		int ID_Read;
		int Length_Read;
		int Para1_Read;
		int Para2_Read;
		int ChechS_Read;
		
	Checksum = ~(ID + AX_POS_LENGTH  + AX_READ_DATA + AX_PRESENT_POSITION_L + AX_PRESENT_POSITION_H);
    
    digitalWrite(Direction_Pin,HIGH);
	Serial.flush();	
    Serial.write(AX_START);
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_POS_LENGTH);
    Serial.write(AX_READ_DATA);
    Serial.write(AX_PRESENT_POSITION_L);
    Serial.write(AX_PRESENT_POSITION_H);
    Serial.write(Checksum);	
    delayMicroseconds(TX_READ_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
	
	Position_Long_Byte = 0;
	Time_Counter = READ_TIME_OUT + millis(); 					// Setup time out error
	
    while(Serial.available() < 5 ) {							// Wait for header data, ID Length and error data from Dynamixel									
			if ( millis() >= Time_Counter) {
				return(-254);									// time out error , exit with fauilt code
			}					
		if( Serial.peek() != 0xFF )	{					
			Serial.read(); 										// Clear Present Byte so next Byte can be read as we are looking for header (start of Frame) data "0xFF"							
		} 

	}		
		Incoming_Byte = Serial.read();
		if (Incoming_Byte == 0xFF & Serial.peek() == 0xFF){		// check that there are 2 "0xFF" header data
			Serial.read(); 										// clear 2nd 0xFF
			ID_Read = Serial.read();                    		// ID sent from Dynamixel
			Length_Read = Serial.read();						// Frame Length
			if( (Error_Byte = Serial.read()) != 0 ) {   		// See if error code was sent from Dynamixel	
				return (Error_Byte*(-1));					
				}
				
					Time_Counter = READ_TIME_OUT + millis(); 	// Setup time out error
					while(Serial.available() < Length_Read - 1)	// Wait loop for rest of data from Dynamixel			
					{					
						if ( millis() >= Time_Counter){
						return(-253);							// time out error , exit with fauilt code
						}
					}
									
		}else{
			return(-252);											//2nd Header data not recived, exit with fauilt code
			}
			
	Para1_Read = Serial.read();								// Positon Low data Value  
	Para2_Read = Serial.read();								// Positon High data Value 
	ChechS_Read = Serial.read();							// Check Sum data Value
//			if ( ((~(ID_Read + Length_Read + Para1_Read + Para2_Read )) & 0xFF) != ChechS_Read & 0xFF){	// Check sum of the recived data
//				return(-251);	
//			}	

		
	Position_Long_Byte = Para2_Read; 
	Position_Long_Byte = (Position_Long_Byte << 8) + Para1_Read;
	ChechS_Read = Serial.read();
			
	//		if ( ((~(ID_Read + Length_Read + Para1_Read + Para2_Read )) && 0xFF) != ChechS_Read && 0xFF)
	//		{	// Check sum of the recived data
	//			return(-252);	
	//		}
	
	return (Position_Long_Byte);     // Returns the read position
}

int DynamixelClass::readLoad(unsigned char ID)
{	
		int ID_Read;
		int Length_Read;
		int Para1_Read;
		int Para2_Read;
		int ChechS_Read;
	
    Checksum = ~(ID + AX_LOAD_LENGTH  + AX_READ_DATA + AX_PRESENT_LOAD_L + AX_PRESENT_LOAD_H);
    
    digitalWrite(Direction_Pin,HIGH); 
	Serial.flush();
    Serial.write(AX_START);
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_LOAD_LENGTH);
    Serial.write(AX_READ_DATA);
    Serial.write(AX_PRESENT_LOAD_L);
    Serial.write(AX_PRESENT_LOAD_H);
    Serial.write(Checksum);
    delayMicroseconds(TX_READ_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
	
	Load_Long_Byte = 0;	
	Time_Counter = READ_TIME_OUT + millis(); 					// Setup time out error
	
    while(Serial.available() < 5 ) {							// Wait for header data, ID Length and error data from Dynamixel									
			if ( millis() >= Time_Counter) {
				return(-254);									// time out error , exit with fauilt code
			}					
		if( Serial.peek() != 0xFF )	{					
			Serial.read(); 										// Clear Present Byte so next Byte can be read as we are looking for header (start of Frame) data "0xFF"							
		} 

	}		
		Incoming_Byte = Serial.read();
		if (Incoming_Byte == 0xFF & Serial.peek() == 0xFF){		// check that there are 2 "0xFF" header data
			Serial.read(); 										// clear 2nd 0xFF
			ID_Read = Serial.read();                    		// ID sent from Dynamixel
			Length_Read = Serial.read();						// Frame Length
			if( (Error_Byte = Serial.read()) != 0 ) {   		// See if error code was sent from Dynamixel	
				return (Error_Byte*(-1));					
				}
				
					Time_Counter = READ_TIME_OUT + millis(); 	// Setup time out error
					while(Serial.available() < Length_Read - 1)	// Wait loop for rest of data from Dynamixel			
					{					
						if ( millis() >= Time_Counter){
						return(-253);							// time out error , exit with fauilt code
						}
					}
									
		}else{
			return(-252);											//2nd Header data not recived, exit with fauilt code
			}
			
	Para1_Read = Serial.read();								// Positon Low data Value  
	Para2_Read = Serial.read();								// Positon High data Value 
	ChechS_Read = Serial.read();							// Check Sum data Value
//			if ( ((~(ID_Read + Length_Read + Para1_Read + Para2_Read )) & 0xFF) != ChechS_Read & 0xFF){	// Check sum of the recived data
//				return(-251);	
//			}			
			
			Load_Long_Byte =  Para1_Read ; 			
			Load_Long_Byte = (Load_Long_Byte << 8) + Para2_Read;

	
	return (Load_Long_Byte);     // Returns the read position
}

int DynamixelClass::readSpeed(unsigned char ID)
{	
		int ID_Read;
		int Length_Read;
		int Para1_Read;
		int Para2_Read;
		int ChechS_Read;
	
    Checksum = ~(ID + AX_READ_SPEED_LENGTH  + AX_READ_DATA + AX_PRESENT_SPEED_L + AX_PRESENT_SPEED_H);
    
    digitalWrite(Direction_Pin,HIGH); 
	Serial.flush();
    Serial.write(AX_START);
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_READ_SPEED_LENGTH);
    Serial.write(AX_READ_DATA);
    Serial.write(AX_PRESENT_SPEED_L);
    Serial.write(AX_PRESENT_SPEED_H);
    Serial.write(Checksum);	
    delayMicroseconds(TX_READ_DELAY_TIME);
    digitalWrite(Direction_Pin,LOW);     // Set Rx Mode
	
	Speed_Long_Byte = 0;
	Time_Counter = READ_TIME_OUT + millis(); 					// Setup time out error
	
    while(Serial.available() < 5 ) {							// Wait for header data, ID Length and error data from Dynamixel									
			if ( millis() >= Time_Counter) {
				return(-254);									// time out error , exit with fauilt code
			}					
		if( Serial.peek() != 0xFF )	{					
			Serial.read(); 										// Clear Present Byte so next Byte can be read as we are looking for header (start of Frame) data "0xFF"							
		} 

	}		
		Incoming_Byte = Serial.read();
		if (Incoming_Byte == 0xFF & Serial.peek() == 0xFF){		// check that there are 2 "0xFF" header data
			Serial.read(); 										// clear 2nd 0xFF
			ID_Read = Serial.read();                    		// ID sent from Dynamixel
			Length_Read = Serial.read();						// Frame Length
			if( (Error_Byte = Serial.read()) != 0 ) {   		// See if error code was sent from Dynamixel	
				return (Error_Byte*(-1));					
				}
				
					Time_Counter = READ_TIME_OUT + millis(); 	// Setup time out error
					while(Serial.available() < Length_Read - 1)	// Wait loop for rest of data from Dynamixel			
					{					
						if ( millis() >= Time_Counter){
						return(-253);							// time out error , exit with fauilt code
						}
					}
									
		}else{
			return(-252);											//2nd Header data not recived, exit with fauilt code
			}
			
	Para1_Read = Serial.read();								// Positon Low data Value  
	Para2_Read = Serial.read();								// Positon High data Value 
	ChechS_Read = Serial.read();							// Check Sum data Value
//			if ( ((~(ID_Read + Length_Read + Para1_Read + Para2_Read )) & 0xFF) != ChechS_Read & 0xFF){	// Check sum of the recived data
//				return(-251);	
//			}			
	
	
			Speed_Long_Byte =  Para1_Read; 			
			Speed_Long_Byte = (Speed_Long_Byte << 8) + Para2_Read;

	return (Speed_Long_Byte);     // Returns the read position
}


int DynamixelClass::readVoltage(unsigned char ID)
{    
		int ID_Read;
		int Length_Read;
		int Para1_Read;
		int ChechS_Read;
		
	Checksum = ~(ID + AX_VOLT_LENGTH  + AX_READ_DATA + AX_PRESENT_VOLTAGE + AX_BYTE_READ);
	    
    digitalWrite(Direction_Pin,HIGH);
	Serial.flush();	
    Serial.write(AX_START);
    Serial.write(AX_START);
    Serial.write(ID);
    Serial.write(AX_VOLT_LENGTH);
    Serial.write(AX_READ_DATA);
    Serial.write(AX_PRESENT_VOLTAGE);
    Serial.write(AX_BYTE_READ);
    Serial.write(Checksum);							 
	delayMicroseconds(TX_READ_DELAY_TIME);	
    digitalWrite(Direction_Pin,LOW);     // Set Rx Mode 
	
	Voltage_Byte = 0;	
	Time_Counter = READ_TIME_OUT + millis(); 					// Setup time out error
	
    while(Serial.available() < 5 ) {							// Wait for header data, ID Length and error data from Dynamixel									
			if ( millis() >= Time_Counter) {
				return(-254);									// time out error , exit with fauilt code
			}					
		if( Serial.peek() != 0xFF )	{					
			Serial.read(); 										// Clear Present Byte so next Byte can be read as we are looking for header (start of Frame) data "0xFF"							
		} 

	}		
		Incoming_Byte = Serial.read();
		if (Incoming_Byte == 0xFF & Serial.peek() == 0xFF){		// check that there are 2 "0xFF" header data
			Serial.read(); 										// clear 2nd 0xFF
			ID_Read = Serial.read();                    		// ID sent from Dynamixel
			Length_Read = Serial.read();						// Frame Length
			Error_Byte = Serial.read();
			if( Error_Byte != 0 ) {   		// See if error code was sent from Dynamixel	
				return (Error_Byte*(-1));					
				}
				
					Time_Counter = READ_TIME_OUT + millis(); 	// Setup time out error
					while(Serial.available() < Length_Read - 1)	// Wait loop for rest of data from Dynamixel			
					{					
						if ( millis() >= Time_Counter){
						return(-253);							// time out error , exit with fauilt code
						}
					}
									
		}else{
			return(-252);											//2nd Header data not recived, exit with fauilt code
			}
		
		Para1_Read = Serial.read();							// Voltage Value  
		ChechS_Read = Serial.read();
			if ( ((~(ID_Read + Length_Read + Para1_Read)) & 0xFF) != ChechS_Read & 0xFF){	// Check sum of the recived data
				return(-251);	
			}				

	Voltage_Byte = Para1_Read / 10;			// Voltage read is 10 times so it needs to be devied
	return (Voltage_Byte);               	// Returns the read Voltage
}

