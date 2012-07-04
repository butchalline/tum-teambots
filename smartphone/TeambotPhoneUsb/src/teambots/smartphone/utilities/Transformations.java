package teambots.smartphone.utilities;

public class Transformations {

	public static int twoBytesToInt(byte lowerByte, byte upperByte)
	{
		int result = upperByte;
		result = upperByte << 8;
		result += lowerByte;
		return result;
	}
	
	public static int signedByteToUnsignedInt(byte value)
	{
		if(value < 0)
			return -(int)value + 128;
		else
			return (int)value;
	}
	
	public static byte unsignedIntToSignedByte(int value)
	{
		if(value > 128)
			return (byte)-(value-128);
		else
			return (byte)value;
	}
}
