package teambot.common.usb;

import org.apache.commons.lang3.ArrayUtils;

public class UsbPacket {

	UsbHeader _header;
	UsbData _data;
	
	public UsbPacket(UsbHeader header, UsbData data) {
		_header = header;
		_data = data;
	}
	
	public UsbHeader getHeader()
	{
		return _header;
	}
	
	public UsbData getData()
	{
		return _data;
	}
	
	public byte[] asByteArray() {
		return ArrayUtils.addAll(_header.asByteArray(), _data.asByteArray());
	}
}
