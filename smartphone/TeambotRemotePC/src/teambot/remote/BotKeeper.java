package teambot.remote;

import teambot.RegisterClass;
import teambot.common.ITeambotPrx;
import teambot.common.communication.NetworkHub;
import teambot.common.interfaces.IBotKeeper;

public class BotKeeper implements IBotKeeper
{
	NetworkHub _hub = null;
	Ice.ObjectAdapter _streamObjectAdapter = null;
	ITeambotPrx _bot = null;
	String _botIp = "";
	
	public void addNetworkHub(NetworkHub hub)
	{
		_hub = hub;
	}
	
	public void addStreamObjectAdapter(Ice.ObjectAdapter streamObjectAdapter)
	{
		_streamObjectAdapter = streamObjectAdapter;
	}
	
	@Override
	public boolean isRegistered(String botId)
	{
		if(_botIp.equals(botId))
			return true;
		return false;
	}

	@Override
	public synchronized void registerBot(String botId, ITeambotPrx proxy)
	{
		if(_bot != null || _hub == null || _streamObjectAdapter == null)
			return;
		
		proxy.ice_getConnection().setAdapter(_streamObjectAdapter);
		proxy.addClient(_hub.getIdentity(), RegisterClass.STREAMRECEIVER);
		_bot = proxy;
		_botIp = botId;	
	}

	@Override
	public void unregisterBot(String botId)
	{
		
	}

}
