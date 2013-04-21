package teambot.common.interfaces;

public interface IPositionSupplier {

	public void register(IPositionListener listener);
	public void register(IPositionListener listener, int callbackInterval_ms);
	public void register(IPositionListener listener, int callbackPositionDelta, int callbackAngleDelta_rad);
}
