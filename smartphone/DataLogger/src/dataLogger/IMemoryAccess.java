package dataLogger;

import teambotData.Data;

public interface IMemoryAccess extends Runnable {
	void save(Data data);
	void stop();
}
