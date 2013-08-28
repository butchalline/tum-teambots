package android.graphics;

public class PointF {
	
	public float x;
	public float y;
	
	public PointF() {
		
	}
	
	public PointF(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PointF && x == ((PointF) obj).x & y == ((PointF) obj).y)
			return true;
		
		return false;
	}
}
