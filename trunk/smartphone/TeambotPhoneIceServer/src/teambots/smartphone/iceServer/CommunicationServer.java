package teambots.smartphone.iceServer;

import Ice.InitializationData;


public class CommunicationServer extends Ice.Application {

		
    public int
    run(String[] args)
    {
        Ice.ObjectAdapter adapter = communicator().createObjectAdapter("MessageInterface");
        adapter.add(new MessageInterfaceI(), communicator().stringToIdentity("messageInterface"));
        adapter.activate();
        communicator().waitForShutdown();
        return 0;
    }

	static InitializationData createInitialisationData() {
		
        Ice.InitializationData initData = new Ice.InitializationData();
        initData.properties = Ice.Util.createProperties();
        initData.properties.setProperty("MessageInterface.Endpoints", "tcp -p 10000");

        initData.properties.setProperty("Ice.Warn.Connections", "1");
        
//        initData.properties.setProperty("Ice.Plugin.IceSSL", "IceSSL.PluginFactory");
//        initData.properties.setProperty("IceSSL.DefaultDir", "./certs");
//        initData.properties.setProperty("IceSSL.Keystore", "server.jks");
//        initData.properties.setProperty("IceSSL.Password", "password"); 
        
        return initData;
	}


}