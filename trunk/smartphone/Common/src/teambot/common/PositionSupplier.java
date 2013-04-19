package teambot.common;

import java.util.LinkedList;

import teambot.common.data.Position;
import teambot.common.interfaces.IPositionListener;
import teambot.common.interfaces.IPositionSupplier;
import android.graphics.PointF;


/**
 * 
 * Class which supplies the current position of an sensor or the Bot
 * The orientation is x-axis show forwards from the robot, y-axis to the left 
 * 
 */
public class PositionSupplier implements IPositionSupplier, IPositionListener
{
	private Position _position = new Position(0, 0, 0); 
	private LinkedList<IPositionListener> _listeners = new LinkedList<IPositionListener>();
	protected PointF _offsetFromBotCenter = new PointF(0, 0);
	protected float _offsetAngleFromBotCenter = 0;

	public PositionSupplier()
	{

	}

	public PositionSupplier(PointF offsetFromBotCenter_mm)
	{
		_offsetFromBotCenter = offsetFromBotCenter_mm;
		_position = addOffset(_position);
	}

	public PositionSupplier(PointF offsetFromBotCenter_mm, float offsetAngleFromBotCenter_rad)
	{
		_offsetFromBotCenter = offsetFromBotCenter_mm;
		_offsetAngleFromBotCenter = offsetAngleFromBotCenter_rad;
		_position = addOffset(_position);
	}

	public synchronized Position getBotPosition()
	{
		return _position;
	}

	@Override
	public synchronized void register(IPositionListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public synchronized void callback_PositionChanged(Position newPosition)
	{
		_position = addOffset(newPosition);
		for (IPositionListener listener : _listeners)
		{
			listener.callback_PositionChanged(newPosition);
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
	
	private Position addOffset(Position currentBotPosition)
	{		
		float x = currentBotPosition.getX() + (float) Math.cos(currentBotPosition.getAngleInRadian() + Math.PI / 2) * _offsetFromBotCenter.y
				+ (float) Math.cos(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.x;
		float y = currentBotPosition.getX() + (float) Math.sin(currentBotPosition.getAngleInRadian() + Math.PI / 2) * _offsetFromBotCenter.y
				+ (float) Math.cos(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.x;
		
		float angle = Position.normalizeAngle_plusMinusPi(currentBotPosition.getAngleInRadian() + _offsetAngleFromBotCenter);
		
		return new Position(x, y, angle);		
	}

	@Override
	public void register(IPositionListener listener, float callbackInterval_Hz)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(IPositionListener listener,
			float callbackPositionDelta, float callbackAngleDelta_rad)
	{
		// TODO Auto-generated method stub
		
	}
}
