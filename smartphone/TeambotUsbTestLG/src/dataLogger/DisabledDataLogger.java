package dataLogger;

import teambotData.Data;

public class DisabledDataLogger implements IDataLogger {

	@Override
	public void run() {
		
	}

	@Override
	public boolean log(Data data) {
		return false;
	}

	@Override
	public LoggerStatus getStatus() {
		return LoggerStatus.IDLE;
	}

}
