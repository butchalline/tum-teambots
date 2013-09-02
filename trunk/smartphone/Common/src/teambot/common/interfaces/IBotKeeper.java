package teambot.common.interfaces;

import teambot.common.ITeambotPrx;

public interface IBotKeeper
{
	public boolean isRegistered(String botId);
	public void registerBot(String botId, ITeambotPrx proxy);
	public void unregisterBot(String botId);
}
