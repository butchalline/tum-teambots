package teambot.smartphone.sensortest;

import java.util.concurrent.ConcurrentLinkedQueue;

import dataLogger.LogDistributionManager;

import teambotData.DataType;
import teambotData.FloatArrayData;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorListener implements SensorEventListener {

	protected SensorManager sensorManager;
	protected Sensor sensor;
	protected DataType dataType;
	public ConcurrentLinkedQueue<FloatArrayData> latestValues;

	LogDistributionManager logger;
	Calibration calibration;

	public SensorListener(SensorManager sensorManager, Sensor sensor,
			DataType sensorDataType, LogDistributionManager logger, Calibration calibration) {
		this.sensorManager = sensorManager;
		this.sensor = sensor;
		this.dataType = sensorDataType;
		this.logger = logger;
		this.calibration = calibration;
		latestValues = new ConcurrentLinkedQueue<FloatArrayData>();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		FloatArrayData data = new FloatArrayData(event.timestamp, calibration.adjust(event.values),
				dataType);
		// logger.log(data);
		
		latestValues.add(data);
		if (latestValues.size() >= 4)
			latestValues.remove();
	}

	public void startListening() {
		this.sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void stopListening() {
		sensorManager.unregisterListener(this);
	}
}
