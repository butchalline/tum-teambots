package teambot.common.interfaces;

import teambot.common.data.Pose;

public interface IPositionListener {
	public void callback_PositionChanged(Pose newPosition);
}
