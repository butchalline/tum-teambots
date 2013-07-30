package teambot.common.utils;

import android.graphics.Point;
import android.graphics.PointF;

public class MathHelper
{

	public static float calculateDistance(Point p1, Point p2)
	{

		return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
	}

	public static float calculateDistance(PointF p1, PointF p2)
	{

		return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
	}
}
