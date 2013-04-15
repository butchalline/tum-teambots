package teambot.slam;

import teambot.common.data.PositionOrientation;
import android.graphics.PointF;

public class BotModel {

	PositionOrientation _positionOrientation;
	
	public BotModel(PositionOrientation positionOrientation)
	{
		
	}
	
	public PositionOrientation getPositionOrientation()
	{
		return _positionOrientation;
	}
	
	public PointF getPosition()
	{
		return _positionOrientation.getPosition();
	}
	
	public float getAngleInRadian()
	{
		return _positionOrientation.getAngleInRadian();
	}
}
