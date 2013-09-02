package teambotData;

public class FloatArrayData extends Data {

	float[] data;
	
	public FloatArrayData() {
		super();
	}
	
	public FloatArrayData(long timestamp, float[] data) {
		super(timestamp);
		this.data = new float[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}
	
	public FloatArrayData(long timestamp, float[] data, DataType type) {
		super(timestamp, type);
		this.data = new float[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);
	}

	public float[] getData() {
		return data;
	}

	@Override
	public FloatArrayData getClone() {
		return new FloatArrayData(this.timestamp, this.data, this.type);
	}
}
