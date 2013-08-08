package teambot.remote;

public class VelocityToPacketValues
{
	public enum Direction
	{
		FORWARD, BACKWARDS, TURN_LEFT, TURN_RIGHT
	}

	public static byte[] convert(float leftVelocity, float rightVelocity)
	{
		byte[] packet =
		{ 0, 0, 0 };

		if (leftVelocity >= 0 && rightVelocity >= 0)
			packet[0] = (byte) Direction.FORWARD.ordinal();
		else if (leftVelocity < 0 && rightVelocity < 0)
			packet[0] = (byte) Direction.BACKWARDS.ordinal();
		else if (leftVelocity < 0 && rightVelocity >= 0)
			packet[0] = (byte)Direction.TURN_LEFT.ordinal();
		else if (leftVelocity >= 0 && rightVelocity < 0)
			packet[0] = (byte)Direction.TURN_RIGHT.ordinal();

		packet[1] = sensorValueTobyteValue(leftVelocity);
		packet[2] = sensorValueTobyteValue(rightVelocity);

		return packet;
	}

	protected static byte sensorValueTobyteValue(float sensorValue)
	{
		return (byte) (Math.abs(sensorValue) * 20);
	}
}
