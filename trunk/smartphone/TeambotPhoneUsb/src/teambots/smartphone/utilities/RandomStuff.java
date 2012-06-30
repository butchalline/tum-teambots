package teambots.smartphone.utilities;

import teambots.smartphone.usbInterface.Message;

public class RandomStuff {

	public static Message randomDataMessage(int type)
	{
		return new Message(Message.IntToType.get(type), randomLengthRandomByteArray());
	}
	
	public static byte[] randomLengthRandomByteArray()
	{
		byte[] randomArray = randomByteArray((int) (Math.random() * 100));
		return randomArray;		
	}
	
	public static byte[] randomByteArray(int length)
	{
		byte[] randomArray = new byte[length];
		
		for(int i = 0; i < length; i++)
			randomArray[i] = (byte) Math.abs(256 / Math.random() + 0.01);
		return randomArray;		
	}
	
}
