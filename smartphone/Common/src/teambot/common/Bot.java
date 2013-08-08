package teambot.common;

import java.util.HashMap;
import java.util.Map;

import teambot.DisplayInformation;
import teambot.common.data.Pose;
import teambot.common.interfaces.ICyclicCallback;
import teambot.common.interfaces.IInformationDisplayer;
import Ice.Current;

public class Bot extends _ITeambotDisp implements ICyclicCallback
{
	private static final long serialVersionUID = 1L;
	static protected String _botId = null;
	static protected Pose _pose = null;
	static protected NetworkHub _networkHub = null;
	static protected Map<String, ITeambotPrx> _registeredBots = new HashMap<String, ITeambotPrx>(30);

	static protected BotNetworkLookUp _lookUp;
	static protected CyclicCaller _displayUpdater;

	protected IInformationDisplayer _display;

	public Bot(String ip, IInformationDisplayer display)
	{
		_botId = ip;
		_networkHub = new NetworkHub(Settings.debugIceConnections);
		_networkHub.start();
		_networkHub.addLocalTcpProxy(this, botProxyName(), Settings.registerPort);
		_lookUp = new BotNetworkLookUp();
		_display = display;
	}

	public void run()
	{
		setup();

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

	protected void setup()
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
		return _pose;
	}

	static public synchronized boolean isRegistered(String botId)
	{
		if (_registeredBots.containsKey(botId))
			return true;

		return false;
	}

	static public synchronized void registerBot(String botId, ITeambotPrx proxy)
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

	@Override
	public void callback_cyclic(int callbackIntervalInfo_ms)
	{
		if (_display == null)
			return;

		_display.display(new DisplayInformation());
		// TODO
	}
}
