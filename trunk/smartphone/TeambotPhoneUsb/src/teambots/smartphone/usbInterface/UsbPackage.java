package teambots.smartphone.usbInterface;

import org.apache.commons.lang3.ArrayUtils;

import teambots.smartphone.utilities.Transformations;

public class UsbPackage {

	static final int headerSize = 4;
	
	public Message.Type packageType;
	public byte[] data;
	
	UsbPackage()
	{
		
	}
	
	public UsbPackage(int type, byte[] data)
	{
		this.packageType = Message.IntIdToType.get(type);
		this.data = data;
	}
	
	public byte[] asDataStream()
	{
		byte[] header = new byte[headerSize];
		header[0] = Transformations.unsignedIntToSignedByte(packageType.id & 0xFF00);
		header[1] = Transformations.unsignedIntToSignedByte(packageType.id & 0xFF);
		header[2] = Transformations.unsignedIntToSignedByte(42);
		header[3] = Transformations.unsignedIntToSignedByte(42);
		//TODO time stamp of header?
		
		return ArrayUtils.addAll(header, this.data);
	}
}
