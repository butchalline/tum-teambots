package teambot.common.communication;

import teambot.common.ITeambotPrx;
import teambot.common.Settings;
import teambot.common.interfaces.IBotKeeper;
import teambot.common.interfaces.ICyclicCallback;
import teambot.common.utils.CyclicCaller;

public class BotNetworkDiscovery implements ICyclicCallback
{
	protected CyclicCaller _cyclicCaller;
	protected String _constIpPart = "0.0.0.";
	protected int _lastByeOfLocalBotIp = 0;
	
	protected NetworkHub _networkHub;
	protected IBotKeeper _callbackListener;

	public BotNetworkDiscovery(NetworkHub networkHub, IBotKeeper objectForCallback)
	{
		String[] ipParts = networkHub.getIp().split("\\.");
		_constIpPart = ipParts[0] + "." + ipParts[1] + "." + ipParts[2] + ".";
		_lastByeOfLocalBotIp = Integer.parseInt(ipParts[3]);

		_networkHub = networkHub;
		_callbackListener = objectForCallback;
		
		_cyclicCaller = new CyclicCaller(this, Settings.sleepTimeBetweenBotLookUps_ms);
		new Thread(_cyclicCaller).start();
		System.out.println("Network lookup started");
	}

	public void stop()
	{
		_cyclicCaller.running.set(false);
	}

	@Override
	public void callback_cyclic(int callbackIntervalInfo_ms)
	{
		String ip = "";
		ITeambotPrx proxy = null;

		for (int i = 1; i < 255; i++)
		{
			if (i == _lastByeOfLocalBotIp)
				continue;

			ip = _constIpPart + Integer.toString(i);

			if (_callbackListener.isRegistered(ip))
				continue;
			
			proxy = _networkHub.connectionPossible(ip);
			if (proxy != null)
			{
				try
				{
					System.out.println("Bot found! IP: " + ip);
					_callbackListener.registerBot(proxy.getIdRemote(), proxy);
				} catch (Ice.TimeoutException timoutEx)
				{
					Settings.timoutOnSingleBotLookUp_ms += 50;
					i--;
//					System.out.println("Timeout is too low, increased to " + Settings.timoutOnSingleBotLookUp_ms);
					continue;
				}
				if(Settings.timoutOnSingleBotLookUp_ms >= 30)
					Settings.timoutOnSingleBotLookUp_ms -= 10;
			}

		}
	}
}
