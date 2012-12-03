package dataLogger;

import teambotData.Data;

enum LoggerStatus {
	IDLE,
	OCCUPIED,
	CRITICAL,
	AT_LIMIT
}

public interface IDataLogger extends Runnable {
	
	public boolean log(Data data);
	public LoggerStatus getStatus();
}