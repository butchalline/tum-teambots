package teambot.common.hardware;

public class SensorValue
{
	int _timestamp;
	float _value;
	
	public SensorValue(float value, int timestamp)
	{
		_timestamp = timestamp;
		_value = value;
	}
	
	public SensorValue(float value)
	{
		_timestamp = -1;
		_value = value;
	}
	
	public SensorValue(byte value, int timestamp)
	{
		_timestamp = timestamp;
		_value = value & 0xFF;
	}
	
	public SensorValue(byte value)
	{
		_timestamp = -1;
		_value = value & 0xFF;
	}
	
	public float value()
	{
		return _value;
	}
	
	public int timestamp()
	{
		return _timestamp;
	}
}
