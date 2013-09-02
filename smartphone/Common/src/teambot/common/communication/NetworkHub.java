package teambot.common.communication;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

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

	protected IBotKeeper _botKeeper;
	protected String _ownIp;

	public NetworkHub(IBotKeeper bot, String ownIp)
	{
		initialize(bot, ownIp, false);
	}

	public NetworkHub(IBotKeeper bot, String ownIp, boolean verbose)
	{
		initialize(bot, ownIp, verbose);
	}
	
	protected void initialize(IBotKeeper bot, String ownIp, boolean verbose)
	{
		_botKeeper = bot;
		
		if(ownIp == "")
		{
			System.out.println("Seems like something is wrong with the ip -> turn WLAN on?");
		}
		_ownIp = ownIp;
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
		
		System.out.println("Network hub started");
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

	public <T extends Ice.ObjectPrxHelperBase> void connectToRemoteUdpProxy(String identity, String ip, String port, T proxyHelper)
	{
		connectToRemoteProxy(identity, ip, port, false, proxyHelper);
	}

	public <T extends Ice.ObjectPrxHelperBase> void connectToRemoteTcpProxy(String identity, String ip, String port, T proxyHelper)
	{
		connectToRemoteProxy(identity, ip, port, true, proxyHelper);
	}

	public <T extends Ice.ObjectPrxHelperBase> void connectToRemoteProxy_Blocking(String identity, String ip,
			String port, boolean useTcp, T proxyHelper)
	{
		while (!_botKeeper.isRegistered(ip))
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		connectToRemoteProxy(identity, ip, port, useTcp, proxyHelper);
	}

	protected synchronized <T extends Ice.ObjectPrxHelperBase> void connectToRemoteProxy(String identity, String ip,
			String port, boolean useTcp, T proxyHelper)
	{

		// TODO registered or simulator or server
		// if (!_bot.isRegistered(ip))
		// return null;

		Ice.ObjectPrx proxy;

		if (useTcp)
		{
			proxy = _communicator.stringToProxy(identity + ":tcp -h " + ip + " -p " + port);
		} else
		{
			proxy = _communicator.stringToProxy(identity + ":udp -h " + ip + " -p " + port);
		}

		try
		{
			proxy.ice_getConnection();
		} catch (ConnectFailedException ex)
		{
			_botKeeper.unregisterBot(ip);
		} catch (NoEndpointException ex)
		{
			_botKeeper.unregisterBot(ip);
		}
		
		proxyHelper.__copyFrom(proxy);

		_remoteBotProxies.add(proxyHelper);
	}

	public <T extends Ice.ObjectImpl> Ice.ObjectAdapter addLocalUdpProxy(T localObject, String adapterName, String localPort)
	{
		return addLocalProxy(localObject, adapterName, localPort, false);
	}

	public <T extends Ice.ObjectImpl> Ice.ObjectAdapter addLocalTcpProxy(T localObject, String adapterName, String localPort)
	{
		return addLocalProxy(localObject, adapterName, localPort, true);
	}

	protected synchronized <T extends Ice.ObjectImpl> Ice.ObjectAdapter addLocalProxy(T localObject, String adapterName, String localPort,
			boolean useTcp)
	{

		Ice.ObjectAdapter objectAdapter = null;
		String endpoint = "";
		
		if(_communicator == null)
		{
			System.out.println("Seems like the communicator is not initialized -> did you call start() on the NetworkHub object?");
			return null;
		}
		
		if (useTcp)
		{
			endpoint = "tcp -h " + _ownIp + " -p " + localPort;
			objectAdapter = _communicator.createObjectAdapterWithEndpoints(adapterName, endpoint);
			objectAdapter.add(localObject, getIdentity());
		} else
		{
			endpoint = "udp -h " + _ownIp + " -p " + localPort;
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

		
		if(_communicator == null)
		{
			System.out.println("Seems like the communicator is not initialized -> did you call start() on the NetworkHub object?");
			return null;
		}		
		
		prx = _communicator.stringToProxy(getBotIdentityString(ip) + ":tcp -h " + ip + " -p " + Settings.botPort
				+ " -t " + Settings.timoutOnSingleBotLookUp_ms);

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
	
	public String getBotIdentityString(String ip)
	{
		return "Bot_" + ip;
	}
	
	public Ice.Identity getIdentity()
	{
		return _communicator.stringToIdentity(getBotIdentityString(_ownIp));
	}
	
	public String getIp()
	{
		return _ownIp;
	}
}
