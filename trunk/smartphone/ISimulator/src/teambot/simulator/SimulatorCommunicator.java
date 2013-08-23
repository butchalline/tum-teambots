package teambot.simulator;

import teambot.common.Bot;
import teambot.communication.DebugGridPoint;
import teambot.communication.IDataServerPrx;
import teambot.communication.IDataServerPrxHelper;
import teambot.communication.TBFrame;
import teambot.communication._IDataClientDisp;
import Ice.Application;

public class SimulatorCommunicator extends Application implements Runnable
{

	protected boolean verbose = false;
	protected Ice.Communicator communicator = null;
	protected Ice.ObjectAdapter objectAdapter;
	protected String localPort = "91000";
	protected String remotePort = "91000";
	protected String simulatorIp = "";
	protected boolean useTcp = true;
	protected IDataServerPrx serverInterface;
	protected Ice.Identity ident;

	public SimulatorCommunicator(String localPort, String remotePort)
	{
		initialize(localPort, remotePort, true, true);
	}

	public SimulatorCommunicator(String localPort, String remotePort, boolean useTcp, boolean verbose)
	{
		initialize(localPort, remotePort, useTcp, verbose);
	}

	private void initialize(String localPort, String remotePort, boolean useTcp, boolean verbose)
	{
		this.localPort = localPort;
		this.remotePort = remotePort;
		this.useTcp = useTcp;
		this.verbose = verbose;
		ident = new Ice.Identity();
		ident.name = java.util.UUID.randomUUID().toString();
		ident.category = "";
	}

	public void start(_IDataClientDisp localDataInterface, String simulatorIp)
	{

		new Thread(this).start();

		while (communicator == null)
		{
			try
			{
				Thread.sleep(300);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		if (useTcp)
		{
			objectAdapter = communicator.createObjectAdapterWithEndpoints(Bot.id(), "tcp -h localhost -p " + localPort);
			objectAdapter.add(localDataInterface, ident);

		} else
		{
			objectAdapter = communicator.createObjectAdapterWithEndpoints(Bot.id(), "udp -h localhost -p " + localPort);
			objectAdapter.add(localDataInterface, ident);
		}

		objectAdapter.activate();
		this.simulatorIp = simulatorIp;
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
		if (this.verbose)
		{
			properties.setProperty("Ice.Warn.Connections", "2");
			properties.setProperty("Ice.Trace.Protocol", "2");
		}
		Ice.InitializationData initializationData = new Ice.InitializationData();
		initializationData.properties = properties;
		communicator = Ice.Util.initialize(initializationData);

		communicator.waitForShutdown();
		return 0;
	}

	public synchronized void sendFrame(TBFrame frame)
	{

		interfaceCheck();

		serverInterface.update(frame);
	}

	public synchronized void sendDebugMap(DebugGridPoint[] points, short cellSize)
	{

		interfaceCheck();

		serverInterface.debugMap(points, cellSize);
	}

	private void interfaceCheck()
	{
		if (serverInterface == null)
		{
			Ice.ObjectPrx prx;

			if (useTcp)
			{
				prx = communicator.stringToProxy("Simulator:tcp -h " + simulatorIp + " -p " + remotePort);
			} else
			{
				prx = communicator.stringToProxy("Simulator:udp -h " + simulatorIp + " -p " + remotePort);
			}

			serverInterface = IDataServerPrxHelper.uncheckedCast(prx);
			serverInterface.ice_getConnection().setAdapter(objectAdapter);
			serverInterface.addClient(ident);
		}

		while (serverInterface == null)
		{// wait for remoteInterface initialization
			try
			{
				Thread.sleep(300);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
