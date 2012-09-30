package teambot.smartphone.sensortest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Calibration implements SensorEventListener {

	protected SensorManager sensorManager;
	protected Sensor sensor;
	protected float[] offsets;
	protected long measurementCount = 0;
	protected boolean calibrationRunning = false;

	public Calibration() {
		offsets = new float[3];
		for (int i = 0; i < 3; i++)
			offsets[i] = 0;
	}
	
	public Calibration(SensorManager sensorManager, Sensor sensor) {
		initialize(sensorManager, sensor, 3);
	}

	public Calibration(SensorManager sensorManager, Sensor sensor,
			int numberOfSensorValues) {
		initialize(sensorManager, sensor, numberOfSensorValues);
	}

	protected void initialize(SensorManager sensorManager, Sensor sensor,
			int numberOfSensorValues) {
		this.sensorManager = sensorManager;
		this.sensor = sensor;

		offsets = new float[numberOfSensorValues];
		for (int i = 0; i < numberOfSensorValues; i++)
			offsets[i] = 0;
	}

	public void toggleCalibration() {
		if(calibrationRunning)
		{
			stopCalibration();
			calibrationRunning = false;
		}
		else{
			startCalibration();
			calibrationRunning = true;
		}
	}
	
	public void startCalibration() {
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void stopCalibration() {
		sensorManager.unregisterListener(this);
		for (int i = 0; i < offsets.length; i++)
			offsets[i] = offsets[i] / (float) measurementCount;
		measurementCount = 0;
	}

	public float[] adjust(float[] measurement) {
		for (int i = 0; i < measurement.length; i++)
			measurement[i] += offsets[i];
		return measurement;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		for (int i = 0; i < event.values.length; i++)
			offsets[i] += event.values[i];
		measurementCount++;
	}
	
	public float[] getOffsets() {
		return offsets;
	}
	
	public void reset() {
		for (int i = 0; i < offsets.length; i++)
			offsets[i] = 0;
	}
}
