package teambot.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.PointF;

import teambot.common.data.Position;

import Ice.Current;

public class Bot {
	
	static private String _botId = "12";
	static private Position _position= new Position(new PointF(0, 0), 0);
	
	static public NetworkHub networkHub = new NetworkHub();
	static public NetworkHub networkDebugHub = new NetworkHub(true);
	
	static public Position getPosition()
	{
		return _position;
	}
	
	static public float getAngleInRadian()
	{
		return _position.getAngleInRadian();
	}
	
	/**
	 * Mapping of the IP as a string to BotRegistrationPrx of the registered bots
	 */
	static protected Map<String, ITeambotPrx> registeredBots = new HashMap<String, ITeambotPrx>(30);
	static private BotRegistration botRegistration = new BotRegistration(); 
	
	static {
//		networkHub.start();
//		networkDebugHub.start();
//		networkHub.addLocalTcpProxy(botRegistration, "BotRegistration", "61000");
//		new Thread(botRegistration).start();
	}
	
	/**
	 * Class which other bots can use to register at this bot
	 * (= tell them that they exist)
	 */
	private static class BotRegistration extends _ITeambotDisp implements Runnable {

		private static final long serialVersionUID = -8924536353000644369L;
		AtomicBoolean running = new AtomicBoolean(false);
		
		@Override
		public String getId(Current __current) {
			return _botId;
		}

		@Override
		public void run() {
			running.set(true);
			while(running.get()) {
				
				running.set(false); //TODO comment this out if there is a use for this 
				synchronized (this) {
					for (Map.Entry<String, ITeambotPrx> entry : registeredBots.entrySet()) {
						entry.getValue();
						//TODO the idea was to check if the bot is still reachable
						//but maybe just do something else..
					}	
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}			
		}
		
		public void stopUpdate() {
			running.set(false);
		}
	}
	
	static public boolean isRegistered(String botId) {
		if(registeredBots.containsKey(botId))
			return true;
		
		return false;
	}
	
	static public String getId() {
		return _botId;
	}
	
	static public void setId(String botId) {
		_botId = botId; 
	}
	
	static public void deinitialize() {
		botRegistration.stopUpdate();
	}
}
