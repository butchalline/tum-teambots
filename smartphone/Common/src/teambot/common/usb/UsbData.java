package teambot.common.usb;

import teambot.common.utils.ByteHelper;
import teambot.common.utils.IllegalByteValueException;

public class UsbData {

	protected byte[] data;
	
	public UsbData(int[] data) throws IllegalByteValueException {
		setData(data);
	}
	
	protected void setData(int[] data) throws IllegalByteValueException {
		this.data = new byte[data.length];
		for(int i = 0; i < data.length; i++) {
			this.data[i] = ByteHelper.checkedByteConversion(data[i]);
		}		
	}
	
	public byte[] asByteArray() {
		return data;
	}
}
