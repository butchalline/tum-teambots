package teambot.common.communication;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import teambot.common.Bot;
import teambot.common.ITeambotPrx;
import teambot.common.ITeambotPrxHelper;
import teambot.common.Settings;
import teambot.common.interfaces.IBotKeeper;
import Ice.Application;
import Ice.ConnectFailedException;
import Ice.NoEndpointException;
import Ice.SocketException;

public class NetworkHub extends Application implements Runnable
{
	protected Object _serverProxy = null;
	protected ConcurrentLinkedQueue<Ice.ObjectPrxHelperBase> _remoteBotProxies = new ConcurrentLinkedQueue<Ice.ObjectPrxHelperBase>();
	protected ConcurrentLinkedQueue<Ice.ObjectAdapter> _localProxies = new ConcurrentLinkedQueue<Ice.ObjectAdapter>();
	
	private boolean _verbose = false;
	private Ice.Communicator _communicator = null;
	Vector<Ice.ObjectAdapter> _objectAdapters = new Vector<Ice.ObjectAdapter>();

	protected IBotKeeper _bot;

	public NetworkHub(IBotKeeper bot)
	{
		_bot = bot;
	}

	public NetworkHub(IBotKeeper bot, boolean verbose)
	{
		_bot = bot;
		_verbose = verbose;
	}

	public synchronized void start()
	{
		new Thread(this).start();

		while (_communicator == null)
		{
			try
			{
				Thread.sleep(300);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void stop()
	{
		_communicator.shutdown();
	}

	@Override
	public void run()
	{
		String args[] =
		{ "" };
		main("", args);
	}

	@Override
	public int run(String[] args)
	{

		Ice.StringSeqHolder argsH = new Ice.StringSeqHolder(args);
		Ice.Properties properties = Ice.Util.createProperties(argsH);

		// -1 => don't retry, which is better for bot discovery in the network
		properties.setProperty("Ice.RetryIntervals", "-1");

		// always use network connections,
		// even if the loopback interface is used
		properties.setProperty("Ice.Default.CollocationOptimized", "0");

		if (this._verbose)
		{
			properties.setProperty("Ice.Warn.Connections", "2");
			properties.setProperty("Ice.Trace.Protocol", "2");
			properties.setProperty("Ice.Trace.Network", "3");
			properties.setProperty("Ice.Trace.Locator", "3");
		}

		Ice.InitializationData initializationData = new Ice.InitializationData();
		initializationData.properties = properties;
		_communicator = Ice.Util.initialize(initializationData);

		_communicator.waitForShutdown();
		return 0;
	}

	public <T extends Ice.ObjectPrxHelperBase> void connectToRemoteUdpProxy(String proxyName, String ip, String port, T proxyHelper)
	{
		connectToRemoteProxy(proxyName, ip, port, false, proxyHelper);
	}

	public <T extends Ice.ObjectPrxHelperBase> void connectToRemoteTcpProxy(String proxyName, String ip, String port, T proxyHelper)
	{
		connectToRemoteProxy(proxyName, ip, port, true, proxyHelper);
	}

	public <T extends Ice.ObjectPrxHelperBase> void connectToRemoteProxy_Blocking(String proxyName, String ip,
			String port, boolean useTcp, T proxyHelper)
	{
		while (!_bot.isRegistered(ip))
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		connectToRemoteProxy(proxyName, ip, port, useTcp, proxyHelper);
	}

	@SuppressWarnings("unchecked")
	protected synchronized <T extends Ice.ObjectPrxHelperBase> void connectToRemoteProxy(String proxyName, String ip,
			String port, boolean useTcp, T proxyHelper)
	{

		// TODO registered or simulator or server
		// if (!_bot.isRegistered(ip))
		// return null;

		Ice.ObjectPrx proxy;

		if (useTcp)
		{
			proxy = _communicator.stringToProxy(proxyName + ":tcp -h " + ip + " -p " + port);
		} else
		{
			proxy = _communicator.stringToProxy(proxyName + ":udp -h " + ip + " -p " + port);
		}

		try
		{
			proxy.ice_getConnection();
		} catch (ConnectFailedException ex)
		{
			Bot.unregisterBot(ip);
		} catch (NoEndpointException ex)
		{
			Bot.unregisterBot(ip);
		}
		
		proxyHelper.__copyFrom(proxy);

		_remoteBotProxies.add(proxyHelper);
	}

	public <T extends Ice.ObjectImpl> Ice.ObjectAdapter addLocalUdpProxy(T localObject, String proxyName, String localPort)
	{
		return addLocalProxy(localObject, proxyName, localPort, false);
	}

	public <T extends Ice.ObjectImpl> Ice.ObjectAdapter addLocalTcpProxy(T localObject, String proxyName, String localPort)
	{
		return addLocalProxy(localObject, proxyName, localPort, true);
	}

	protected synchronized <T extends Ice.ObjectImpl> Ice.ObjectAdapter addLocalProxy(T localObject, String adapterName, String localPort,
			boolean useTcp)
	{

		Ice.ObjectAdapter objectAdapter = null;
		String endpoint = "";

		if (useTcp)
		{
			endpoint = "tcp -h " + Bot.id() + " -p " + localPort;
			objectAdapter = _communicator.createObjectAdapterWithEndpoints(adapterName, endpoint);
			objectAdapter.add(localObject, getIdentity());
		} else
		{
			endpoint = "udp -h " + Bot.id() + " -p " + localPort;
			objectAdapter = _communicator.createObjectAdapterWithEndpoints(adapterName, endpoint);
			objectAdapter.add(localObject, getIdentity());
		}

		objectAdapter.activate();
		System.out.println("Local endpoint published, adapter: " + objectAdapter.getName() + "; endpoint: " + endpoint);

		_objectAdapters.add(objectAdapter);
		return objectAdapter;
	}

	public ITeambotPrx connectionPossible(String ip)
	{
		ITeambotPrx proxy = null;
		Ice.ObjectPrx prx = null;

		prx = _communicator.stringToProxy(Bot.idToProxyName(ip) + ":tcp -h " + ip + " -p " + Settings.registerPort
				+ " -t " + Settings.timoutOnSingleBotLookUp_ms);

		// System.out.println("Tried to connect to bot: " +
		// Bot.idToProxyName(ip) + ":tcp -h " + ip + " -p "
		// + Settings.registerPort);

		proxy = ITeambotPrxHelper.uncheckedCast(prx);

		try
		{
			proxy.ice_getConnection();
		} catch (ConnectFailedException ex)
		{
			return null;
		} catch (NoEndpointException ex)
		{
			return null;
		} catch (SocketException ex)
		{
			return null;
		} catch (Ice.TimeoutException ex)
		{
			return null;
		}

		return proxy;
	}
	
	public Ice.Identity getIdentity()
	{
		return _communicator.stringToIdentity("Bot_" + Bot.id());
	}
}
