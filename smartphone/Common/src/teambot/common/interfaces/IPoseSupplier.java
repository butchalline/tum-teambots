package teambot.common.interfaces;

public interface IPoseSupplier {

	public void register(IPoseListener listener);
	public void register(IPoseListener listener, int callbackInterval_ms);
	public void register(IPoseListener listener, int callbackPositionDelta, int callbackAngleDelta_rad);
	public void unregister(IPoseListener listener);
}
