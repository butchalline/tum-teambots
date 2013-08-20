package android.graphics;

public class PointF {
	
	public float x;
	public float y;
	
	public PointF() {
		
	}
	
	public PointF(PointF point) {
		synchronized (point)
		{
			this.x = new Float(point.x);
			this.y = new Float(point.y);	
		}
	}
	
	public PointF(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
