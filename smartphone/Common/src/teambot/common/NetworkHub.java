package teambot.common;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import Ice.Application;
import Ice.ConnectFailedException;
import Ice.NoEndpointException;
import Ice.SocketException;

public class NetworkHub extends Application implements Runnable
{
	protected Object _serverProxy = null;
	protected ConcurrentLinkedQueue<ITeambotPrx> _remoteBotProxies = new ConcurrentLinkedQueue<ITeambotPrx>();
	protected ConcurrentLinkedQueue<Ice.ObjectAdapter> _localProxies = new ConcurrentLinkedQueue<Ice.ObjectAdapter>();

	private boolean _verbose = false;
	private Ice.Communicator _communicator = null;
	Vector<Ice.ObjectAdapter> _objectAdapters = new Vector<Ice.ObjectAdapter>();

	public NetworkHub()
	{
	}

	public NetworkHub(boolean verbose)
	{
		this._verbose = verbose;
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
		properties.setProperty("Ice.RetryIntervals", "-1"); // -1 => don't
															// retry, which is
															// better for bot
															// discovery on the
															// network
		properties.setProperty("Ice.Default.CollocationOptimized", "0"); // always
																			// use
																			// network
																			// connections,
																			// even
																			// if
																			// the
																			// loopback
																			// interface
																			// is
																			// used

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

	public ITeambotPrx connectToRemoteUdpProxy(String proxyName, String ip, String port)
	{
		return connectToRemoteProxy(proxyName, ip, port, false);
	}

	public ITeambotPrx connectToRemoteTcpProxy(String proxyName, String ip, String port)
	{
		return connectToRemoteProxy(proxyName, ip, port, true);
	}

	public ITeambotPrx connectToRemoteProxy_Blocking(String proxyName, String ip, String port, boolean useTcp)
	{
		while (!Bot.isRegistered(ip))
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return connectToRemoteProxy(proxyName, ip, port, useTcp);
	}

	protected synchronized ITeambotPrx connectToRemoteProxy(String proxyName, String ip, String port, boolean useTcp)
	{

		if (!Bot.isRegistered(ip))
			return null;

		ITeambotPrx proxy;
		Ice.ObjectPrx prx;

		if (useTcp)
		{
			prx = _communicator.stringToProxy(proxyName + ":tcp -h " + ip + " -p " + port);
		} else
		{
			prx = _communicator.stringToProxy(proxyName + ":udp -h " + ip + " -p " + port);
		}

		proxy = ITeambotPrxHelper.uncheckedCast(prx);

		try
		{
			proxy.ice_getConnection();
		} catch (ConnectFailedException ex)
		{
			Bot.unregisterBot(ip);
			return null;
		} catch (NoEndpointException ex)
		{
			Bot.unregisterBot(ip);
			return null;
		}

		_remoteBotProxies.add(proxy);

		return _remoteBotProxies.peek();
	}

	public void addLocalUdpProxy(Ice.ObjectImpl localObject, String proxyName, String localPort)
	{
		addLocalProxy(localObject, proxyName, localPort, false);
	}

	public void addLocalTcpProxy(Ice.ObjectImpl localObject, String proxyName, String localPort)
	{
		addLocalProxy(localObject, proxyName, localPort, true);
	}

	protected synchronized void addLocalProxy(Ice.ObjectImpl localObject, String proxyName, String localPort,
			boolean useTcp)
	{

		Ice.ObjectAdapter objectAdapter = null;
		String adapterName = "Bot_" + Bot.id();
		String endpoint = "";

		if (useTcp)
		{
			endpoint = "tcp -h " + Bot.id() + " -p " + localPort;
			objectAdapter = _communicator.createObjectAdapterWithEndpoints(adapterName, endpoint);
			objectAdapter.add(localObject, _communicator.stringToIdentity(proxyName));
		} else
		{
			endpoint = "udp -h " + Bot.id() + " -p " + localPort;
			objectAdapter = _communicator.createObjectAdapterWithEndpoints(adapterName, endpoint);
			objectAdapter.add(localObject, _communicator.stringToIdentity(proxyName));
		}

		objectAdapter.activate();
		System.out.println("Local endpoint published, adapter: " + objectAdapter.getName() + "; endpoint: " + endpoint);

		_objectAdapters.add(objectAdapter);
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
}
