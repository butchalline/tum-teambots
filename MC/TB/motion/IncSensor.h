/*
 * IncrementalSensor.h
 *
 *  Created on: 30 Nov 2012
 *      Author: maze
 */

#ifndef INCREMENTALSENSOR_H_
#define INCREMENTALSENSOR_H_




#include "Config.h"

class IncSensor {
public:
	IncSensor();
	void Init();

	u_char getPositionLeft();
	u_char getPositionRight();



private:
	static char table[128];
	short roundsLeft;	//number of rounds of the motors
	short roundsRight;
	float angleLeft;	//current angle of the motor 0° --> 12 Uhr?
	float angleRight;

};
extern IncSensor 	incSensor;




#endif /* INCREMENTALSENSOR_H_ */
