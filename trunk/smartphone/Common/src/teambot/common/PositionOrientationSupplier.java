package teambot.common;

import java.util.LinkedList;

import teambot.common.data.PositionOrientation;
import teambot.common.interfaces.IPositionListener;
import teambot.common.interfaces.IPositionOrientationSupplier;
import teambot.common.utils.Constants;
import android.graphics.PointF;

public class PositionOrientationSupplier implements IPositionOrientationSupplier, IPositionListener
{

	private LinkedList<IPositionListener> _listeners = new LinkedList<IPositionListener>();
	protected PointF _offsetFromBotCenter = new PointF(0, 0);
	protected float _offsetAngleFromBotCenter = 0;

	public PositionOrientationSupplier()
	{

	}

	public PositionOrientationSupplier(PointF offsetFromBotCenter)
	{
		_offsetFromBotCenter = offsetFromBotCenter;
	}

	public PositionOrientationSupplier(PointF offsetFromBotCenter, float offsetAngleFromBotCenter)
	{
		_offsetFromBotCenter = offsetFromBotCenter;
		_offsetAngleFromBotCenter = offsetAngleFromBotCenter;
	}

	public PositionOrientation getBotPositionOrientation()
	{
		// TODO: Wrong calculation
		PositionOrientation positionOrientation = Bot.getPositionOrientation();
		positionOrientation.setX(positionOrientation.getX() + _offsetFromBotCenter.x);
		positionOrientation.setY(positionOrientation.getY() + _offsetFromBotCenter.y);
		positionOrientation.setAngleInRadian(positionOrientation.getAngleInRadian() + _offsetAngleFromBotCenter);
		return positionOrientation;
	}

	@Override
	public void register(IPositionListener listener)
	{
		_listeners.add(listener);
	}

	@Override
	public void callback_PositionChanged(PositionOrientation newPositionOrientation)
	{
		for (IPositionListener listener : _listeners)
		{
			listener.callback_PositionChanged(newPositionOrientation);
		}
	}

	public synchronized PointF getPosition()
	{
		// TODO: Wrong calculation
		PointF position = Bot.getPositionOrientation().getPosition();
		position.x += _offsetFromBotCenter.x;
		position.y += _offsetFromBotCenter.y;
		return position;
	}

	public synchronized float getX()
	{
		// TODO: Wrong calculation
		return Bot.getPositionOrientation().getX() + _offsetFromBotCenter.x;
	}

	public synchronized float getY()
	{
		// TODO: Wrong calculation
		return Bot.getPositionOrientation().getY() + _offsetFromBotCenter.y;
	}

	public synchronized float getAngleInRadian()
	{
		return Bot.getPositionOrientation().getAngleInRadian() + _offsetAngleFromBotCenter;
	}

	public synchronized float getAngleInDegree()
	{
		return (Bot.getPositionOrientation().getAngleInRadian() + _offsetAngleFromBotCenter) * Constants.RadianToDegree;
	}
}
