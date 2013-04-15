package teambot.common.utils;


public class ByteHelper {

	public static byte checkedByteConversion(int value) throws IllegalByteValueException{
		
		if(value >= -128 && value <= 127)
			return (byte)value;
			
		throw new IllegalByteValueException();
	}
	
	public static byte checkedByteConversion(long value) throws IllegalByteValueException{
		
		if(value >= -128 && value <= 127)
			return (byte)value;
			
		throw new IllegalByteValueException();
	}
}
