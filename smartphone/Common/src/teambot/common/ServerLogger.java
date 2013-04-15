package teambot.common;

import teambot.common.interfaces.ILogger;
import teambot.common.usb.data.IceDataMapper;
import Communication.DataInterfacePrx;
import Ice.Application;

public class ServerLogger extends Application implements ILogger {
	
	private String ip = "127.0.0.1";
	private String port = "8000";
	private String proxyName = "DataLogger";
	private boolean verbose = false;
	private Ice.Communicator communicator;
	
	protected DataInterfacePrx networkDataProxy;
	
	public ServerLogger(String ip, String port, String proxyName) {
		this.ip = ip;
		this.port = port;
		this.proxyName = proxyName;
		new Thread(this).start();
	}
	
	public ServerLogger(String ip, String port, String proxyName, boolean verbose) {
		this.ip = ip;
		this.port = port;
		this.proxyName = proxyName;
		this.verbose = verbose;
		new Thread(this).start();
	}
	
	public void run() {
		String args[] = { "" };
		main("", args);
	}
	
	@Override
	public int run(String[] args) { 
		        
		Ice.StringSeqHolder argsH = new Ice.StringSeqHolder(args);
		Ice.Properties properties = Ice.Util.createProperties(argsH);
		if(this.verbose) {
			properties.setProperty("Ice.Warn.Connections", "2");
			properties.setProperty("Ice.Trace.Protocol", "2");
		}		
		Ice.InitializationData initializationData = new Ice.InitializationData();
		initializationData.properties = properties;
		communicator = Ice.Util.initialize(initializationData);
		
        Ice.ObjectPrx prx = communicator.stringToProxy(proxyName +":tcp -h "+ ip + " -p "+ port);//.ice_datagram();
        networkDataProxy = Communication.DataInterfacePrxHelper.uncheckedCast(prx);
        communicator.waitForShutdown();
        return 0;
	}
	
	public void stop() {
		communicator.shutdown();	
	}

	@Override
	public void save(teambot.common.usb.data.ByteArrayData data) {
		while(networkDataProxy == null)
		{//wait for networkDataProxy initialization
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		networkDataProxy.sendByteData(IceDataMapper.map(data));
	}

	@Override
	public void save(teambot.common.usb.data.FloatArrayData data) {
		while(networkDataProxy == null)
		{//wait for networkDataProxy initialization
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		networkDataProxy.sendFloatData(IceDataMapper.map(data));	
	}
}
