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

#include "common/StateMachine.h"
#include "network/Usb.h"
#include "network/Data.h"
#include "motion/Motor.h"
#include "motion/Odometry.h"

StateMachine stateMachine; //Global StateMachine Object
TBFrame* receiveFrame;

void StateMachine::Init() {
	currentState = Idle; //PhoneDisconnected;
#if TB_DEBUG_MODE_ENABLED
	currentDebugState = DebugInit;
#endif
	receiveFrame = new TBFrame();
}

void StateMachine::handleVelocity() {
	if (currentState != DriveVelocity)
		return;

	if (receiveFrame->head.SubId == TB_VELOCITY_FORWARD) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4,
				receiveFrame->data.velocity.speedRight * 4, motors.Forwards,
				motors.Forwards);
	} else if (receiveFrame->head.SubId == TB_VELOCITY_BACKWARD) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4,
				receiveFrame->data.velocity.speedRight * 4, motors.Backwards,
				motors.Backwards);
	} else if (receiveFrame->head.SubId == TB_VELOCITY_TURN_LEFT) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4,
				receiveFrame->data.velocity.speedRight * 4, motors.Backwards,
				motors.Forwards);
	} else if (receiveFrame->head.SubId == TB_VELOCITY_TURN_RIGHT) {
		motors.setVelocity(receiveFrame->data.velocity.speedLeft * 4,
				receiveFrame->data.velocity.speedRight * 4, motors.Forwards,
				motors.Backwards);
	}
}

void StateMachine::preHandle() {
	if (currentState != PhoneDisconnected && usb.sizeData() > 0
			&& currentState != DebugMode) {
		u_char bytes = sizeof(TBHeader) - sizeof(u_char); //Head - Primary ID
		char* tmp = (char*) receiveFrame;
		*tmp = usb.read();

		switch (receiveFrame->head.Id) {
		case TB_COMMAND_ID:
			break;
		case TB_VELOCITY_ID:
			bytes += sizeof(TBVelocity);
			for (int i = 0; i < bytes; ++i) { //read rest
				++tmp;
				*tmp = usb.read();
			}
			handleVelocity();
			break;
//		case TB_TURN_ID:
//			break;
		case TB_POSITION_ID:
			break;
		case TB_ERROR_ID:
			break;
		}
	}

	//odometry.update(timer.stop()); //Schleife für den Durchlauf eines Mess-Schrittes
	//timer.start();
}

void StateMachine::postHandle() {
	if (currentState != PhoneDisconnected && currentState != DebugMode) {
		usb.send();
	}
}

void StateMachine::Call() {
	this->preHandle();
	switch (currentState) {
	case Idle:
		delay(500);
		Serial.print("Idle\n\r");
#if TB_STARTUP_IN_DEBUGMODE
		requireState(DebugMode);
#endif
		break;
	case DrivePosition:
		break;
	case DriveVelocity:
		Serial.print("Drive Velocity\n\r");
		motors.driveVeloctiy();
		break;
	case PositionReached:
		break;

	case PhoneConnect: {
		Serial.print("Try Connect\n\r");
		usb.reconnect();
		if (!usb.isConnected())
			requireState(PhoneDisconnected);
		else {
			Serial.print("Connected\n\r");
			requireState(Idle);
		}
		break;
	}

	case PhoneDisconnected: {
		Serial.print("Phone Disconnected\n\r");
		delay(1000);
		this->requireState(PhoneConnect);
	}
		break;
	case Error:
		break;

#if TB_DEBUG_MODE_ENABLED
	case DebugMode:
		debugStateLoop();
		break;
#endif
	}
	this->postHandle();
}

TBState StateMachine::requireState(TBState state) {
	switch (currentState) {
	case Idle:
		switch (state) {
		case DriveVelocity:
			currentState = DriveVelocity;
			break;
#if(TB_DEBUG_MODE_ENABLED)
		case DebugMode:
			Serial.print("DebugMode Requested\n\r");
			currentState = DebugMode;
			break;
#endif
		default:
			//TODO: Log Error
			break;
		}
		return currentState;
	case DrivePosition:
		return currentState;
	case DriveVelocity:
		return currentState;
	case PositionReached:
		return currentState;

	case PhoneConnect: {
		switch (state) {
		case Idle:
			currentState = Idle;
			break;
		case PhoneDisconnected:
			currentState = PhoneDisconnected;
			break;
		default:
			//TODO: Log Error
			break;
		}
		return currentState;
	}

	case PhoneDisconnected: {
		switch (state) {
		case PhoneConnect:
			currentState = PhoneConnect;
			break;
		default:
			//TODO: Log Error
			break;
		}
		return currentState;
	}

	case Error:
		return currentState;
	default:
		return currentState;
	}
}

#if TB_DEBUG_MODE_ENABLED

unsigned long time;
unsigned long meassureEnd;
int cnt;
TBQueue<int> valueQueue;
bool banging;
bool forwardBang;
int tmp_sensor_value;
int sharp_distance;

void StateMachine::debugStateLoop() {
	switch (currentDebugState) {
	case DebugInit:
		Serial.print("DebugMode - Init\n\r");
		//queueList.push(DebugSetID_Right);
		//queueList.push(DebugSetID_Left);
		//queueList.push(DebugSetID_Tablet);
		/*for(int i = 0; i < 10; i++)
		{
		queueList.push(DebugRunMotorTablet);
		queueList.push(DebugRunMotorLeft);
		queueList.push(DebugRunMotorRight);
		queueList.push(DebugRunMotorAll);
		}*/
//		queueList.push(DebugTabletPositions);
//		queueList.push(DebugTabletBanging);
//		queueList.push(DebugDoNothing);
		queueList.push(DebugGetSharpSensorValues);
		//queueList.push(DebugReadPoti);
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugDoNothing:
		Serial.print("I Do Nothing! ^_^");
		delay(100);
		break;
	case DebugRunMotorLeft:
		Serial.print("Debug Mode run LeftMotor\n\r");
		Serial.print("left Start Forwards\n\r");
		motors.setVelocity(50 * 4, 0, motors.Forwards, motors.Forwards);
		motors.driveVeloctiy();
		delay(2000);
		Serial.print("left Start Backwards\n\r");
		motors.setVelocity(50 * 4, 0, motors.Backwards, motors.Forwards);
		motors.driveVeloctiy();
		delay(2000);
		Serial.print("Stop\n\r");
		motors.setVelocity(0, 0);
		motors.driveVeloctiy();
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugRunMotorRight:
		Serial.print("Debug Mode run RightMotor\n\r");
		Serial.print("right Start Forwards\n\r");
		motors.setVelocity(0, 50 * 4, motors.Forwards, motors.Forwards);
		motors.driveVeloctiy();
		delay(2000);
		Serial.print("right Start Backwards\n\r");
		motors.setVelocity(0, 50 * 4, motors.Forwards, motors.Backwards);
		motors.driveVeloctiy();
		delay(2000);
		Serial.print("Stop\n\r");
		motors.setVelocity(0, 0);
		motors.driveVeloctiy();
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugRunMotorTablet:
		Serial.print("Debug Mode run tabletMotor\n\r");
		Serial.print("Tablet Start Forwards\n\r");
		motors.setTabletPosition(0, 150);
		motors.driveVeloctiy();
		delay(2000);
		Serial.print("Tablet Start Backwards\n\r");
		motors.setTabletPosition(200, 150);
		motors.driveVeloctiy();
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugRunMotorAll:
		Serial.print("Debug Mode run all\n\r");
		Serial.print("all Forwards\n\r");
		motors.setVelocity(50 * 4, 50 * 4, motors.Forwards, motors.Forwards);
		motors.setTabletPosition(0, 150);
		motors.driveVeloctiy();
		delay(2000);
		Serial.print("all Backwards\n\r");
		motors.setVelocity(50 * 4, 50 * 4, motors.Backwards, motors.Backwards);
		motors.setTabletPosition(200, 150);
		motors.driveVeloctiy();
		delay(2000);
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugTabletPositions:
		Serial.print("Debug Mode Tablet Positions\n\r");
		motors.setTabletPosition(200, 200);
		motors.driveVeloctiy();
		delay(5000);
		for(int i = TABLET_Max_Front; i < TABLET_Max_Back; i += 10)
		{
		Serial.print("Set Position to ");
		Serial.println(i);
		motors.setTabletPosition(i, 200);
		motors.driveVeloctiy();
		delay(500);
		}

		currentDebugState = DebugTestEnvironment;
		break;
	case DebugTabletBanging:
		banging = true; // WE BANG 4Ever
		forwardBang = true;
		motors.setTabletPosition(TABLET_Horizontal, 200);
		motors.driveVeloctiy();
		while(banging)
		{
			delay(700);
			forwardBang == true ? motors.setTabletPosition(TABLET_Horizontal, 400) : motors.setTabletPosition(TABLET_Vertical, 200);
			forwardBang = !forwardBang;
			motors.driveVeloctiy();
			delay(300);
			forwardBang == true ? motors.setTabletPosition(TABLET_Horizontal, 400) : motors.setTabletPosition(TABLET_Vertical, 200);
			forwardBang = !forwardBang;
			motors.driveVeloctiy();
		}
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugReadPoti:
		pinMode(A0, INPUT);
		cnt = 0;

		Serial.print("Time: ");
		Serial.println(time);
		Serial.print("Time End: ");
		Serial.print(meassureEnd);
		Serial.println("Start Motors");
		Serial.println(analogRead(A0));
		motors.setVelocity(50 * 4, 50 * 4, motors.Forwards, motors.Forwards);
		motors.driveVeloctiy();
		Serial.println("Start Measurement of Poti");
		Serial.println(
				"-------------------------------------------------------");
		time = millis();
		meassureEnd = time + 20000; //10 Seconds
		while (millis() < meassureEnd) {
			//	valueQueue.push(cnt);
			//	valueQueue.push(analogRead(A0));
			//cnt++;
			delay(1);
			Serial.print(cnt);
			Serial.print(",");
			Serial.print(analogRead(A0));
			Serial.println(";");
			++cnt;
		}
		Serial.println(
				"-------------------------------------------------------");
		Serial.println("Stop Measurement of Poti");
		Serial.println("Stop Motors");
		motors.setVelocity(0, 0, motors.Forwards, motors.Forwards);
		motors.driveVeloctiy();
		Serial.println("Print Data:");
		Serial.println(
				"-------------------------------------------------------");
		while (valueQueue.hasNext()) {
			int value = 0;
			valueQueue.pop(value);
			Serial.print(value);
			Serial.print(",");
			valueQueue.pop(value);
			Serial.print(value);
			Serial.println(";");
		}
		Serial.println(
				"-------------------------------------------------------");
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugTestEnvironment:
		Serial.print("------------------------------------------------\n\r");
		if (queueList.hasNext()) {
			Serial.print("Starting next Debug Test\n\r");
			queueList.pop(currentDebugState);
		}
		Serial.print("------------------------------------------------\n\r");
		break;

	case DebugSetID_Left:
		Serial.print(
				"Debug Mode Set ID Left : Attach Motor - Delay 5 Seconds\n\r");
		Serial.print("5..");
		delay(1000);
		Serial.print("4..");
		delay(1000);
		Serial.print("3..");
		delay(1000);
		Serial.print("2..");
		delay(1000);
		Serial.print("1..");
		delay(1000);
		Serial.print("0!\n\r");
		motors.setID(MOTOR_ID_LEFT);
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugSetID_Right:
		Serial.print(
				"Debug Mode Set ID Right : Attach Motor - Delay 5 Seconds\n\r");
		Serial.print("5..");
		delay(1000);
		Serial.print("4..");
		delay(1000);
		Serial.print("3..");
		delay(1000);
		Serial.print("2..");
		delay(1000);
		Serial.print("1..");
		delay(1000);
		Serial.print("0!\n\r");
		motors.setID(MOTOR_ID_RIGHT);
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugSetID_Tablet:
		Serial.print("Debug Mode Set ID Tablet : Attach Motor - Delay 5 Seconds\n\r");
		Serial.print("5..");
		delay(1000);
		Serial.print("4..");
		delay(1000);
		Serial.print("3..");
		delay(1000);
		Serial.print("2..");
		delay(1000);
		Serial.print("1..");
		delay(1000);
		Serial.print("0!\n\r");
		motors.setID(MOTOR_ID_TABLET);
		currentDebugState = DebugTestEnvironment;
		break;
	case DebugGetSharpSensorValues:
		tmp_sensor_value = 0;
		sharp_distance = 0;
		Serial.print("Debug Mode output the Infrared values. ");
		while(true){
			if(digitalRead(SENSOR_BUMBER_FRONT_RIGHT) == LOW){
				sharp_distance = sharp_distance + 25;
				Serial.println();
				Serial.print("neuer Abstand [in mm]: ");
				Serial.print(sharp_distance); Serial.println();
			}
			if(digitalRead(SENSOR_BUMBER_FRONT_LEFT) == LOW ){
				Serial.println();
				Serial.print("Ignore values!."); Serial.println();
			}
		tmp_sensor_value = analogRead(SENSOR_INFRARET_SHARP);
		Serial.println(tmp_sensor_value);
		delay(500);
		}
		currentDebugState = DebugTestEnvironment;
		break;
	default:
		break;
	}
}
#endif
