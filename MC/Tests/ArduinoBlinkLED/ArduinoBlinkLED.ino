#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define LED_PIN 13

// accessory descriptor. It's how Arduino identifies itself to Android
char applicationName[] = "ArduinoBlinkLED"; // the app on your phone
char accessoryName[] = "Mega_ADK"; // your Arduino board
char companyName[] = "Arduino SA";

// make up anything you want for these
char versionNumber[] = "1.0";
char serialNumber[] = "1";
char url[] = "http://labs.arduino.cc/adk/ADK_count"; // the URL of your app online

AndroidAccessory usb(companyName, applicationName,
accessoryName,versionNumber,url,serialNumber);

long timer = millis();

void setup()
{
  // set communiation speed
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);
  usb.powerOn();  
  Serial.print("\r\nStart");
}

int connected = 0;

void loop()
{
  if(millis()-timer>100)
  {
    if (usb.isConnected() && usb.peek() == -1)
    {
      int peekABoo = usb.peek();
      Serial.print("\r\nPeek: ");
      Serial.print(peekABoo);
    }
    if (usb.isConnected() && usb.peek() != -1)  // isConnected makes sure the USB connection is open
    {
      if(connected == 0)
      {
        Serial.print("\r\nConnected");
        connected = 1;
      }
      
      int val = usb.read();
      Serial.print("\r\nRead from usb: ");
      Serial.print( val );
      if( val == 1 )
        digitalWrite(LED_PIN, HIGH);
      else
        digitalWrite(LED_PIN, LOW);
    }
    else
    {
      if(connected == 1)
      {
        Serial.print("\r\nDisconnected");
        connected = 0;
      }
      digitalWrite(LED_PIN , LOW); // turn off light
    }
    timer = millis();
  }
}
