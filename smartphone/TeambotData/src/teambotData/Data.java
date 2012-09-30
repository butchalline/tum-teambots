package teambotData;

import teambot.smartphone.Bot;

public abstract class Data {

	protected long timestamp;
	protected int botId = Bot.getId();
	protected DataType type = DataType.UNSPECIFIED;

	public Data() {
		this.timestamp = System.currentTimeMillis();
	}
	
	public Data(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public Data(DataType type) {
		this.timestamp = System.currentTimeMillis();
	}
	
	public Data(long timestamp, DataType type) {
		this.timestamp = timestamp;
		this.type = type;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public DataType getType() {
		return type;
	}
	
	public int getBotId() {
		return botId;
	}
}
