package dataLogger;

import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import teambotData.ByteArrayData;
import teambotData.FloatArrayData;
import Communication.*;

public class NetworkAccess extends Ice.Application implements IMemoryAccess {
	
	public AtomicBoolean running = new AtomicBoolean(false);
	
	private String hostname = "localhost";
	private String port = "10000";
	private String proxyName = "DataLogger";	
	private Ice.Communicator communicator;
	
	protected DataInterfacePrx networkDataProxy;
	
	public NetworkAccess()
	{
		running = new AtomicBoolean(false);
	}
	
	public NetworkAccess(String hostnameOrIp, String port, String proxyName)
	{
		running = new AtomicBoolean(false);
		this.hostname = hostnameOrIp;
		this.port = port;
		this.proxyName = proxyName;
	}
	
	@Override
	public void save(ByteArrayData data) {
		while(networkDataProxy == null) 
		{//wait for networkDataProxy initialization
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
        networkDataProxy.sendByteData(IceDataMapper.map(data));
	}
	
	@Override
	public void save(FloatArrayData data) {
		while(networkDataProxy == null)
		{//wait for networkDataProxy initialization
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
        networkDataProxy.sendFloatData(IceDataMapper.map(data));
	}

	@Override
	public void run() {        
		if(running.get())
			return;
	    String args[] = { "" };
	    main("CommunicationServer", args);
	}

	@Override
	public int run(String[] args) {       
			
		//TODO get host and port from config file 
//		Properties configFile = new Properties();
		
//		try {
//			configFile.load(this.getClass().getClassLoader().getResourceAsStream("/IceServer.properties"));
//			String host = configFile.getProperty("host");
//			String port = configFile.getProperty("port");
//			prx = communicator.stringToProxy("DataLogger:tcp -h "+ host +" -p " + port).ice_twoway();
//			
//		} catch (IOException e) {
//			prx = communicator.stringToProxy("DataLogger:tcp -h localhost -p 10000").ice_twoway();
//			
//			e.printStackTrace();
//		}        
		        
		Ice.StringSeqHolder argsH = new Ice.StringSeqHolder(args);
		Ice.Properties properties = Ice.Util.createProperties(argsH);
//		properties.setProperty("Ice.Warn.Connections", "2");
//		properties.setProperty("Ice.Trace.Protocol", "2");
		Ice.InitializationData id = new Ice.InitializationData();
		id.properties = properties;
		communicator = Ice.Util.initialize(id);
		running.set(true);
		
		String proxyString = proxyName +":tcp -h "+ hostname + " -p "+ port;
        Ice.ObjectPrx prx = communicator.stringToProxy(proxyString);//.ice_datagram();
        Log.d("NetworkAccess", proxyString);
        networkDataProxy = Communication.DataInterfacePrxHelper.uncheckedCast(prx);//.uncheckedCast(prx);
        communicator.waitForShutdown();
        return 0;
	}

	@Override
	public void stop() {
		running.set(false);
		communicator.shutdown();	
	}

}
