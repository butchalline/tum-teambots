package teambot.common.interfaces;

import java.io.IOException;

import teambot.common.usb.UsbPacketClass;

public interface IUsbIO {
	
	public int read(byte[] buffer) throws IOException;
	public void write(byte[] buffer) throws IOException;
}
