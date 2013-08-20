package android.graphics;

public class Point {
	public int x;
	public int y;

	public Point() {

	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point)
	{
		synchronized (point)
		{
			x = point.x;
			y = point.y;	
		}
	}

	@Override
	public boolean equals(Object o) {
		// Return true if the objects are identical.
		// (This is just an optimization, not required for correctness.)
		if (this == o) {
			return true;
		}

		// Return false if the other object has the wrong type.
		// This type may be an interface depending on the interface's
		// specification.
		if (!(o instanceof Point)) {
			return false;
		}

		// Cast to the appropriate type.
		// This will succeed because of the instanceof, and lets us access
		// private fields.
		Point lhs = (Point) o;

		// Check each field. Primitive fields, reference fields, and nullable
		// reference
		// fields are all treated differently.
		return lhs.x == this.x && lhs.y == this.y;
	}

	@Override
	public int hashCode() {
		// Start with a non-zero constant.
		int result = 17;

		// Include a hash for each field.
		result = 31 * result + x;
		result = 31 * result + y;

		return result;
	}
	
	@Override
	public String toString() {
		return this.x + ";" + this.y;
	}
}
