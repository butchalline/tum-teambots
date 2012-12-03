package teambot.common;

public class Bot {

	static protected int _botId = -1;

	public static int getId() {
		return _botId;
	}
	
	public static void setId(int botId) {
		_botId = botId; 
	}
}
