package teambotData;

public class Info extends Data {

	protected String infoText = "no text given";
	protected DataType type = DataType.INFO;
	
	public Info(int botId, String infoText) {
		super(botId);
		this.infoText = infoText;
	}
	
	public Info(int botId, long timestamp, String infoText) {
		super(botId, timestamp);
		this.infoText = infoText;
	}
	
	@Override
	public byte[] dataAsByteArray() {
		return infoText.getBytes();
	}

}
