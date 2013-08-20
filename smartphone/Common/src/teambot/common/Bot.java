package teambot.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import teambot.DisplayInformation;
import teambot.common.communication.BotNetworkLookUp;
import teambot.common.communication.NetworkHub;
import teambot.common.data.Pose;
import teambot.common.hardware.BotSensor;
import teambot.common.hardware.BotSensors;
import teambot.common.hardware.DistanceSensor;
import teambot.common.interfaces.IBotKeeper;
import teambot.common.interfaces.ICyclicCallback;
import teambot.common.interfaces.IInformationDisplayer;
import teambot.common.interfaces.IPacketListener;
import teambot.common.interfaces.IUsbIO;
import teambot.common.slam.NoiseProvider;
import teambot.common.slam.ParticleFilter;
import teambot.common.usb.PacketDistribution;
import teambot.common.usb.UsbConnectionParser;
import teambot.common.usb.UsbHeader;
import teambot.common.usb.UsbPacket;
import teambot.common.utils.Constants;
import teambot.common.utils.CyclicCaller;
import teambot.common.utils.WheelTransform;
import Ice.Current;
import android.graphics.PointF;

public class Bot extends _ITeambotDisp implements ICyclicCallback, IBotKeeper, IPacketListener
{
	private static final long serialVersionUID = 1L;
	static protected String _botId = null;
	static protected PoseSupplier _poseSupplier = new PoseSupplier();

	static protected NetworkHub _networkHub = null;
	static protected BotNetworkLookUp _lookUp;
	static protected Map<String, ITeambotPrx> _registeredBots = new HashMap<String, ITeambotPrx>(30);

	static protected UsbConnectionParser _connectionParser;
	static protected PacketDistribution _packetDistribution;

	static protected ParticleFilter _particleFilter;

	static protected DisplayInformation _displayInformation;
	static protected CyclicCaller _displayUpdater;

	@SuppressWarnings("serial")
	static protected Map<BotSensors, BotSensor> _sensors = new HashMap<BotSensors, BotSensor>()
	{
		{
			PoseSupplier poseSupplier = new PoseSupplier(BotLayoutConstants.distanceSensorOffset_mm);
//			_poseSupplier.registerForChangeUpdates(poseSupplier);
			put(BotSensors.DISTANCE, new DistanceSensor(poseSupplier));
		}
	};

	protected IUsbIO _usbIO;
	protected IInformationDisplayer _display;

	public Bot(String ip, IInformationDisplayer display)
	{
		_botId = ip;

		setupNetwork();
		startParticleFilter();

		_display = display;
		// setupDisplayUpater();
	}

	protected void setupNetwork()
	{
		_networkHub = new NetworkHub(this, Settings.debugIceConnections);
		_networkHub.start();
		_networkHub.addLocalTcpProxy(this, botProxyName(), Settings.registerPort);
		// _lookUp = new BotNetworkLookUp(_networkHub, this);
	}

	public void setupUsb(IUsbIO usbIO)
	{
		_usbIO = usbIO;
		_connectionParser = new UsbConnectionParser(_usbIO);
		_packetDistribution = new PacketDistribution(_connectionParser);
		_connectionParser.registerPacketListener(_packetDistribution);
		_connectionParser.start();
		_packetDistribution.start();
		registerPacketListeners();
	}

	protected void registerPacketListeners()
	{
		_packetDistribution.register((DistanceSensor) _sensors.get(BotSensors.DISTANCE), UsbHeader.TB_DATA_INFRARED);
		_packetDistribution.register(this, UsbHeader.TB_DATA_WHEEL_CHANGES);
		_packetDistribution.register(this, UsbHeader.TB_MOCK_POSITION_CHANGE);
	}

	public void run()
	{
		setupDisplayUpater();

		while (true)
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// finish();
	}

	private void startParticleFilter()
	{
		NoiseProvider noiser = new NoiseProvider(0, 0, 0, 0.05f, 0.05f, 0.05f);
		_particleFilter = new ParticleFilter(50, 1500, 0.5f, 0.8f, 0.2f, noiser, 300, new Pose(new PointF(500f * 10f,
				Settings.mapOffsetY - 562.5f * 10f), 90 * Constants.DegreeToRadian));
		_sensors.get(BotSensors.DISTANCE).registerForSensorValues(_particleFilter);
		_poseSupplier.registerForChangeUpdates(_particleFilter);
	}

	protected void setupDisplayUpater()
	{
		_displayUpdater = new CyclicCaller(this, 1000 / Settings.displayInfoRefreshRate_hz);
		new Thread(_displayUpdater).start();
	}

	protected void finish()
	{
		_lookUp.stop();
		_displayUpdater.running.set(false);
	}

	public void finalize()
	{
		if (_lookUp != null)
			_lookUp.stop();
	}

	static public Pose getPose()
	{
		return _poseSupplier.getPose();
	}

	public synchronized boolean isRegistered(String botId)
	{
		if (_registeredBots.containsKey(botId))
			return true;

		return false;
	}

	public synchronized void registerBot(String botId, ITeambotPrx proxy)
	{
		_registeredBots.put(botId, proxy);
		System.out.println("New bot registered, id: " + botId);
	}

	static public synchronized void unregisterBot(String botId)
	{
		_registeredBots.remove(botId);
		System.out.println("Bot unregistered, id: " + botId);
	}

	@Override
	public String getIdRemote(Current __current)
	{
		return _botId;
	}

	public static String id()
	{
		return _botId;
	}

	public static String botProxyName()
	{
		return idToProxyName(_botId);
	}

	public static String idToProxyName(String id)
	{
		return id + "_register";
	}

	public static NetworkHub networkHub()
	{
		return _networkHub;
	}

	public static BotSensor getSensor(BotSensors sensor)
	{
		return _sensors.get(sensor);
	}

	public static ParticleFilter getParticleFilter()
	{
		return _particleFilter;
	}

	@Override
	public void callback_cyclic(int callbackIntervalInfo_ms)
	{
		if (_display == null)
			return;

		_display.display(new DisplayInformation());
		// TODO
	}

	@Override
	public void setVelocity(byte[] velocityPacket, Current __current)
	{
		if (_usbIO == null)
			return;

		try
		{
			_usbIO.write(velocityPacket);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void newPacketCallback(UsbPacket packet)
	{
		UsbHeader header = packet.getHeader();
		if (header.compareTo(UsbHeader.TB_DATA_WHEEL_CHANGES) == 0)
		{
			byte[] data = packet.getData().asByteArray();
			int changeLeft_steps = ((data[0] & 0xFF) << 8) | data[1];
			int changeRight_steps = ((data[2] & 0xFF) << 8) | data[3];

			float changeLeft_radian = WheelTransform.wheelStepsToRadian(changeLeft_steps,
					_displayInformation.leftWheelRefVelocity, true);
			float changeRight_radian = WheelTransform.wheelStepsToRadian(changeRight_steps,
					_displayInformation.rightWheelRefVelocity, false);

			_poseSupplier.poseChangeCallback(convertChange(changeLeft_radian, changeRight_radian));
			return;
		}
		if (header.compareTo(UsbHeader.TB_MOCK_POSITION_CHANGE) == 0)
		{
			byte[] data = packet.getData().asByteArray();
			int changeX = ((data[0] & 0xFF) << 8) | data[1];
			int changeY = ((data[2] & 0xFF) << 8) | data[3];
			int angle_deziDeg = ((data[4] & 0xFF) << 8) | data[5];
			
//			if(changeX != 0)
			_poseSupplier
					.poseChangeCallback(new Pose(changeX, changeY, angle_deziDeg * 0.1f * Constants.DegreeToRadian));
			
//				System.out.println("pos change out: " + changeX + " : " + changeY + " a: " + angle_centiDeg);
			return;
		}
	}

	/**
	 * d = (dR + dL) / 2 angle = (dR - dL) / wheel distance
	 * 
	 * 
	 * x = x + d * cos(angle) y = y + d * sin(angle) angle = angle + angle
	 */
	public static Pose convertChange(float changeLeft_radian, float changeRight_radian)
	{
		float changeLeft_mm = BotLayoutConstants.WheelRadius_mm * changeLeft_radian;
		float changeRight_mm = BotLayoutConstants.WheelRadius_mm * changeRight_radian;

		float d = (changeRight_mm + changeLeft_mm) * 0.5f;
		float angleChange = (changeRight_mm - changeLeft_mm) / BotLayoutConstants.DistanceBeweenWheelCenters_mm;

		return new Pose(d * (float) Math.cos(angleChange), d * (float) Math.sin(angleChange), angleChange);
	}
}
