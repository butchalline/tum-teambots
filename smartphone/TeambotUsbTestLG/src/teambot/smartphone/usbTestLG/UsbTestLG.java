package teambot.smartphone.usbTestLG;

import teambot.smartphone.Bot;
import teambot.smartphone.helper.Constants;
import teambot.ssmartphone.usbTestLG.R;
import teambotData.ByteArrayData;
import teambotData.DataType;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import dataLogger.NetworkAccess;

public class UsbTestLG extends Activity implements SensorEventListener {

	private static final String TAG = "UsbTestLG";

    private Sensor mAccelerometer;
	
    private float mSensorX;
    private float mSensorY;
    private long mSensorTimeStamp;
    private ToggleButton toggleSendPackets;
    private ToggleButton toggleLeftRight;
    private TextView textView_speedLeft;
    private TextView textView_speedRight;
    private TextView textView_direction;
    private Button button_forceReset;
    private NetworkAccess remoteSmartphone;
    
    private boolean mStartSensorReading;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bot.setId(2);
        
        mStartSensorReading = false;
        
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();  
		setContentView(R.layout.activity_usb_test_lg);
        
        toggleSendPackets = ((ToggleButton)findViewById(R.id.toggleButton_packets));
        toggleLeftRight = ((ToggleButton)findViewById(R.id.ToggleButton_leftRightSwitch));
        textView_speedLeft = ((TextView)findViewById(R.id.textView_speedLeft));
        textView_speedRight = ((TextView)findViewById(R.id.textView_speedRight));
        textView_direction = ((TextView)findViewById(R.id.textView_direction));
        
        button_forceReset = ((Button)findViewById(R.id.button_usbReconnect));
        button_forceReset.setOnClickListener(buttonForceReset);
        
        ((Button)findViewById(R.id.button_startSending)).setOnClickListener(buttonStartSending);
        
		SensorManager sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
    }

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	public void onSensorChanged(SensorEvent event) {        	
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        
        long newTimeStamp = (long) (event.timestamp * Constants.NanoSecsToMSecs / 10);
        
        if(!mStartSensorReading || newTimeStamp < mSensorTimeStamp + 30) {
        	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return;
        }
        
        mSensorTimeStamp = newTimeStamp;
        mSensorX = -event.values[1];
        mSensorY = event.values[0];
        
        byte[] buffer = new byte[6];
        
        buffer[0] = 1;
        buffer[2] = ((byte)(mSensorTimeStamp & 0xFF00 >> 8));
        buffer[3] = ((byte)(mSensorTimeStamp & 0x00FF));
        
        if(toggleLeftRight.isChecked())
        	mSensorX *= -1;
        
        byte[] velocityInfo = calculateVelocityInfo(mSensorY, mSensorX);
        
        buffer[1] = velocityInfo[0];
        buffer[4] = velocityInfo[1];
        buffer[5] = velocityInfo[2];        
        
		if (toggleSendPackets.isChecked() && remoteSmartphone.running.get()) {
				remoteSmartphone.save(new ByteArrayData(0, buffer, DataType.ACCELEROMETER));
		}
    }
	
	enum Direction {
		FORWARD,
		BACKWARDS,
		TURN_LEFT,
		TURN_RIGHT
	}

	private byte[] calculateVelocityInfo(float forward, float sidewards) {

		byte velocityInfo[] = { 0, 0, 0 };
//		float ratio = calcLeftRightRatio(Math.abs(roll));
		boolean turnLeft = (sidewards >= 0) ? true : false;
		
		forward *= -1;		
		float velocity = forward + sidewards;
		float secondVelocity = forward - sidewards;
		float leftVelocity = 0;
		float rightVelocity = 0;
		
		boolean sameSign = hasSameSign(velocity, secondVelocity);
		
		//go forward
		if(forward >= 0 && sameSign) {
			velocityInfo[0] = (byte)Direction.FORWARD.ordinal();
			if(turnLeft) {
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
		// go backwards
		if(forward < 0 && sameSign) {
			velocityInfo[0] = (byte)Direction.BACKWARDS.ordinal();
			if(turnLeft) {
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
		//turn forward
		if(forward >= 0 && !sameSign) {			
			if(turnLeft) {
				velocityInfo[0] = (byte)Direction.TURN_LEFT.ordinal();
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				velocityInfo[0] = (byte)Direction.TURN_RIGHT.ordinal();
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
		//turn backwards		
		if(forward < 0 && !sameSign) {
			if(turnLeft) {
				 //yes it has to be turn right, although we turn left.. kind of
				velocityInfo[0] = (byte)Direction.TURN_RIGHT.ordinal();
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				 //yes it has to be turn left, although we turn right.. kind of
				velocityInfo[0] = (byte)Direction.TURN_LEFT.ordinal();
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
        textView_speedLeft.setText(Float.toString(leftVelocity));
        textView_speedRight.setText(Float.toString(rightVelocity));
        
        switch (velocityInfo[0]) {
        case 0:
        	textView_direction.setText(Direction.FORWARD.toString());
        	break;
        case 1:
        	textView_direction.setText(Direction.BACKWARDS.toString());
        	break;
        case 2:
        	textView_direction.setText(Direction.TURN_LEFT.toString());
        	break;
        case 3:
        	textView_direction.setText(Direction.TURN_RIGHT.toString());
        	break;
        }        
		
		velocityInfo[1] = sensorValueTobyteValue(Math.abs(leftVelocity));
		velocityInfo[2] = sensorValueTobyteValue(Math.abs(rightVelocity));

		return velocityInfo;
	}
	
	private boolean hasSameSign(float value1, float value2) {
		if(value1 < 0 && value2 < 0)
			return true;
		if(value1 >= 0 && value2 >= 0)
			return true;
		return false;
	}

	private float calcLeftRightRatio(float roll) {
		if (roll > 9.83f)
			roll = 9.83f;

		// flipped sigmoid function (looks like a S) mapped to x(center) = 0.5*g + scaled to [1,-1]
		float exponentComponent = (float) Math.exp(4.91f - roll);
		return 1 - (2 / (exponentComponent + 1));
	}

	private byte sensorValueTobyteValue(float sensorValue) {
		return (byte) (Math.abs(sensorValue) * 20);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
    private OnClickListener buttonForceReset = new OnClickListener() {
		@Override
        public void onClick(View v) {
		}
    };

    
    private OnClickListener buttonStartSending = new OnClickListener() {
    	
		@Override
        public void onClick(View v) {
			
			if(remoteSmartphone != null && remoteSmartphone.running.get())
				remoteSmartphone.stop();
			
	        String ipAddress = ((EditText)findViewById(R.id.editText_IpAddress)).getText().toString();
	        String port = ((EditText)findViewById(R.id.editText_Port)).getText().toString();
	        Log.d(TAG, "ip / port set to: "+ ipAddress +":"+ port);
	        remoteSmartphone = new NetworkAccess(ipAddress, port, "InterfaceBot1");
	        new Thread(remoteSmartphone).start();
			mStartSensorReading = true;
		}
    };

//	@Override
//	public void onStatusChange(Status status) {
//		((TextView)findViewById(R.id.textView_statusView)).setText(status.toString());
//	}
}
