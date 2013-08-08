package teambot.remote;

public class VelocitySupplier implements IPitchRollListener
{
	IVelocityListener _listener;
	
	public VelocitySupplier(IVelocityListener listener){
		_listener = listener;
	}
	
	@Override
	public void onPitchRollChange(float newPitch, float newRoll)
	{
		float leftVelocity = 0;
		float rightVelocity = 0;
		
		if(newPitch >= 0)
		{
			leftVelocity = newPitch + newRoll;
			rightVelocity = newPitch - newRoll;
		}
		else
		{
			leftVelocity = newPitch - newRoll;
			rightVelocity = newPitch + newRoll;
		}
		
		_listener.onVelocityChange(leftVelocity, rightVelocity);
		
	}
	
}
