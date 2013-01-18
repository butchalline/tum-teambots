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
//	void calcRoundLeft();
//	void calcRoundRight();
//	char getRoundsLeft();
//	char getRoundsRight();



private:

	static const u_char table[128];
//	short inkrRoundsLeft;	//number of rounds of the motors
//	short inkrRoundsRight;
//	char roundsLeft;
//	char roundsRight;
//	char lastPosRight;
//	char lastPosLeft;
//	char actPosRight;
//	char actPosLeft;
};

extern IncSensor 	incSensor;

#endif /* INCREMENTALSENSOR_H_ */
