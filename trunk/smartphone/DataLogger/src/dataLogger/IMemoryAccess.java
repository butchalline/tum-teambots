package dataLogger;

import teambotData.ByteArrayData;
import teambotData.FloatArrayData;

public interface IMemoryAccess extends Runnable {
	void save(ByteArrayData data);
	void save(FloatArrayData data);
	void stop();
}
