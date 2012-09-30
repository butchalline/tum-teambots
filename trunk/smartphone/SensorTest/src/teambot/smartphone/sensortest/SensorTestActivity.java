package teambot.smartphone.sensortest;

import java.util.List;

import dataLogger.LogDistributionManager;
import dataLogger.NetworkAccess;

import teambotData.DataType;
import teambotData.Info;
import teambot.smartphone.positionEstimation.PositionEstimator;
import teambot.smartphone.sensortest.R;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SensorTestActivity extends Activity {

	LogDistributionManager ldm;
	SensorManager mSensorManager;
	Calibration linearAccelCalibration;
	PositionEstimator positionEstimator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_test);

		ldm = new LogDistributionManager(new NetworkAccess("192.168.0.101",
				"10000"));
		ldm.log(new Info("App started"));

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		
		Sensor linearAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		linearAccelCalibration = new Calibration(mSensorManager, linearAccelSensor);
		SensorListener accelListener = new SensorListener(mSensorManager, linearAccelSensor, DataType.ACCELEROMETER, ldm, linearAccelCalibration); 
//		accelListener.startListening();
		
		Sensor gyroSensor = (mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE)).get(1);
		SensorListener gyroListener = new SensorListener(mSensorManager, gyroSensor, DataType.GYROSCOPE, ldm, new Calibration()); 
		
        ((Button)findViewById(R.id.toggletButton)).setOnClickListener(calibrationToggle);
        ((Button)findViewById(R.id.logButton)).setOnClickListener(calibrationLogListener);        
        ((Button)findViewById(R.id.resetButton)).setOnClickListener(calibrationReset);   
		
        positionEstimator = new PositionEstimator(accelListener, gyroListener, ldm);
        accelListener.startListening();
        gyroListener.startListening();
        
        ((Button)findViewById(R.id.trackingButton)).setOnClickListener(trackingListener); 
        
//		The linear acceleration sensor always has an offset, which you need to remove.
//		The simplest way to do this is to build a calibration step into your application.
//		During calibration you can ask the user to set the device on a table, and then read the offsets for all three axes.
//		You can then subtract that offset from the acceleration sensor's direct readings to get the actual linear acceleration.
//		The sensor coordinate system is the same as the one used by the acceleration sensor, as are the units of measure (m/s2). 
		
//		rotation vector sensor uses the gravity field
//      use Sensor12 - Corrected Gyroscope Sensor; it doesn't use magnetic field or something so should work without influence
//      of electric stuff. The correction is just throwing away nonsense values and some filtering to make it more stable:
//        
//        const float dT = (event.timestamp - mGyroTime) / 1000000000.0f;
//        const float freq = 1 / dT;
//        if (freq >= 100 && freq<1000) { // filter values obviously wrong
//            const float alpha = 1 / (1 + dT); // 1s time-constant
//            mGyroRate = freq + (mGyroRate - freq)*alpha;
//        }

	}

	@SuppressWarnings("unused")
	private void logAllSensorInfo() {
		List<Sensor> deviceSensors = mSensorManager
				.getSensorList(Sensor.TYPE_ALL);
		Sensor sensor;
		for (int i = 0; i < deviceSensors.size(); i++) {
			sensor = deviceSensors.get(i);
			ldm.log(new Info("--------------------------------------"));
			ldm.log(new Info("Sensor" + i + " - " + sensor.getName() + ":"));
			ldm.log(new Info("type: " + typeIdToString(sensor.getType())));
			ldm.log(new Info("min delay [microsecs]: " + sensor.getMinDelay()));
			ldm.log(new Info("power consumption [mA]: " + sensor.getPower()));
			ldm.log(new Info("resolution: " + sensor.getResolution()));
			ldm.log(new Info("max range: " + sensor.getMaximumRange()));
			ldm.log(new Info("vendor: " + sensor.getVendor()));
			ldm.log(new Info("version: " + sensor.getVersion()));
		}
	}

	@SuppressWarnings("deprecation")
	private String typeIdToString(int type) {
		switch (type) {
		case (Sensor.TYPE_ACCELEROMETER):
			return "ACCELEROMETER";
		case (Sensor.TYPE_AMBIENT_TEMPERATURE):
			return "AMBIENT_TEMPERATURE";
		case (Sensor.TYPE_GRAVITY):
			return "GRAVITY";
		case (Sensor.TYPE_GYROSCOPE):
			return "GYROSCOPE";
		case (Sensor.TYPE_LIGHT):
			return "LIGHT";
		case (Sensor.TYPE_LINEAR_ACCELERATION):
			return "LINEAR_ACCELERATION";
		case (Sensor.TYPE_MAGNETIC_FIELD):
			return "MAGNETIC_FIELD";
		case (Sensor.TYPE_ORIENTATION):
			return "ORIENTATION";
		case (Sensor.TYPE_PRESSURE):
			return "PRESSURE";
		case (Sensor.TYPE_PROXIMITY):
			return "PROXIMITY";
		case (Sensor.TYPE_RELATIVE_HUMIDITY):
			return "RELATIVE_HUMIDITY ";
		case (Sensor.TYPE_ROTATION_VECTOR):
			return "ROTATION_VECTOR";
		case (Sensor.TYPE_TEMPERATURE):
			return "TEMPERATURE";
		}

		return "UNKNOWN";
	}

    private OnClickListener calibrationToggle = new OnClickListener() {
		@Override
        public void onClick(View v) {
			linearAccelCalibration.toggleCalibration();
           } 
    };
    
    private OnClickListener calibrationReset = new OnClickListener() {
		@Override
        public void onClick(View v) {
			linearAccelCalibration.reset();
           } 
    };
    
    private OnClickListener calibrationLogListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			float[] offsets = linearAccelCalibration.getOffsets();
			for (int i = 0; i < offsets.length; i++)
				Log.d("Calibration", "Offset "+ i +": "+ offsets[i]);
           } 
    };
    
    private OnClickListener trackingListener = new OnClickListener() {
		@Override
        public void onClick(View v) {
			if(positionEstimator.running.get() == false)
				new Thread(positionEstimator).start();
			else
				positionEstimator.running.set(false);			
           } 
    };
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sensor_test, menu);
		return true;
	}
}
