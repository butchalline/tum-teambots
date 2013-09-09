package teambot.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import teambot.ClassType;
import teambot.DisplayInformation;
import teambot.IInformationDisplayerPrx;
import teambot.IInformationDisplayerPrxHelper;
import teambot.common.communication.BotNetworkDiscovery;
import teambot.common.communication.NetworkHub;
import teambot.common.data.Direction;
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
import teambot.common.usb.UsbData;
import teambot.common.usb.UsbHeader;
import teambot.common.usb.UsbPacket;
import teambot.common.utils.Constants;
import teambot.common.utils.CyclicCaller;
import teambot.common.utils.WheelTransform;
import teambot.remote.IStreamReceiverPrx;
import teambot.remote.IStreamReceiverPrxHelper;
import Ice.Current;
import Ice.Identity;
import android.graphics.PointF;

public class Bot extends _ITeambotDisp implements ICyclicCallback, IBotKeeper, IPacketListener
{
	private static final long serialVersionUID = 1L;
	static public String _botId = null;
	static protected PoseSupplier _poseSupplier = new PoseSupplier();

	static protected NetworkHub _networkHub = null;
	static protected BotNetworkDiscovery _lookUp;
	static protected Map<String, ITeambotPrx> _registeredBots = new HashMap<String, ITeambotPrx>(30);

	static protected Vector<IStreamReceiverPrx> _streamReceivers = new Vector<IStreamReceiverPrx>(10);
	static protected Vector<IInformationDisplayerPrx> _displayer = new Vector<IInformationDisplayerPrx>(10);
	static protected IInformationDisplayer _localDisplayer;

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
			// _poseSupplier.registerForChangeUpdates(poseSupplier);
			put(BotSensors.DISTANCE, new DistanceSensor(poseSupplier));
		}
	};

	protected IUsbIO _usbIO;

	public Bot(String ip, IInformationDisplayer localDisplayer)
	{
		_botId = ip;
		_localDisplayer = localDisplayer;

		setupNetwork();
		startParticleFilter();
		
		// setupDisplayUpater();
	}

	protected void setupNetwork()
	{
		_networkHub = new NetworkHub(this, _botId, Settings.debugIceConnections);
		_networkHub.start();
		_networkHub.addLocalTcpProxy(this, botProxyName(), Settings.botPort);
		_lookUp = new BotNetworkDiscovery(_networkHub, this);
	}

	public void setupUsb(IUsbIO usbIO)
	{
		_usbIO = usbIO;
		_connectionParser = new UsbConnectionParser(_usbIO);
		_packetDistribution = new PacketDistribution(_connectionParser);
		_connectionParser.registerPacketListener(_packetDistribution);
		_connectionParser.start();
		System.out.println("USB connection parser started");
		_packetDistribution.start();
		System.out.println("USB packet distributer started");
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
		_particleFilter = new ParticleFilter(50, 1500, 0.5f, 0.8f, 0.2f, noiser, Settings.numberOfParticles, new Pose(
				new PointF(500f * 10f, Settings.mapOffsetY - 562.5f * 10f), 90 * Constants.DegreeToRadian));
		_sensors.get(BotSensors.DISTANCE).registerForSensorValues(_particleFilter);
		_poseSupplier.registerForChangeUpdates(_particleFilter);
		System.out.println("Particle filter ready");
	}

	public static ParticleFilter getFilter()
	{
		return _particleFilter;
	}

	protected void setupDisplayUpater()
	{
		_displayUpdater = new CyclicCaller(this, 1000 / Settings.displayInfoRefreshRate_hz);
		new Thread(_displayUpdater).start();
		System.out.println("Display updates started");
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

	static public PoseSupplier getPoseSupplier()
	{
		return _poseSupplier;
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

	public synchronized void unregisterBot(String botId)
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
		if(_localDisplayer != null)
			_localDisplayer.display(_displayInformation);
		
		for(IInformationDisplayerPrx infoDisplay : _displayer)
			infoDisplay.begin_infoCallback(_displayInformation);
	}

	@Override
	public void setVelocity(byte[] velocityPacket, Current __current)
	{
		if (_usbIO == null)
			return;

		UsbHeader header = null;
		byte[] data =
		{ velocityPacket[1], velocityPacket[2] };

		switch (Direction.values()[velocityPacket[0]])
		{
		case FORWARD:
			header = UsbHeader.TB_VELOCITY_FORWARD;
			break;
		case BACKWARDS:
			header = UsbHeader.TB_VELOCITY_BACKWARD;
			break;
		case TURN_LEFT:
			header = UsbHeader.TB_VELOCITY_TURN_LEFT;
			break;
		case TURN_RIGHT:
			header = UsbHeader.TB_VELOCITY_TURN_RIGHT;
			break;
		}

		UsbPacket packet = new UsbPacket(header, new UsbData(data));

		try
		{
			_usbIO.write(packet.asByteArray());
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
			int[] data = packet.getData().asIntArray();
			int changeLeft_steps = (data[0] << 8) | data[1];
			int changeRight_steps = (data[2] << 8) | data[3];

			float changeLeft_radian = WheelTransform.wheelStepsToRadian(changeLeft_steps,
					_displayInformation.leftWheelRefVelocity, true);
			float changeRight_radian = WheelTransform.wheelStepsToRadian(changeRight_steps,
					_displayInformation.rightWheelRefVelocity, false);

			_poseSupplier.poseChangeCallback(convertChange(changeLeft_radian, changeRight_radian), true);
			return;
		}
		if (header.compareTo(UsbHeader.TB_MOCK_POSITION_CHANGE) == 0)
		{
			int[] data = packet.getData().asIntArray();
			int changeX = (data[0] << 8) | data[1];
			int changeY = (data[2] << 8) | data[3];
			int angle_deziDeg = (data[4] << 8) | data[5];

			// if(changeX != 0)
			_poseSupplier.poseChangeCallback(
					new Pose(changeX, changeY, angle_deziDeg * 0.1f * Constants.DegreeToRadian), true);

			System.out.println("pos change in: " + changeX + " : " + changeY + " a: " + angle_deziDeg);
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

	@Override
	public void addClient(Identity ident, ClassType registerType, Current __current)
	{
		Ice.ObjectPrx proxy = __current.con.createProxy(ident);

		System.out.println("Client added: " + ident.name);
		System.out.println("Client added, proxy: " + proxy.ice_getConnection()._toString());
		
		switch (registerType)
		{
		case STREAMRECEIVER:
			synchronized (_streamReceivers)
			{
				_streamReceivers.add(IStreamReceiverPrxHelper.uncheckedCast(proxy));
			}
			break;

		case INFORMATIONDISPLAYER:
			synchronized (_displayer)
			{
				_displayer.add(IInformationDisplayerPrxHelper.uncheckedCast(proxy));
			}
			break;

		case SERVER:
			// TODO
			break;
		}
	}
	
	public static Vector<IStreamReceiverPrx> getStreamReceivers()
	{
		Vector<IStreamReceiverPrx> receiverCopy = null;
		synchronized (_streamReceivers)
		{
			receiverCopy = new Vector<IStreamReceiverPrx>(_streamReceivers);
		}		
		return receiverCopy;
	}
}
