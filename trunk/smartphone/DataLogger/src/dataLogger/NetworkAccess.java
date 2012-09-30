package dataLogger;

import teambotData.ByteArrayData;
import teambotData.FloatArrayData;
import Communication.*;

public class NetworkAccess extends Ice.Application implements IMemoryAccess {
	
	String hostname = "localhost";
	String port = "10000";
	
	protected DataInterfacePrx networkDataProxy;
	
	NetworkAccess()
	{
	}
	
	public NetworkAccess(String hostnameOrIp, String port)
	{
		this.hostname = hostnameOrIp;
		this.port = port;
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
//			prx = communicator().stringToProxy("DataLogger:tcp -h "+ host +" -p " + port).ice_twoway();
//			
//		} catch (IOException e) {
//			prx = communicator().stringToProxy("DataLogger:tcp -h localhost -p 10000").ice_twoway();
//			
//			e.printStackTrace();
//		}        
		        
        Ice.ObjectPrx prx = communicator().stringToProxy("DataLogger:udp -h "+ hostname + " -p "+ port).ice_datagram();
        networkDataProxy = Communication.DataInterfacePrxHelper.uncheckedCast(prx);
        communicator().waitForShutdown();
        return 0;
	}

	@Override
	public void stop() {
		communicator().shutdown();	
	}

}
