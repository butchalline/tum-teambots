package teambot.common.interfaces;

import teambot.common.hardware.SensorValue;

public interface ISensorListener
{
	public void newSensorValueCallback(SensorValue value);
}
