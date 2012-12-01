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
#include "IncSensor.h"

IncSensor 	incSensor;

IncSensor::IncSensor(){
	roundsLeft = 0;
	roundsRight = 0;
	angleLeft = 0;
	angleRight = 0;
}

void IncSensor::Init(){
	pinMode(INKR_SENSOR_LEFT_1, INPUT);
	pinMode(INKR_SENSOR_LEFT_2, INPUT);
	pinMode(INKR_SENSOR_LEFT_3, INPUT);
	pinMode(INKR_SENSOR_LEFT_4, INPUT);
	pinMode(INKR_SENSOR_LEFT_5, INPUT);
	pinMode(INKR_SENSOR_LEFT_6, INPUT);
	pinMode(INKR_SENSOR_LEFT_7, INPUT);
	pinMode(INKR_SENSOR_LEFT_8, INPUT);

	pinMode(INKR_SENSOR_RIGHT_1, INPUT);
	pinMode(INKR_SENSOR_RIGHT_2, INPUT);
	pinMode(INKR_SENSOR_RIGHT_3, INPUT);
	pinMode(INKR_SENSOR_RIGHT_4, INPUT);
	pinMode(INKR_SENSOR_RIGHT_5, INPUT);
	pinMode(INKR_SENSOR_RIGHT_6, INPUT);
	pinMode(INKR_SENSOR_RIGHT_7, INPUT);
	pinMode(INKR_SENSOR_RIGHT_8, INPUT);
};

u_char IncSensor::getPositionLeft(){
	u_char posLeft = 0;

	return  posLeft;
}

u_char IncSensor::getPositionRight(){
	u_char posRight = 0;
	return posRight;
}


char IncSensor::table[128] =  {0b01111111, 0b00111111, 0b00111110, 0b00111010, 0b00111000, 0b10111000, 0b10011000, 0b00011000,
							0b00001000, 0b01001000, 0b01001001, 0b01001101, 0b01001111, 0b00001111, 0b00101111, 0b10101111,
							0b10111111, 0b10011111, 0b00011111, 0b00011101, 0b00011100, 0b01011100, 0b01001100, 0b00001100,
							0b00000100, 0b00100100, 0b10100100, 0b10100110, 0b10100111, 0b10000111, 0b10010111, 0b11010111,
							0b11011111, 0b11001111, 0b10001111, 0b10001110, 0b00101110, 0b00100110, 0b00000110, 0b00000010,
							0b00010010, 0b01010010, 0b01010011, 0b11010011, 0b11000011, 0b11001011, 0b11101011, 0b11101111,
							0b11100111, 0b11000111, 0b01000111, 0b00000111, 0b00010111, 0b00010011, 0b00000011, 0b00000001,
							0b00001001, 0b00101001, 0b10101001, 0b11101001, 0b11100001, 0b11100101, 0b11110101, 0b11110111,
							0b11110011, 0b11100011, 0b10100011, 0b10000011, 0b10000011, 0b10001011, 0b10001001, 0b10000001,
							0b10000000, 0b10000100, 0b10010100, 0b11010100, 0b11110100, 0b11110000, 0b11110010, 0b11111010,
							0b11111011, 0b11111001, 0b11110001, 0b11010001, 0b11000001, 0b11000101, 0b11000100, 0b11000000,
							0b01000000, 0b01000010, 0b01001010, 0b01101010, 0b01111010, 0b01111000, 0b01111001, 0b01111101,
							0b11111101, 0b11111100, 0b11111000, 0b11101000, 0b11100000, 0b11100010, 0b01100010, 0b01100000,
							0b00100000, 0b00100001, 0b00110101, 0b00110101, 0b00111101, 0b00111100, 0b10111100, 0b10111110,
							0b11111110, 0b01111110, 0b01111100, 0b01110100, 0b01110000, 0b01110001, 0b00110001, 0b00110000,
							0b00010000, 0b10010000, 0b10010010, 0b10011010, 0b10011110, 0b00011110, 0b01011110, 0b01011111};
