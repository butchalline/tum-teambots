package teambot.common;

import teambot.common.data.Pose;

public class BotLayoutConstants
{
	public static final float DistanceBeweenWheelCenters_mm = 33f;
	public static final float WheelRadius_mm = 17 * 10f;
	
	public static final Pose distanceSensorOffset_mm = new Pose(69, 0, 0);//-115, -130, -90 * Constants.DegreeToRadian);
}
