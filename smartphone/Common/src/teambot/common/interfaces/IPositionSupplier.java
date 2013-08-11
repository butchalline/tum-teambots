package teambot.common.interfaces;

public interface IPositionSupplier {

	public void register(IPoseListener listener);
	public void register(IPoseListener listener, int callbackInterval_ms);
	public void register(IPoseListener listener, int callbackPositionDelta, int callbackAngleDelta_rad);
}
