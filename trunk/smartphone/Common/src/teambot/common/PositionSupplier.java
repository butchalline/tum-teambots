package teambot.common;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;

import teambot.common.data.Position;
import teambot.common.interfaces.ICyclicCallback;
import teambot.common.interfaces.IPositionListener;
import teambot.common.interfaces.IPositionSupplier;
import android.graphics.PointF;


/**
 * 
 * Class which supplies the current position of an sensor or the Bot
 * The orientation is x-axis show forwards from the robot, y-axis to the left 
 *
 *		  	  ^ x-axis     equals:      ^  y-axis
 *		  	  |						    |
 *		<-- |---|					   ---
 *		y-axis						    |  --->
 *									   ---  x-axis
 *
 * 
 */
public class PositionSupplier implements IPositionSupplier, IPositionListener, ICyclicCallback
{
	protected Position _position = new Position(0, 0, 0);
	protected PointF _offsetFromBotCenter = new PointF(0, 0);
	protected float _offsetAngleFromBotCenter = 0;
	
	protected LinkedList<IPositionListener> _listeners = new LinkedList<IPositionListener>();
	protected HashMap<SimpleEntry<Integer, Integer>, LinkedList<IPositionListener>> _listeners_changeDelta
						= new HashMap<SimpleEntry<Integer,Integer>, LinkedList<IPositionListener>>();
	protected HashMap<Integer, LinkedList<IPositionListener>> _listeners_cyclic = new HashMap<Integer, LinkedList<IPositionListener>>(5);

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
		float x = currentBotPosition.getX() + (float) Math.cos(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.x
				- (float) Math.sin(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.y;
		float y = currentBotPosition.getY() + (float) Math.sin(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.x
				+ (float) Math.cos(currentBotPosition.getAngleInRadian()) * _offsetFromBotCenter.y;
		
		float angle = Position.normalizeAngle_plusMinusPi(currentBotPosition.getAngleInRadian() + _offsetAngleFromBotCenter);
		
		return new Position(x, y, angle);
	}

	@Override
	public synchronized void callback_PositionChanged(Position newPosition)
	{	
		int updateDifferencePosition = Math.round(Position.calculatDistance(_position, newPosition));
		int updateDifferenceAngle_grad = Math.abs(Math.round(_position.getAngleInDegree() - newPosition.getAngleInDegree()));
	
		_position = addOffset(newPosition);
		
		for(IPositionListener listener : _listeners)
		{
			listener.callback_PositionChanged(_position);
		}
		
		for(SimpleEntry<Integer, Integer> minChangeEntry : _listeners_changeDelta.keySet())
		{
			if(updateDifferenceAngle_grad >= minChangeEntry.getValue() || updateDifferencePosition >= minChangeEntry.getKey())
			{
				for(IPositionListener listener : _listeners_changeDelta.get(minChangeEntry))
				{
					listener.callback_PositionChanged(_position);
				}
			}
		}
	}
	
	@Override
	public synchronized void callback_cyclic(int callbackIntervalInfo_ms)
	{
		LinkedList<IPositionListener> listeners = _listeners_cyclic.get(callbackIntervalInfo_ms);
		Position newPosition = getBotPosition();
		
		for(IPositionListener listener : listeners)
		{
			listener.callback_PositionChanged(newPosition);
		}
	}

	@Override
	public synchronized void register(IPositionListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public synchronized void register(IPositionListener listener, int callbackInterval_ms)
	{
		if(_listeners_cyclic.containsKey(callbackInterval_ms))
		{
			_listeners_cyclic.get(callbackInterval_ms).add(listener);
			return;
		}
		
		_listeners_cyclic.put(callbackInterval_ms, new LinkedList<IPositionListener>());
		_listeners_cyclic.get(callbackInterval_ms).add(listener);		
	}

	@Override
	public synchronized void register(IPositionListener listener,
			int callbackPositionDelta, int callbackAngleDelta_deg)
	{
		SimpleEntry<Integer, Integer> minChangeEntry = new SimpleEntry<Integer, Integer>(callbackPositionDelta, callbackAngleDelta_deg);
		
		if(_listeners_changeDelta.containsKey(minChangeEntry))
		{
			_listeners_changeDelta.get(minChangeEntry).add(listener);
			return;
		}
		
		_listeners_changeDelta.put(minChangeEntry, new LinkedList<IPositionListener>());
		_listeners_changeDelta.get(minChangeEntry).add(listener);		
	}
}
