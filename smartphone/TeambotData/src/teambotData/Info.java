package teambotData;

public class Info extends ByteArrayData {

	protected String infoText = "no text given";
	protected DataType type = DataType.INFO;
	
	public Info(String infoText) {
		super(infoText.getBytes());
		this.infoText = infoText;
	}
	
	public Info(int botId, long timestamp, String infoText) {
		super(timestamp, infoText.getBytes());
		this.infoText = infoText;
	}
}
