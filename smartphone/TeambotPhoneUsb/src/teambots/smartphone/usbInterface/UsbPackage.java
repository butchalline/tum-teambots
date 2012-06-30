package teambots.smartphone.usbInterface;

import org.apache.commons.lang3.ArrayUtils;

public class UsbPackage {

	public Message.Type packageType;
	public byte[] data;
	
	UsbPackage()
	{
		
	}
	
	public UsbPackage(byte type, byte[] data)
	{
		this.packageType = Message.IntToType.get((int)type);
		this.data = data;
	}
	
	public byte[] asDataStream()
	{
		byte[] header = new byte[2];
		header[0] = (byte)packageType.id;
		header[1] = (byte)this.data.length;
		
		return ArrayUtils.addAll(header, this.data);
	}
}
