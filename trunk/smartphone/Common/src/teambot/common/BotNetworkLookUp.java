package teambot.common;

import teambot.common.interfaces.ICyclicCallback;

public class BotNetworkLookUp implements ICyclicCallback
{
	protected CyclicCaller cyclicCaller;
	protected String _constIpPart = "0.0.0.";
	protected int _lastByeOfLocalBotIp = 0;

	public BotNetworkLookUp()
	{
		String[] ipParts = Bot.id().split("\\.");
		_constIpPart = ipParts[0] + "." + ipParts[1] + "." + ipParts[2] + ".";
		_lastByeOfLocalBotIp = Integer.parseInt(ipParts[3]);

		cyclicCaller = new CyclicCaller(this, Settings.sleepTimeBetweenBotLookUps_ms);
		new Thread(cyclicCaller).start();
	}

	public void stop()
	{
		cyclicCaller.running.set(false);
	}

	@Override
	public void callback_cyclic(int callbackIntervalInfo_ms)
	{
		String ip = "";
		ITeambotPrx proxy = null;
		System.out.println("running...");

		for (int i = 1; i < 255; i++)
		{
			if (i == _lastByeOfLocalBotIp)
				continue;

			ip = _constIpPart + Integer.toString(i);

			if (Bot.isRegistered(ip))
				continue;

			proxy = Bot.networkHub().connectionPossible(ip);
			if (proxy != null)
			{
				try
				{
					Bot.registerBot(proxy.getIdRemote(), proxy);
				} catch (Ice.TimeoutException timoutEx)
				{
					Settings.timoutOnSingleBotLookUp_ms += 50;
					i--;
					System.out.println("Timeout is too low, increased to " + Settings.timoutOnSingleBotLookUp_ms);
				}
			}

		}
	}
}
