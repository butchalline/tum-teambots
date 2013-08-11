package teambot.common.usb;

import teambot.common.utils.ByteHelper;
import teambot.common.utils.IllegalByteValueException;

public class UsbData {

	protected byte[] _data;
	
	public UsbData(int[] data) throws IllegalByteValueException {
		setData(data);
	}
	
	public UsbData(byte[] data) {
		_data = data;
	}
	
	protected void setData(int[] data) throws IllegalByteValueException {
		_data = new byte[data.length];
		for(int i = 0; i < data.length; i++) {
			_data[i] = ByteHelper.checkedByteConversion(data[i]);
		}		
	}
	
	public byte[] asByteArray() {
		return _data;
	}
}
