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

#include "Odometry.h"
#include <math.h>
#include "IncSensor.h"

Odometry odometry;

const u_char wheelDiameter = 168; //Raddurchmesser in [mm]
const unsigned short wheelPerimeter = 525; //Radumfang 2 * pi* r
const unsigned short axialDistance = 275; // Roboterbreite in [mm]
const unsigned char numberOfInkr = 128;
const float pi = 3.1415926434;
const float inkrlength = wheelDiameter * pi / numberOfInkr;

void Odometry::Init()
{
	prevX = 0;
	prevY = 0;
	currX = 0;
	currY = 0;
	inkrRight = 0;
	inkrLeft = 0;
	prevInkrLeft = 0;
	prevInkrRight = 0;
	deltaInkrLeft = 0;
	deltaInkrRight = 0;
	roundsLeft = 0;
	roundsRight = 0;
	timeStep = 0;
	actVelocityLeft = 0;
	actVelocityRight = 0;
}

void Odometry::getInkr()
{

	prevInkrLeft = inkrLeft;
	prevInkrRight = inkrRight;
	inkrRight = incSensor.getPositionLeft();
	inkrLeft = incSensor.getPositionRight();
	//delta der Inkremente berechnen


	deltaInkrLeft = inkrLeft - prevInkrLeft;
	deltaInkrRight = inkrRight - prevInkrRight;

	//Bedingung: die Umdrehung darf nicht größer als die halbe Umdrehung eines Inkrementumlaufs sein.

	//Linkes Rad***********************************************
	if(deltaInkrLeft > 64)				//Überlauf nach hinten
	{
		deltaInkrLeft -= 128;
		--roundsLeft;
	}
	if(deltaInkrLeft < -64)				//Überlauf nach vorne
	{
		deltaInkrLeft += 128;
		++roundsLeft;
	}
	//Rechtes Rad**********************************************
	if(deltaInkrRight > 64)				//Überlauf nach hinten
	{
		deltaInkrRight -= 128;
		++roundsRight;
	}
	if(deltaInkrRight < -64)			//Überlauf nach vorne
	{
		deltaInkrRight += 128;
		--roundsRight;
	}

	deltaInkrRight ^= 0b1<<8 ; //toggle Vorzeichen //da die Richtung der Motoren umgedreht ist
}

void Odometry::calcPosition()
{
	prevAngle = currAngle;
	prevX = currX;
	prevY = currY;
	getInkr();

    /**
     * Strecke
     * (dR + dL) / 2
     * Winkel
     * (dR - dL) / Achsabstand
     *
     * x = x + strecke * cos(winkel)
     * y = y + strecke * sin(winkel)
     * winkel = winkel + winkel
     */

	distLeft = deltaInkrLeft * inkrlength;
	distRight = deltaInkrRight * inkrlength;


    float s = (distRight + distLeft) / 2;		//mittlere zurückgelegte Strecke der Räder seit der letzten Messung
    float w = (distRight - distLeft) / axialDistance; //relativer Winkel seit der letzen Messung

    currAngle -= w;
    if (currAngle <= - pi)
    	currAngle += 2*pi;

    if( currAngle > pi)
    	currAngle -= 2*pi;

    currX = prevX + (s * sin(currAngle));						//  Math.Sin(Angle));
    currY = prevY + (s * cos(currAngle)); 						//Math.Cos(Angle));

}


void Odometry::update(unsigned long deltaTime){
	this->timeStep = deltaTime;
	this->getInkr();
	this->calcPosition();

	//calc delta v für linkes und rechtes Rad
	//Zeit in Millisekunden
	//Strecke in mm

	actVelocityLeft = (distLeft)/(1000*timeStep);
	actVelocityRight = (distRight)/(1000*timeStep);
}
