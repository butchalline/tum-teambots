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

#ifndef __CONTROL_H_
#define __CONTROL_H_

#include "Config.h"
#include "common/Types.h"

/**
 * @param delta_phi 	the given value from the tablet
 * @param phi
 */

class Control {

public:
	Control();
	void Init();
	//void motor_omega(u_short omega_soll, u_short omega_ist);
	u_short omega_observer_left(u_short phi);
	u_short omega_observer_right(u_short phi);
	short motor_velocity_control(short omega_soll, short omega_ist);


private:
	u_short omega_left;
	u_short omega_right;
	double k;
};

extern Control motorControl;

#endif /* __CONTROL_H_ */
