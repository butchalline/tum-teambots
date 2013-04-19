package teambot.common.interfaces;

import teambot.common.data.Position;

public interface IPositionListener {
	public void callback_PositionChanged(Position newPosition);
}
