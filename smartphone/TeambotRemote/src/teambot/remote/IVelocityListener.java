package teambot.remote;

public interface IVelocityListener
{
	public void onVelocityChange(VelocitySupplier.Direction direction, byte rightSpeed, byte leftSpeed);
}
