package teambot.remote;

import java.util.Vector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class PitchRollSupplier implements SensorEventListener
{
	protected static final int _updateIntervall_hz = 30;

	protected Vector<IPitchRollListener> _listeners = new Vector<IPitchRollListener>();

	protected long _timestamp = 0;
	protected float _pitch;
	protected float _roll;
	
	public void registerListener(IPitchRollListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;

		if (event.timestamp < _timestamp + 1000000000f / _updateIntervall_hz)
			return;

		_timestamp = event.timestamp;
		_pitch = -event.values[0];
		_roll = event.values[1];

		for(IPitchRollListener listener : _listeners)
		{
			listener.onPitchRollChange(_pitch, _roll);
		}
	}

}
