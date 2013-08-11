package teambot.common.interfaces;

import teambot.common.usb.UsbPacket;

public interface IPacketListener
{
	public void newPacketCallback(UsbPacket packet);
}
