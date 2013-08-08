package teambot.common.usb.data;

import teambot.common.Bot;

public abstract class Data {

	protected long timestamp;
	protected String botId = Bot.id();
	protected DataType type = DataType.UNSPECIFIED;

	public Data() {
		this.timestamp = System.currentTimeMillis();
	}
	
	public Data(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public Data(DataType type) {
		this.type = type;
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
	
	public String getBotId() {
		return botId;
	}
	
	public abstract Data getClone();
}
