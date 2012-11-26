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

#include "TB.h"

//The setup function is called once at startup of the sketch
void setup() {
	stateMachine.Init();
	Serial.begin(9600);
	Serial.print("Hello World!");
	pinMode(LED13, OUTPUT); // Pin setup for Visual indication of runing (heart beat) program using onboard LED
	digitalWrite(LED13, HIGH);
	motors.Init(); // Init Motors
}

// The loop function is called in an endless loop
void loop() {
	stateMachine.Call();
}
