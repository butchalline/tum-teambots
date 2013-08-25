package teambot.common.data;

import teambot.common.utils.Constants;
import android.graphics.PointF;

public class Pose
{

	protected PointF _position;
	protected float _angleInRadian;

	public Pose(PointF position, float anlgeInRadian)
	{
		this._position = position;
		this._angleInRadian = anlgeInRadian;
	}

	public Pose(float x, float y, float anlgeInRadian)
	{
		_position = new PointF(x, y);
		this._angleInRadian = normalizeAngle_plusMinusPi(anlgeInRadian);
	}

	public Pose(Pose pose)
	{
		synchronized (pose)
		{
			this._position = new PointF(pose.getPosition());
			this._angleInRadian = pose.getAngleInRadian();
		}
	}

	public synchronized void setPosition(PointF position)
	{
		this._position = position;
	}

	public synchronized void setX(float x)
	{
		_position.x = x;
	}

	public synchronized void setY(float y)
	{
		_position.y = y;
	}

	public synchronized void setAngleInRadian(float angle)
	{
		this._angleInRadian = angle;
	}

	public synchronized void setAngleInDegree(float angle)
	{
		this._angleInRadian = angle * Constants.DegreeToRadian;
	}

	public synchronized PointF getPosition()
	{
		return _position;
	}

	public synchronized float getX()
	{
		return _position.x;
	}

	public synchronized float getY()
	{
		return _position.y;
	}

	public synchronized float getAngleInRadian()
	{
		return _angleInRadian;
	}

	public synchronized float getAngleInDegree()
	{
		return _angleInRadian * Constants.RadianToDegree;
	}

	public synchronized void increaseX()
	{
		_position.x++;
	}

	public synchronized void decreaseX()
	{
		_position.x--;
	}

	public synchronized void addToX(float count)
	{
		_position.x += count;
	}

	public synchronized void increaseY()
	{
		_position.y++;
	}

	public synchronized void decreaseY()
	{
		_position.y--;
	}

	public synchronized void addToY(float count)
	{
		_position.y += count;
	}

	public synchronized void addToAngleInRadian(float addition)
	{
		_angleInRadian += addition;
	}

	/**
	 * Normalizes around +- PI
	 * 
	 * @param additionInDegree
	 */
	public synchronized void addToAngleInRadian_normalized(float addition)
	{
		_angleInRadian += addition;
		_angleInRadian = normalizeAngle_plusMinusPi(_angleInRadian);
	}

	public synchronized void addToAngleInDegree(float additionInDegree)
	{
		_angleInRadian += additionInDegree * Constants.DegreeToRadian;
	}

	/**
	 * Normalizes around +- 180°
	 * 
	 * @param additionInDegree
	 */
	public synchronized void addToAngleInDegree_normalized(float additionInDegree)
	{
		_angleInRadian += additionInDegree * Constants.DegreeToRadian;
		_angleInRadian = normalizeAngle_plusMinusPi(_angleInRadian);
	}

	public synchronized void addToAll(Pose pose)
	{
		_position.x += pose.getX();
		_position.y += pose.getY();
		_angleInRadian += pose.getAngleInRadian();
	}

	public synchronized void subtractFromAll(Pose pose)
	{
		_position.x -= pose.getX();
		_position.y -= pose.getY();
		_angleInRadian -= pose.getAngleInRadian();
	}

	public synchronized void addToAll(float xAddition, float yAddition, float angleAdditionInRadian)
	{
		_position.x += xAddition;
		_position.y += yAddition;
		_angleInRadian += angleAdditionInRadian;
	}

	static public float normalizeAngle_plusMinusPi(float angle)
	{
		if (angle > Constants.piAsFloat)
			angle -= 2 * Constants.piAsFloat;
		else if (angle <= -Constants.piAsFloat)
			angle += 2 * Constants.piAsFloat;
		return angle;
	}

	static public float normalizeAngle_plusTwoPi(float angle)
	{
		if (angle < 0f)
			angle = 2 * Constants.piAsFloat - angle;
		else if (angle >= 2 * Constants.piAsFloat)
			angle -= 2 * Constants.piAsFloat;
		return angle;
	}

	static public float normalizeAngle_plusMinus180(float angle)
	{
		if (angle > 180f)
			angle -= 360f;
		else if (angle <= -180f)
			angle += 360f;
		return angle;
	}

	static public float normalizeAngle_plusMinus360(float angle)
	{
		if (angle > 360f)
			angle -= 720f;
		else if (angle <= -360f)
			angle += 720f;
		return angle;
	}

	static public float calculatDistance(Pose position1, Pose position2)
	{
		float x = (position1.getX() - position2.getX()) * (position1.getX() - position2.getX());
		float y = (position1.getY() - position2.getY()) * (position1.getY() - position2.getY());
		return (float) Math.sqrt(x + y);
	}

	/**
	 * 
	 * Returns a pose where the position is transformed by a rotation.
	 * 
	 * @param angle_rad
	 *            the angle between the current system and the target system (in
	 *            respect to the current system)
	 * @return a new pose objects which is the pose in the target system without
	 *         the angle being changed
	 */
	public Pose transformPosePosition(float angle_rad)
	{
		float newX = (float) Math.cos(angle_rad) * _position.x - (float) Math.sin(angle_rad) * _position.y;
		float newY = (float) Math.sin(angle_rad) * _position.x + (float) Math.cos(angle_rad) * _position.y;

		return new Pose(newX, newY, _angleInRadian);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Pose))
			return false;
		Pose pose = (Pose) obj;
		if(_position.equals(pose._position) && _angleInRadian == pose._angleInRadian)
			return true;
		return false;	
	}
}
