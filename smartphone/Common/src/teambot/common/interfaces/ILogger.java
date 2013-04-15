package teambot.common.interfaces;

import teambot.common.usb.data.ByteArrayData;
import teambot.common.usb.data.FloatArrayData;

public interface ILogger extends Runnable {
	public void save(ByteArrayData data);
	public void save(FloatArrayData data);
	public void stop();
}
