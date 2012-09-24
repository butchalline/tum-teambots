package teambotData;

public class Info extends Data {

	protected String infoText = "no text given";
	protected DataType type = DataType.INFO;
	
	public Info(String infoText) {
		super();
		this.infoText = infoText;
	}
	
	public Info(long timestamp, String infoText) {
		super(timestamp);
		this.infoText = infoText;
	}
	
	@Override
	public byte[] dataAsByteArray() {
		return infoText.getBytes();
	}

}
