package teambot.common;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;

import teambot.common.data.Pose;
import teambot.common.interfaces.ICyclicCallback;
import teambot.common.interfaces.IPositionListener;
import teambot.common.interfaces.IPositionSupplier;
import android.graphics.PointF;

/**
 * 
 * Class which supplies the current position of an sensor or the Bot The
 * orientation is x-axis show forwards from the robot, y-axis to the left
 * 
 * ^ x-axis equals: ^ y-axis | | <-- |---| --- y-axis | ---> --- x-axis
 * 
 * 
 */
public class PositionSupplier implements IPositionSupplier, IPositionListener, ICyclicCallback
{
	protected Pose _position = new Pose(0, 0, 0);
	protected PointF _offsetFromBotCenter = new PointF(0, 0);
	protected float _offsetAngleFromBotCenter = 0;

	protected LinkedList<IPositionListener> _listeners = new LinkedList<IPositionListener>();
	protected HashMap<SimpleEntry<Integer, Integer>, LinkedList<SimpleEntry<IPositionListener, Pose>>> _listeners_changeDelta = 
			new HashMap<SimpleEntry<Integer, Integer>, LinkedList<SimpleEntry<IPositionListener, Pose>>>();
	protected HashMap<Integer, LinkedList<IPositionListener>> _listeners_cyclic = new HashMap<Integer, LinkedList<IPositionListener>>(
			5);

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

	public synchronized Pose getBotPosition()
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

	private Pose addOffset(Pose currentBotPosition)
	{
		float x = currentBotPosition.getX() + (float) Math.cos(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.x - (float) Math.sin(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.y;
		float y = currentBotPosition.getY() + (float) Math.sin(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.x + (float) Math.cos(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.y;

		float angle = Pose.normalizeAngle_plusMinusPi(currentBotPosition.getAngleInRadian()
				+ _offsetAngleFromBotCenter);

		return new Pose(x, y, angle);
	}
	
	static public Pose addOffset(Pose botPosition, Pose offset)
	{
		float x = botPosition.getX() + (float) Math.cos(botPosition.getAngleInRadian())
				* offset.getX() - (float) Math.sin(botPosition.getAngleInRadian())
				* offset.getY();
		float y = botPosition.getY() + (float) Math.sin(botPosition.getAngleInRadian())
				* offset.getX() + (float) Math.cos(botPosition.getAngleInRadian())
				* offset.getY();

		float angle = Pose.normalizeAngle_plusMinusPi(botPosition.getAngleInRadian()
				+ offset.getAngleInRadian());

		return new Pose(x, y, angle);
	}

	@Override
	public synchronized void callback_PositionChanged(Pose newPosition)
	{
		newPosition = addOffset(newPosition);
		int updateDifferencePosition;
		int updateDifferenceAngle_grad;

		for (IPositionListener listener : _listeners)
		{
			listener.callback_PositionChanged(newPosition);
		}

		for (SimpleEntry<Integer, Integer> minChangeEntry : _listeners_changeDelta.keySet())
		{
			for (SimpleEntry<IPositionListener, Pose> listener : _listeners_changeDelta.get(minChangeEntry))
			{
				updateDifferencePosition = Math.round(Pose.calculatDistance(listener.getValue(), newPosition));
				updateDifferenceAngle_grad = Math.abs(Math.round(listener.getValue().getAngleInDegree()
						- newPosition.getAngleInDegree()));

				if (updateDifferenceAngle_grad >= minChangeEntry.getValue()
						|| updateDifferencePosition >= minChangeEntry.getKey())
				{
					listener.getKey().callback_PositionChanged(newPosition);
					listener.setValue(newPosition);
				}
			}
		}

		_position = newPosition;
	}

	@Override
	public synchronized void callback_cyclic(int callbackIntervalInfo_ms)
	{
		LinkedList<IPositionListener> listeners = _listeners_cyclic.get(callbackIntervalInfo_ms);
		Pose newPosition = getBotPosition();

		for (IPositionListener listener : listeners)
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
		throw new UnsupportedOperationException("Needs bugfixing -> see TODO");
		
		//TODO start a cyclic caller when a new callbackInterval_ms is received 
		//Instead of waiting for a callback with an certain interval it would also possible to measure the time from the last call see if  > callbackInterval_ms
		
	/*
		if (_listeners_cyclic.containsKey(callbackInterval_ms))
		{
			_listeners_cyclic.get(callbackInterval_ms).add(listener);
			return;
		}

		_listeners_cyclic.put(callbackInterval_ms, new LinkedList<IPositionListener>());
		_listeners_cyclic.get(callbackInterval_ms).add(listener);
		*/
	}

	@Override
	public synchronized void register(IPositionListener listener, int callbackPositionDelta, int callbackAngleDelta_deg)
	{
		throw new UnsupportedOperationException("Needs bugfixing -> see TODO");
		
		//TODO Instead of waiting for ONE change > delta sum up the changes over time and wait till that is > delta 
		/*
		SimpleEntry<Integer, Integer> minChangeEntry = new SimpleEntry<Integer, Integer>(callbackPositionDelta,
				callbackAngleDelta_deg);

		if (_listeners_changeDelta.containsKey(minChangeEntry))
		{
			_listeners_changeDelta.get(minChangeEntry).add(new SimpleEntry<IPositionListener, Position>(listener, _position));
			return;
		}

		_listeners_changeDelta.put(minChangeEntry,  new LinkedList<SimpleEntry<IPositionListener, Position>>());
		_listeners_changeDelta.get(minChangeEntry).add(new SimpleEntry<IPositionListener, Position>(listener, _position));
		*/
	}
}
