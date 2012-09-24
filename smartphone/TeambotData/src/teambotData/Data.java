package teambotData;

public abstract class Data {

	protected long timestamp;
	protected int botId = -1;
	protected DataType type = DataType.UNSPECIFIED;

	public Data(int botId) {
		this.botId = botId;
		this.timestamp = System.currentTimeMillis();
	}
	
	public Data(int botId, long timestamp) {
		this.botId = botId;
		this.timestamp = timestamp;
	}
	
	public Data(int botId, DataType type) {
		this.botId = botId;
		this.timestamp = System.currentTimeMillis();
	}
	
	public Data(int botId, long timestamp, DataType type) {
		this.botId = botId;
		this.timestamp = timestamp;
		this.type = type;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public DataType getType() {
		return type;
	}
	
	abstract public byte[] dataAsByteArray();
}
