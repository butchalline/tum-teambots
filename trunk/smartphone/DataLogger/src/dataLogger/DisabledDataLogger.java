package dataLogger;

import teambotData.Data;

public class DisabledDataLogger implements IDataLogger {

	@Override
	public void run() {
		
	}

	@Override
	public void log(Data data) {		
	}

	@Override
	public LoggerStatus getStatus() {
		return LoggerStatus.AT_LIMIT;
	}

}
