#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#define LED1_RED 13 // use pin 13 to display analog value
AndroidAccessory acc("Google, Inc.",
"DemoKit",
"DemoKit Arduino Board",
"1.0",
"http://www.android.com",
"0000000012345678");
boolean flagval = true;

void setup()
{
Serial.begin(115200);
pinMode(LED1_RED, OUTPUT);
Serial.print("\r\nStart");
acc.powerOn();
}
void loop()
{
byte msg[3];
if (acc.isConnected()) {
if (flagval) {
Serial.println("Accessory connected. ");
int len = acc.read(msg, sizeof(msg), 1);
Serial.print("Message length: ");
Serial.println(len, DEC);
flagval = false;
}
int len = acc.read(msg, sizeof(msg), 1);
if (msg[0] == 0x2) {
if (msg[1] == 0x0)
analogWrite(LED1_RED, msg[2]);
}
}
delay(400);
/*
Delay time shall be long enough to make sure Android can detect USB Host.
For my HTC Desire, this value shall not less than 400 ms.
*/
} 