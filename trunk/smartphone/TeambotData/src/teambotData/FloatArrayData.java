package teambotData;

public class FloatArrayData extends Data {

	float[] data;
	
	public FloatArrayData() {
		super();
	}
	
	public FloatArrayData(long timestamp, float[] data) {
		super(timestamp);
		this.data = data;
	}
	
	public FloatArrayData(long timestamp, float[] data, DataType type) {
		super(timestamp, type);
		this.data = data;
	}

	public float[] getData() {
		return data;
	}

}
