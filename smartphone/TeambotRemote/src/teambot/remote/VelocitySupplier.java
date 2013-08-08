package teambot.remote;

public class VelocitySupplier implements IPitchRollListener
{
	IVelocityListener _listener;
	
	public VelocitySupplier(IVelocityListener listener){
		_listener = listener;
	}
	
	public enum Direction {
		FORWARD,
		BACKWARDS,
		TURN_LEFT,
		TURN_RIGHT
	}
	
	@Override
	public void onPitchRollChange(float newPitch, float newRoll)
	{
		Direction direction = Direction.FORWARD;
		byte rightSpeed = 0;
		byte leftSpeed = 0;

		boolean turnLeft = (newRoll >= 0) ? true : false;
		
		newPitch *= -1;		
		float velocity = newPitch + newRoll;
		float secondVelocity = newPitch - newRoll;
		float leftVelocity = 0;
		float rightVelocity = 0;
		
		boolean sameSign = hasSameSign(velocity, secondVelocity);
		
		//go forward
		if(newPitch >= 0 && sameSign) {
			direction = Direction.FORWARD;
			if(turnLeft) {
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
		// go backwards
		if(newPitch < 0 && sameSign) {
			direction = Direction.BACKWARDS;
			if(turnLeft) {
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
		//turn forward
		if(newPitch >= 0 && !sameSign) {			
			if(turnLeft) {
				direction = Direction.TURN_LEFT;
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				direction = Direction.TURN_RIGHT;
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
		//turn backwards		
		if(newPitch < 0 && !sameSign) {
			if(turnLeft) {
				 //yes it has to be turn right, although we turn left.. kind of
				direction = Direction.TURN_RIGHT;
				leftVelocity = secondVelocity;
				rightVelocity = velocity;
			}
			else {
				 //yes it has to be turn left, although we turn right.. kind of
				direction = Direction.TURN_LEFT;
				leftVelocity = velocity;
				rightVelocity = secondVelocity;
			}
		}
		
		rightSpeed = sensorValueTobyteValue(Math.abs(rightVelocity));
		leftSpeed = sensorValueTobyteValue(Math.abs(leftVelocity));		

		_listener.onVelocityChange(direction, rightSpeed, leftSpeed);
	}
	
	protected boolean hasSameSign(float value1, float value2) {
		if(value1 < 0 && value2 < 0)
			return true;
		if(value1 >= 0 && value2 >= 0)
			return true;
		return false;
	}
	
	protected byte sensorValueTobyteValue(float sensorValue) {
		return (byte) (Math.abs(sensorValue) * 20);
	}
	
}
