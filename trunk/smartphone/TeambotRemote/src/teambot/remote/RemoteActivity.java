package teambot.remote;

import java.util.ArrayList;
import java.util.List;

import teambot.DisplayInformation;
import teambot.common.Bot;
import teambot.common.BotNetworkLookUp;
import teambot.common.ITeambotPrx;
import teambot.common.NetworkHub;
import teambot.common.interfaces.IBotKeeper;
import teambot.common.interfaces.IInformationDisplayer;
import teambot.remote.VelocityToPacketValues.Direction;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RemoteActivity extends Activity implements IVelocityListener, IPitchRollListener, IInformationDisplayer,
		IBotKeeper
{

	protected TextView _textView_status;
	protected TextView _textView_direction;
	protected TextView _textView_velocityLeft;
	protected TextView _textView_velocityRight;
	protected TextView _textView_pitch;
	protected TextView _textView_roll;

	protected Switch _switch_onOff;
	protected Button _button_stop;
	protected CheckBox _checkBox_disableRotation;
	protected CheckBox _checkBox_onlyRotation;

	protected Spinner _spinner_botSelection;
	protected ArrayAdapter<String> _spinnerAdapter;
	private List<String> _spinnerEntries = new ArrayList<String>();

	protected boolean _on = false;
	protected static final int waitTimeAfterStop_ms = 3000;
	protected long _timestampStop;

	protected PitchRollSupplier _pitchRollSupplier;
	protected VelocitySupplier _velocitySupplier;
	protected Sensor _accelerometer;

	protected NetworkHub _networkHub;
	protected BotNetworkLookUp _botlookUp;

	protected String _botId = null;
	protected ITeambotPrx _bot = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote);

		_textView_status = ((TextView) findViewById(R.id.textView_status));
		_textView_direction = ((TextView) findViewById(R.id.textView_direction));
		_textView_velocityLeft = ((TextView) findViewById(R.id.textView_velocityLeft));
		_textView_velocityRight = ((TextView) findViewById(R.id.textView_velocityRight));
		_textView_pitch = ((TextView) findViewById(R.id.TextView_pitch));
		_textView_roll = ((TextView) findViewById(R.id.TextView_roll));

		_switch_onOff = (Switch) findViewById(R.id.switch_onOff);
		_switch_onOff.setOnCheckedChangeListener(_listener_onOffSwitch);

		_button_stop = (Button) findViewById(R.id.button_stop);
		_button_stop.setOnClickListener(_listener_stopButton);

		_checkBox_disableRotation = (CheckBox) findViewById(R.id.checkBox_disableRotation);
		_checkBox_onlyRotation = (CheckBox) findViewById(R.id.checkBox_onlyRotation);

		_spinner_botSelection = ((Spinner) findViewById(R.id.spinner_botSelection));
		_spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _spinnerEntries);
		_spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinner_botSelection.setAdapter(_spinnerAdapter);
		_spinner_botSelection.setOnItemSelectedListener(_listener_botSelection);

		_pitchRollSupplier = new PitchRollSupplier();
		_velocitySupplier = new VelocitySupplier(this);
		_pitchRollSupplier.registerListener(this);
		_pitchRollSupplier.registerListener(_velocitySupplier);

		SensorManager sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
		_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(_pitchRollSupplier, _accelerometer, SensorManager.SENSOR_DELAY_GAME);

		_networkHub = new NetworkHub(this);
		_botlookUp = new BotNetworkLookUp(_networkHub, this);
		// _botlookUp.
	}

	private OnCheckedChangeListener _listener_onOffSwitch = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			_on = isChecked;

			if (!_on)
				_button_stop.performClick();
		}
	};

	private OnClickListener _listener_stopButton = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			_timestampStop = System.currentTimeMillis();
		}
	};

	private OnItemSelectedListener _listener_botSelection = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			System.out.println("Bot selected: " + position);
			_botId = _spinnerAdapter.getItem(position);
			_bot = _networkHub.connectToRemoteUdpProxy(Bot.idToProxyName(_botId), _botId,
					teambot.common.Settings.remoteControlPort);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0)
		{

		}
	};

	@Override
	public void onPitchRollChange(float newPitch, float newRoll)
	{
		_textView_pitch.setText(Float.toString(newPitch));
		_textView_roll.setText(Float.toString(newRoll));
	}

	@Override
	public void onVelocityChange(float leftVelocity, float rightVelocity)
	{
		if (System.currentTimeMillis() < _timestampStop + waitTimeAfterStop_ms)
		{
			leftVelocity = 0;
			rightVelocity = 0;
		} else if (!_on)
			return;

		if (_checkBox_disableRotation.isChecked())
		{
			leftVelocity = (rightVelocity + leftVelocity) / 2;
			rightVelocity = leftVelocity;
		}

		byte[] packet = VelocityToPacketValues.convert(leftVelocity, rightVelocity);

		if (_checkBox_onlyRotation.isChecked())
		{
			if (packet[0] == Direction.FORWARD.ordinal() || packet[0] == Direction.BACKWARDS.ordinal())
			{
				leftVelocity = 0;
				rightVelocity = 0;
				packet[0] = (byte) Direction.FORWARD.ordinal();
				packet[1] = 0;
				packet[2] = 0;
			} else
			{
				if (packet[0] == Direction.TURN_LEFT.ordinal())
					leftVelocity = -rightVelocity;
				else
					rightVelocity = -leftVelocity;
			}
		}

		if (packet[0] == Direction.FORWARD.ordinal())
			_textView_direction.setText("forward");
		else if (packet[0] == Direction.BACKWARDS.ordinal())
			_textView_direction.setText("backwards");
		else if (packet[0] == Direction.TURN_LEFT.ordinal())
			_textView_direction.setText("left");
		else if (packet[0] == Direction.TURN_RIGHT.ordinal())
			_textView_direction.setText("right");

		_textView_velocityLeft.setText(Float.toString(leftVelocity));
		_textView_velocityRight.setText(Float.toString(rightVelocity));

		if(_bot != null)
			_bot.setVelocity(packet);
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

	@Override
	public synchronized boolean isRegistered(String botId)
	{
		int position = _spinnerAdapter.getPosition(botId);
		if (position < 0)
			return false;
		return true;
	}

	@Override
	public synchronized void registerBot(String botId, ITeambotPrx proxy)
	{
		_spinnerAdapter.add(botId);
	}

}
