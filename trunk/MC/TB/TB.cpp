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
	Serial.begin(115200);
	Serial.print("Hello World!");
	motors.Init(); // Init Motors
}

// The loop function is called in an endless loop
void loop() {
	stateMachine.Call();
}

//Timer interrupt für Zeit Messung



/* wird nicht als Timer benötigt, da Arduino schon timerfunktion bereitstellt
void timerInit()
{
	// Todo synchronisiere mit Android
	//_______________________________________________________________________
	TCCR1B |= (0<<CS12) | (0<<CS11) | (1<<CS10);		//setze Prescaler auf 1 (16 Mhz)
	TCCR1B &= !(1<<CS12) & !(1<<CS11) & !(0<<CS10);		//(0 0 1)

	TIMSK1 |= 1<<ICIE1;					//enable Interrupt für Timer1
	sei();								//global interrupt enable
}

ISR(TIMER1_OVF_vect)
{
//compare interrupt auf 16000(für eine millisekunde)
}
*/
