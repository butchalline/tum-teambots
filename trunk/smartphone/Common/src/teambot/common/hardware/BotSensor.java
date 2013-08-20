package teambot.common.hardware;

import java.util.LinkedList;
import java.util.List;

import teambot.common.PoseSupplier;
import teambot.common.data.Pose;
import teambot.common.interfaces.ISensorListener;

public class BotSensor
{
	protected PoseSupplier _poseSupplier;
	protected SensorValue _sensorValue;
	
	protected List<ISensorListener> _listeners = new LinkedList<ISensorListener>();
	
	public BotSensor(PoseSupplier poseSupplier)
	{
		_poseSupplier = poseSupplier;
	}
	
	public PoseSupplier getPoseSupplier()
	{
		return _poseSupplier;
	}
	
	public synchronized void registerForSensorValues(ISensorListener listener)
	{
		_listeners.add(listener);
	}
	
	public synchronized void unregisterForSensorValues(ISensorListener listener)
	{
		_listeners.remove(listener);
	}
	
	public synchronized Pose getLatestPose()
	{
		return _poseSupplier.getPose();
	}
	
	public synchronized SensorValue getLatestSensorValue()
	{
		return getValue();
	}
	
	protected synchronized SensorValue getValue()
	{
		return _sensorValue;
	}
	
	protected synchronized void setValue(SensorValue value)
	{
		_sensorValue = value;
	}
}
