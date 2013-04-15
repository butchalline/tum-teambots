package teambot.common.interfaces;

import teambot.common.data.PositionOrientation;

public interface IPositionListener {
	public void callback_PositionChanged(PositionOrientation newPositionOrientation);
}
