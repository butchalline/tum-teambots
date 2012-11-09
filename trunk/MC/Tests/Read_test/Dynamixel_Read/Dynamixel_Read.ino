#include <Dynamixel_Serial.h>

int Temperature,Voltage,Position; 

//#include <Dynamixel_Serial.h>       // Library needed to control Dynamixal servo

#define SERVO_ID 0x01               // ID of which we will set Dynamixel too 
#define SERVO_ControlPin 0x02       // Control pin of buffer chip, NOTE: this does not matter becasue we are not using a half to full contorl buffer.
#define SERVO_SET_Baudrate 1000000  // Baud rate speed which the Dynamixel will be set too (1Mbps)
#define LED13 0x0D                  // Pin of Visual indication for runing "heart beat" using onboard LED
#define MotorPower 0x06


void setup(){
  
//digitalWrite(0x06, HIGH);
//delay(1000);
pinMode(0x02, OUTPUT);
pinMode(0x03, OUTPUT);
digitalWrite(0x03, LOW);
Dynamixel.begin(1000000,0x02);
delay(1000);
Dynamixel.endlessEnable(1, OFF);

Dynamixel.torqueMax(1, 0x2FF);
Dynamixel.begin(1000000,0x02);  // Inicialize the servo at 1Mbps and Pin Control 2
//digitalWrite(0x06, HIGH);
delay(1000);
}

void loop(){
  Temperature = Dynamixel.readTemperature(1); // Request and Print the Temperature
  Voltage = Dynamixel.readVoltage(1);         // Request and Print the Voltage
  Position = Dynamixel.readPosition(1);       // Request and Print the Position 
 
  Dynamixel.move(1,random(200,800));  // Move the Servo radomly from 200 to 800
 
 Dynamixel.end();                 // End Servo Comunication
 Serial.begin(9600);              // Begin Serial Comunication
 
  Serial.print(" *** Temperature: ");   // Print the variables in the Serial Monitor
  Serial.print(Temperature);
  Serial.print(" Celcius  Voltage: ");
  Serial.print(Voltage);
  Serial.print("  Volts   Position: ");
  Serial.print(Position);
  Serial.println(" of 1023 resolution");
  
 Serial.end();                     // End the Serial Comunication
 Dynamixel.begin(1000000,0x2);         // Begin Servo Comunication
 
delay(1000);
Dynamixel.ledState(SERVO_ID, true);
 delay(1000);
Dynamixel.ledState(SERVO_ID, false);

Dynamixel.turn(SERVO_ID,RIGHT,0x3FF);


}
