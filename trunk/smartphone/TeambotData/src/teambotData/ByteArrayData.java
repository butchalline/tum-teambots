package teambotData;

public class ByteArrayData extends Data {

	protected byte[] data;

	public ByteArrayData(byte[] data) {
		super();
		this.data = data;
	}
	
	public ByteArrayData(long timestamp, byte[] data) {
		super(timestamp);
		this.data = data;
	}
	
	public ByteArrayData(long timestamp, byte[] data, DataType type) {
		super(timestamp, type);
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

}
