package teambot.common.usb.data;

public class ByteArrayData extends Data {

	protected byte[] data;

	public ByteArrayData(byte[] data) {
		super();
		this.data = new byte[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}
	
	public ByteArrayData(byte[] data, DataType type) {
		super(type);
		this.data = new byte[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}

	public ByteArrayData(long timestamp, byte[] data) {
		super(timestamp);
		this.data = new byte[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}

	public ByteArrayData(long timestamp, byte[] data, DataType type) {
		super(timestamp, type);
		this.data = new byte[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public ByteArrayData getClone() {
		return new ByteArrayData(this.timestamp, this.data, this.type);
	}

}
