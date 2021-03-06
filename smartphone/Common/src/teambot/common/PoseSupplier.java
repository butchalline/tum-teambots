package teambot.common;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;

import teambot.common.data.Pose;
import teambot.common.interfaces.ICyclicCallback;
import teambot.common.interfaces.IPoseChangeListener;
import teambot.common.interfaces.IPoseChangeSupplier;
import teambot.common.interfaces.IPoseListener;
import android.graphics.PointF;

/**
 * 
 * Class which supplies the current position of an sensor or the Bot The
 * orientation is x-axis show forwards from the robot, y-axis to the left 
 * 
 */
public class PoseSupplier implements IPoseChangeSupplier, IPoseChangeListener, ICyclicCallback
{
	protected Pose _pose = new Pose(0, 0, 0);
	protected PointF _offsetFromBotCenter = new PointF(0, 0);
	protected float _offsetAngleFromBotCenter = 0;

	protected LinkedList<IPoseListener> _listeners = new LinkedList<IPoseListener>();
	protected HashMap<SimpleEntry<Integer, Integer>, LinkedList<SimpleEntry<IPoseListener, Pose>>> _listeners_changeDelta = new HashMap<SimpleEntry<Integer, Integer>, LinkedList<SimpleEntry<IPoseListener, Pose>>>();
	protected HashMap<Integer, LinkedList<IPoseListener>> _listeners_cyclic = new HashMap<Integer, LinkedList<IPoseListener>>(
			5);

	protected LinkedList<IPoseChangeListener> _changeListeners = new LinkedList<IPoseChangeListener>();
	
	public PoseSupplier()
	{

	}
	
	public PoseSupplier(PoseSupplier poseSupplier)
	{
		_pose = new Pose(poseSupplier._pose);
		_offsetFromBotCenter = new PointF(poseSupplier._offsetFromBotCenter.x, poseSupplier._offsetFromBotCenter.y);
		_offsetAngleFromBotCenter = poseSupplier._offsetAngleFromBotCenter;
	}

	public PoseSupplier(Pose offsetFromBotCenter_mm)
	{
		_offsetFromBotCenter = offsetFromBotCenter_mm.getPosition();
		_offsetAngleFromBotCenter = offsetFromBotCenter_mm.getAngleInRadian();
		_pose = addOffset(_pose);
	}
	
	public PoseSupplier(PointF offsetFromBotCenter_mm)
	{
		_offsetFromBotCenter = offsetFromBotCenter_mm;
		_pose = addOffset(_pose);
	}

	public PoseSupplier(PointF offsetFromBotCenter_mm, float offsetAngleFromBotCenter_rad)
	{
		_offsetFromBotCenter = offsetFromBotCenter_mm;
		_offsetAngleFromBotCenter = offsetAngleFromBotCenter_rad;
		_pose = addOffset(_pose);
	}

	public synchronized Pose getPose()
	{
		return _pose;
	}

	public synchronized PointF getPosition()
	{
		return _pose.getPosition();
	}

	public synchronized float getX()
	{
		return _pose.getX();
	}

	public synchronized float getY()
	{
		return _pose.getY();
	}

	public synchronized float getAngleInRadian()
	{
		return _pose.getAngleInRadian();
	}

	public synchronized float getAngleInDegree()
	{
		return _pose.getAngleInDegree();
	}
	
	private Pose addOffset(Pose currentBotPosition)
	{
		float x = currentBotPosition.getX() + (float) Math.cos(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.x - (float) Math.sin(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.y;
		float y = currentBotPosition.getY() + (float) Math.sin(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.x + (float) Math.cos(currentBotPosition.getAngleInRadian())
				* _offsetFromBotCenter.y;

		float angle = Pose
				.normalizeAngle_plusMinusPi(currentBotPosition.getAngleInRadian() + _offsetAngleFromBotCenter);

		return new Pose(x, y, angle);
	}
	
	@Override
	public synchronized void poseChangeCallback(Pose poseChange, boolean changeIsRelativeToTheLastPosition)
	{
		
		for (IPoseChangeListener listener : _changeListeners)
		{
			listener.poseChangeCallback(poseChange, changeIsRelativeToTheLastPosition);
		}
		
		if(!changeIsRelativeToTheLastPosition)
			_pose.addToAll(poseChange);
		else
			_pose.addToAll(poseChange.transformPosePosition(_pose.getAngleInRadian()));
		Pose newPose = new Pose(_pose);
		
		int updateDifferencePosition;
		int updateDifferenceAngle_grad;

		for (IPoseListener listener : _listeners)
		{
			listener.poseUpdateCallback(newPose);
		}

		for (SimpleEntry<Integer, Integer> minChangeEntry : _listeners_changeDelta.keySet())
		{
			for (SimpleEntry<IPoseListener, Pose> listener : _listeners_changeDelta.get(minChangeEntry))
			{
				updateDifferencePosition = Math.round(Pose.calculatDistance(listener.getValue(), newPose));
				updateDifferenceAngle_grad = Math.abs(Math.round(listener.getValue().getAngleInDegree()
						- newPose.getAngleInDegree()));

				if (updateDifferenceAngle_grad >= minChangeEntry.getValue()
						|| updateDifferencePosition >= minChangeEntry.getKey())
				{
					listener.getKey().poseUpdateCallback(newPose);
					listener.setValue(newPose);
				}
			}
		}
	}
	
	static public Pose addOffset(Pose botPosition, Pose offset)
	{
		float x = botPosition.getX() + (float) Math.cos(botPosition.getAngleInRadian()) * offset.getX()
				- (float) Math.sin(botPosition.getAngleInRadian()) * offset.getY();
		float y = botPosition.getY() + (float) Math.sin(botPosition.getAngleInRadian()) * offset.getX()
				+ (float) Math.cos(botPosition.getAngleInRadian()) * offset.getY();

		float angle = Pose.normalizeAngle_plusMinusPi(botPosition.getAngleInRadian() + offset.getAngleInRadian());

		return new Pose(x, y, angle);
	}

	@Override
	public synchronized void callback_cyclic(int callbackIntervalInfo_ms)
	{
		LinkedList<IPoseListener> listeners = _listeners_cyclic.get(callbackIntervalInfo_ms);
		Pose newPosition = getPose();

		for (IPoseListener listener : listeners)
		{
			listener.poseUpdateCallback(newPosition);
		}
	}

	@Override
	public synchronized void register(IPoseListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public synchronized void register(IPoseListener listener, int callbackInterval_ms)
	{
		throw new UnsupportedOperationException("Needs bugfixing -> see TODO");

		// TODO start a cyclic caller when a new callbackInterval_ms is received
		// Instead of waiting for a callback with an certain interval it would
		// also possible to measure the time from the last call see if >
		// callbackInterval_ms

		/*
		 * if (_listeners_cyclic.containsKey(callbackInterval_ms)) {
		 * _listeners_cyclic.get(callbackInterval_ms).add(listener); return; }
		 * 
		 * _listeners_cyclic.put(callbackInterval_ms, new
		 * LinkedList<IPositionListener>());
		 * _listeners_cyclic.get(callbackInterval_ms).add(listener);
		 */
	}

	@Override
	public synchronized void register(IPoseListener listener, int callbackPositionDelta, int callbackAngleDelta_deg)
	{
		throw new UnsupportedOperationException("Needs bugfixing -> see TODO");

		// TODO Instead of waiting for ONE change > delta sum up the changes
		// over time and wait till that is > delta
		/*
		 * SimpleEntry<Integer, Integer> minChangeEntry = new
		 * SimpleEntry<Integer, Integer>(callbackPositionDelta,
		 * callbackAngleDelta_deg);
		 * 
		 * if (_listeners_changeDelta.containsKey(minChangeEntry)) {
		 * _listeners_changeDelta.get(minChangeEntry).add(new
		 * SimpleEntry<IPositionListener, Position>(listener, _position));
		 * return; }
		 * 
		 * _listeners_changeDelta.put(minChangeEntry, new
		 * LinkedList<SimpleEntry<IPositionListener, Position>>());
		 * _listeners_changeDelta.get(minChangeEntry).add(new
		 * SimpleEntry<IPositionListener, Position>(listener, _position));
		 */
	}

	@Override
	public synchronized void unregister(IPoseListener listener)
	{
		_listeners.remove(listener);
	}

	@Override
	public synchronized void registerForChangeUpdates(IPoseChangeListener listener)
	{
		_changeListeners.add(listener);
	}

	@Override
	public synchronized void unregisterForChangeUpdates(IPoseChangeListener listener)
	{
		_changeListeners.remove(listener);
	}
}
