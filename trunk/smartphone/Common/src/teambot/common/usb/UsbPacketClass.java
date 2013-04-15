package teambot.common.usb;

import org.apache.commons.lang3.ArrayUtils;

public class UsbPacketClass {

	UsbHeader header;
	UsbData data;
	
	public UsbPacketClass(UsbHeader header, UsbData data) {
		this.header = header;
		this.data = data;
	}
	
	public byte[] asByteArray() {
		return ArrayUtils.addAll(header.asByteArray(), data.asByteArray());
	}
}
