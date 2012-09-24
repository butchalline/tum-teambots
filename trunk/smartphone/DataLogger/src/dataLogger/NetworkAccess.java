package dataLogger;

import java.util.concurrent.atomic.AtomicBoolean;

import teambotData.Data;
import Communication.*;

public class NetworkAccess extends Ice.Application implements IMemoryAccess {

	public AtomicBoolean running = new AtomicBoolean(false);
	protected DataInterfacePrx networkDataProxy;
	
	@Override
	public void save(Data data) {
		while(!running.get())
		{
			//cause I can
		}
		
        Ice.AsyncResult r = networkDataProxy.begin_sendData(new Communication.Data(data.dataAsByteArray(), data.getTimestamp()));
        r.waitForSent();
        r.waitForCompleted();
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
		        
        Ice.ObjectPrx prx = communicator().stringToProxy("DataLogger:tcp -h 192.168.0.107 -p 10000").ice_oneway();
        networkDataProxy = Communication.DataInterfacePrxHelper.uncheckedCast(prx);
        running.set(true);
        communicator().waitForShutdown();
        return 0;
	}

	@Override
	public void stop() {
		communicator().shutdown();	
	}

}
