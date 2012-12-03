package teambot.common;

import java.util.concurrent.ConcurrentLinkedQueue;

import Communication.DataInterfacePrx;
import Communication._DataInterfaceDisp;
import Ice.Application;

public class NetworkConnection extends Application implements Runnable {
	
	protected ConcurrentLinkedQueue<DataInterfacePrx> remoteProxies = new ConcurrentLinkedQueue<DataInterfacePrx>();
	protected ConcurrentLinkedQueue<Ice.ObjectAdapter> localProxies = new ConcurrentLinkedQueue<Ice.ObjectAdapter>();
	
	private boolean verbose = false;
	private Ice.Communicator communicator;

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
	
	public DataInterfacePrx connectToRemoteUdpProxy(String proxyName, String ip, String port) {
		return connectToRemoteProxy(proxyName, ip, port, false);
	}
	
	public DataInterfacePrx connectToRemoteTcpProxy(String proxyName, String ip, String port) {
		return connectToRemoteProxy(proxyName, ip, port, true);
	}
	
	protected synchronized DataInterfacePrx connectToRemoteProxy(String proxyName, String ip, String port, boolean useTcp) {
		
		DataInterfacePrx proxy;
		Ice.ObjectPrx prx;
		
		if(useTcp) {
	        prx = communicator.stringToProxy(proxyName +":tcp -h "+ ip + " -p "+ port);
		}
		else {
	        prx = communicator.stringToProxy(proxyName +":udp -h "+ ip + " -p "+ port).ice_datagram();
		}
		
		proxy = Communication.DataInterfacePrxHelper.uncheckedCast(prx);
		remoteProxies.add(proxy);
		
		return remoteProxies.peek();
	}
	
	public void addLocalUdpProxy(_DataInterfaceDisp localObject, String proxyName, String localPort) {
		addLocalProxy(localObject, proxyName, localPort, false);
	}
	
	public void addLocalTcpProxy(_DataInterfaceDisp localObject, String proxyName, String localPort) {
		addLocalProxy(localObject, proxyName, localPort, true);
	}
	
	protected void addLocalProxy(_DataInterfaceDisp localObject, String proxyName, String localPort, boolean useTcp) {
		
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
	}
}
