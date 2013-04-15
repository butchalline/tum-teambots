package teambot.common.data;

import teambot.common.utils.Constants;
import android.graphics.PointF;

public class PositionOrientation {

	protected PointF position;
	protected float angleInRadian;

	public PositionOrientation(PointF position, float anlgeInRadian) {
		this.position = position;
		this.angleInRadian = anlgeInRadian;
	}

	public PositionOrientation(float x, float y, float anlgeInRadian) {
		position = new PointF(x, y);
		this.angleInRadian = anlgeInRadian;
	}

	public synchronized void setPosition(PointF position) {
		this.position = position;
	}

	public synchronized void setX(float x) {
		position.x = x;
	}

	public synchronized void setY(float y) {
		position.y = y;
	}

	public synchronized void setAngleInRadian(float angle) {
		this.angleInRadian = angle;
	}

	public synchronized void setAngleInDegree(float angle) {
		this.angleInRadian = angle * Constants.DegreeToRadian;
	}

	public synchronized PointF getPosition() {
		return position;
	}

	public synchronized float getX() {
		return position.x;
	}

	public synchronized float getY() {
		return position.y;
	}

	public synchronized float getAngleInRadian() {
		return angleInRadian;
	}

	public synchronized float getAngleInDegree() {
		return angleInRadian * Constants.RadianToDegree;
	}

	public synchronized void increaseX() {
		position.x++;
	}
	
	public synchronized void decreaseX() {
		position.x--;
	}
	
	public synchronized void addToX(float count) {
		position.x += count;
	}
	
	public synchronized void increaseY() {
		position.y++;
	}
	
	public synchronized void decreasey() {
		position.y--;
	}
	
	public synchronized void addToY(float count) {
		position.y += count;
	}
	
	public synchronized void addToAngleInRadian(float addition) {
		angleInRadian += addition;
	}
	
	public synchronized void addToAngleInDegree(float additionInDegree) {
		angleInRadian += additionInDegree * Constants.DegreeToRadian;
	}
	
	public synchronized void addToAll(float xAddition, float yAddition, float angleAdditionInRadian) {
		position.x += xAddition;
		position.y += yAddition;
		angleInRadian += angleAdditionInRadian;
	}
	
	static public float normalizeAngle_plusMinusPi(float angle) {
		if (angle > Constants.piAsFloat)
			angle -= 2 * Constants.piAsFloat;
		else if (angle <= -Constants.piAsFloat)
			angle += 2 * Constants.piAsFloat;
		return angle;
	}
	
	static public float normalizeAngle_plusTwoPi(float angle) {
		if (angle < 0f)
			angle = 2*Constants.piAsFloat - angle;
		else if (angle >= 2*Constants.piAsFloat)
			angle -= 2 * Constants.piAsFloat;
		return angle;
	}
	
	static public float normalizeAngle_plusMinus180(float angle) {
		if (angle > 180f)
			angle -= 360f;
		else if (angle <= -180f)
			angle += 360f;
		return angle;
	}
	
	static public float normalizeAngle_plusMinus360(float angle) {
		if (angle > 360f)
			angle -= 720f;
		else if (angle <= -360f)
			angle += 720f;
		return angle;
	}
}
