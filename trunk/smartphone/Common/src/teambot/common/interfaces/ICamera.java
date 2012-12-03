package teambot.common.interfaces;

public interface ICamera {
	public void registerCallback(IImageProcessor processor);
	public byte[] getLatestImage();
}
