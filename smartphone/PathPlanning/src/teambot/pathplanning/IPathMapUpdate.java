package teambot.pathplanning;

import android.graphics.Point;


public interface IPathMapUpdate {
	void updatePathMap(Point point, Occupation occupation);
}