package teambotData;

public class LoggerInfo extends Info {
	
	protected DataType type = DataType.INFO;
	
	public LoggerInfo(int botId, String infoText) {
		super(botId, infoText);
	}	
}
