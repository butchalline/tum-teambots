package teambots.smartphone.utilities;

public class Transformations {

	public static int twoBytesToInt(byte lowerByte, byte upperByte)
	{
		int result = upperByte;
		result = upperByte << 8;
		result += lowerByte;
		return result;
	}
}
