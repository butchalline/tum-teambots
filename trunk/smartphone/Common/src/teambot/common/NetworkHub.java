package teambot.common;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import Ice.Application;

public class NetworkHub extends Application implements Runnable {
	
	protected ConcurrentLinkedQueue<ITeambotPrx> remoteProxies = new ConcurrentLinkedQueue<ITeambotPrx>();
	protected ConcurrentLinkedQueue<Ice.ObjectAdapter> localProxies = new ConcurrentLinkedQueue<Ice.ObjectAdapter>();
	
	private boolean verbose = false;
	private Ice.Communicator communicator = null;
	Vector<Ice.ObjectAdapter> objectAdapters = new Vector<Ice.ObjectAdapter>();

	public NetworkHub(){
	}
	
	public NetworkHub(boolean verbose){
		this.verbose = verbose;
	}
	
	public synchronized void start() {
		
		new Thread(this).start();		
		
		while(communicator == null) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
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
		
        communicator.waitForShutdown();
        return 0;
	}
	
	public ITeambotPrx connectToRemoteUdpProxy(String proxyName, String ip, String port) {
		return connectToRemoteProxy(proxyName, ip, port, false);
	}
	
	public ITeambotPrx connectToRemoteTcpProxy(String proxyName, String ip, String port) {
		return connectToRemoteProxy(proxyName, ip, port, true);
	}
	
	public ITeambotPrx connectToRemoteProxy_Blocking(String proxyName, String ip, String port, boolean useTcp) {
		while(!Bot.isRegistered(ip)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return connectToRemoteProxy(proxyName, ip, port, useTcp);
	}
	
	protected synchronized ITeambotPrx connectToRemoteProxy(String proxyName, String ip, String port, boolean useTcp) {
		
		if(!Bot.isRegistered(ip))
			return null;
		
		ITeambotPrx proxy;
		Ice.ObjectPrx prx;
		
		if(useTcp) {
	        prx = communicator.stringToProxy(proxyName +":tcp -h "+ ip + " -p "+ port);
		}
		else {
	        prx = communicator.stringToProxy(proxyName +":udp -h "+ ip + " -p "+ port).ice_datagram();
		}
		
		proxy = ITeambotPrxHelper.uncheckedCast(prx);
		remoteProxies.add(proxy);
		
		return remoteProxies.peek();
	}
	
	public void addLocalUdpProxy(_ITeambotDisp localObject, String proxyName, String localPort) {
		addLocalProxy(localObject, proxyName, localPort, false);
	}
	
	public void addLocalTcpProxy(_ITeambotDisp localObject, String proxyName, String localPort) {
		addLocalProxy(localObject, proxyName, localPort, true);
	}
	
	protected synchronized void addLocalProxy(_ITeambotDisp localObject, String proxyName, String localPort, boolean useTcp) {
		
		Ice.ObjectAdapter objectAdapter;
		
		if(useTcp) {
			objectAdapter = communicator.createObjectAdapterWithEndpoints("Bot"+Bot.getId(), "tcp -p "+ localPort);
			objectAdapter.add(localObject, communicator.stringToIdentity(proxyName));
		}
		else {
			objectAdapter = communicator.createObjectAdapterWithEndpoints("Bot"+Bot.getId(), "udp -p "+ localPort);
			objectAdapter.add(localObject, communicator.stringToIdentity(proxyName)).ice_datagram();
		}
        
        objectAdapter.activate();
        objectAdapters.add(objectAdapter);
	}
}
