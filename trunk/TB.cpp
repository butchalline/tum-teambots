// Do not remove the include below
#include "TB.h"

//The setup function is called once at startup of the sketch
void setup() {
	stateMachine.Init();
	// usb.Init("Huawei", "U8655");
    //  motors.Init();
}

// The loop function is called in an endless loop
void loop() {
    stateMachine.Call();
}

int main() {
	setup();
	while(true) {
		loop();
	}
	return 0;
}
