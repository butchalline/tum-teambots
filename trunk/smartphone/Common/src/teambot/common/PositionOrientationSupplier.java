package teambot.common;

import java.util.LinkedList;

import teambot.common.data.PositionOrientation;
import teambot.common.interfaces.IPositionListener;
import teambot.common.interfaces.IPositionOrientationSupplier;
import android.graphics.PointF;


/**
 * 
 * Class which supplies the current position of an sensor or the Bot
 * The orientation is x-axis show forwards from the robot, y-axis to the left 
 * 
 */
public class PositionOrientationSupplier implements IPositionOrientationSupplier, IPositionListener
{
	private PositionOrientation _position = new PositionOrientation(0, 0, 0); 
	private LinkedList<IPositionListener> _listeners = new LinkedList<IPositionListener>();
	protected PointF _offsetFromBotCenter = new PointF(0, 0);
	protected float _offsetAngleFromBotCenter = 0;

	public PositionOrientationSupplier()
	{

	}

	public PositionOrientationSupplier(PointF offsetFromBotCenter_mm)
	{
		_offsetFromBotCenter = offsetFromBotCenter_mm;
		_position = addOffset(_position);
	}

	public PositionOrientationSupplier(PointF offsetFromBotCenter_mm, float offsetAngleFromBotCenter_rad)
	{
		_offsetFromBotCenter = offsetFromBotCenter_mm;
		_offsetAngleFromBotCenter = offsetAngleFromBotCenter_rad;
		_position = addOffset(_position);
	}

	public synchronized PositionOrientation getBotPositionOrientation()
	{
		return _position;
	}

	@Override
	public synchronized void register(IPositionListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public synchronized void callback_PositionChanged(PositionOrientation newPositionOrientation)
	{
		_position = addOffset(newPositionOrientation);
		for (IPositionListener listener : _listeners)
		{
			listener.callback_PositionChanged(newPositionOrientation);
		}
	}

	public synchronized PointF getPosition()
	{
		return _position.getPosition();
	}

	public synchronized float getX()
	{
		return _position.getX();
	}

	public synchronized float getY()
	{
		return _position.getY();
	}

	public synchronized float getAngleInRadian()
	{
		return _position.getAngleInRadian();
	}

	public synchronized float getAngleInDegree()
	{
		return _position.getAngleInDegree();
	}
	
	private PositionOrientation addOffset(PositionOrientation currentBotPosition)
	{		
		float x = currentBotPosition.getX() + (float) Math.cos(currentBotPosition.getAngleInRadian() + Math.PI / 2) * _offsetFromBotCenter.y
				+ (float) Math.cos(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.x;
		float y = currentBotPosition.getX() + (float) Math.sin(currentBotPosition.getAngleInRadian() + Math.PI / 2) * _offsetFromBotCenter.y
				+ (float) Math.cos(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.x;
		
		float angle = PositionOrientation.normalizeAngle_plusMinusPi(currentBotPosition.getAngleInRadian() + _offsetAngleFromBotCenter);
		
		return new PositionOrientation(x, y, angle);		
	}
}
