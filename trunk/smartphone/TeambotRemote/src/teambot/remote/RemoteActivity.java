package teambot.remote;

import java.util.ArrayList;
import java.util.List;

import teambot.DisplayInformation;
import teambot.common.interfaces.IInformationDisplayer;
import teambot.remote.VelocitySupplier.Direction;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RemoteActivity extends Activity implements IVelocityListener, IPitchRollListener, IInformationDisplayer
{

	protected TextView _textView_status;
	protected TextView _textView_speedRight;
	protected TextView _textView_speedLeft;
	protected TextView _textView_pitch;
	protected TextView _textView_roll;

	protected Switch _switch_onOff;
	protected Button _button_stop;
	
	protected Spinner _spinner_botSelection;
	protected ArrayAdapter<String> _spinnerAdapter;
	private List<String> _spinnerEntries = new ArrayList<String>();

	protected boolean _on = false;
	protected static final int waitTimeAfterStop_ms = 3000;
	protected long _timestampStop;
	
	protected PitchRollSupplier _pitchRollSupplier;
	protected VelocitySupplier _velocitySupplier;
	protected Sensor _accelerometer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote);


		_textView_status = ((TextView) findViewById(R.id.textView_status));
		_textView_speedRight = ((TextView) findViewById(R.id.textView_speedRight));
		_textView_speedLeft = ((TextView) findViewById(R.id.textView_speedLeft));
		_textView_pitch = ((TextView) findViewById(R.id.TextView_pitch));
		_textView_roll = ((TextView) findViewById(R.id.TextView_roll));

		_switch_onOff = (Switch) findViewById(R.id.switch_onOff);
		_switch_onOff.setOnCheckedChangeListener(_listener_onOffSwitch);
		
		_button_stop = (Button) findViewById(R.id.button_stop);
		_button_stop.setOnClickListener(_listener_stopButton);
		
		_spinner_botSelection = ((Spinner) findViewById(R.id.spinner_botSelection));
		_spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _spinnerEntries);
		_spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinner_botSelection.setAdapter(_spinnerAdapter);
		
		_pitchRollSupplier = new PitchRollSupplier();
		_velocitySupplier = new VelocitySupplier(this);
		_pitchRollSupplier.registerListener(this);
		_pitchRollSupplier.registerListener(_velocitySupplier);
		
		SensorManager sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
		_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(_pitchRollSupplier, _accelerometer, SensorManager.SENSOR_DELAY_GAME);		
	}

    private OnCheckedChangeListener _listener_onOffSwitch = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			_on = isChecked;
			
			if(!_on)
				; //TODO send zero velocity to bot
		}
    };
    
    private OnClickListener _listener_stopButton = new OnClickListener() {
		@Override
        public void onClick(View v) {
			
			_timestampStop = System.currentTimeMillis();
		}
    };
	
	@Override
	public void onPitchRollChange(float newPitch, float newRoll)
	{
		_textView_pitch.setText(Float.toString(newPitch));
		_textView_roll.setText(Float.toString(newRoll));

	}

	@Override
	public void onVelocityChange(Direction direction, byte rightSpeed, byte leftSpeed)
	{
		if(!_on)
			return;		
		
		if(System.currentTimeMillis() < _timestampStop + waitTimeAfterStop_ms)
		{
			direction = Direction.FORWARD;
			rightSpeed = 0;
			leftSpeed = 0;
		}
		
		_textView_speedRight.setText(Byte.toString(rightSpeed));
		_textView_speedLeft.setText(Byte.toString(leftSpeed));
		
		switch (direction)
		{
		case FORWARD:
			_textView_status.setText("Forward");
			break;
		case BACKWARDS:
			_textView_status.setText("backwards");
			break;
		case TURN_LEFT:
			_textView_status.setText("left");
			break;
		case TURN_RIGHT:
			_textView_status.setText("right");
			break;
		}
		//TODO send to Bot
	}

	@Override
	public void display(DisplayInformation info)
	{
		String[] idsOfKnownBots = info.idsOfKnownBots;

		for (String id : idsOfKnownBots)
		{
			if (_spinnerAdapter.getPosition(id) >= 0)
				continue;
			_spinnerAdapter.add(id);
		}
	}

}
