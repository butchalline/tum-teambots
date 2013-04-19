package teambot.common.interfaces;

public interface IPositionSupplier {

	public void register(IPositionListener listener);
	public void register(IPositionListener listener, float callbackInterval_Hz);
	public void register(IPositionListener listener, float callbackPositionDelta, float callbackAngleDelta_rad);
}
