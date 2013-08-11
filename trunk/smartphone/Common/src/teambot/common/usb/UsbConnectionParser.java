package teambot.common.usb;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import teambot.common.SimpleEndlessThread;
import teambot.common.interfaces.IPacketListener;
import teambot.common.interfaces.IUsbIO;
import teambot.common.utils.ThreadUtil;

/**
 * @author Alex
 *
 */
public class UsbConnectionParser extends SimpleEndlessThread
{
	public Queue<UsbPacket> _packets = new ConcurrentLinkedQueue<UsbPacket>();
	
	protected IUsbIO _usbIo;
	protected IPacketListener _newPacketsListener;
	protected byte[] _readBuffer;
	protected int _readBytes;
	protected ArrayDeque<Byte> _buffer = new ArrayDeque<Byte>(512);
	protected int _headerSize = UsbHeader.getHeaderLength();
	 
	
	public UsbConnectionParser(IUsbIO usbIo, IPacketListener newPacketsListener)
	{
		_usbIo = usbIo;
		_readBuffer = new byte[255];
		_newPacketsListener = newPacketsListener;
	}

	@Override
	protected void doInThreadLoop()
	{
		try
		{
			_readBytes = _usbIo.read(_readBuffer);
		} catch (IOException e)
		{
			ThreadUtil.sleepSecs(0.1f);
			return;
		}
		
		if(_readBytes == 0)
		{
			ThreadUtil.sleepMSecs(1f);
			return;
		}
		
		for(int i = 0; i < _readBytes; ++i)
		{
			_buffer.add(_readBuffer[i]);
		}
		extractPackets();
	}
	
	protected void extractPackets()
	{
		byte id;
		byte subId;
		UsbHeader tempHeader;
		byte[] tempData;
		
		while (_buffer.size() >= UsbHeader.getHeaderLength())
		{
			id = _buffer.pollFirst();
			subId = _buffer.pollFirst();
			
			tempHeader = UsbHeader.getHeader(id, subId);
			
			if(_buffer.size() < tempHeader.getDataByteCount() + UsbHeader.timeStampSize)
			{
				_buffer.addFirst(subId);
				_buffer.addFirst(id);
				return;
			}
			
			tempHeader.setTimestamp(_buffer.pollFirst(), _buffer.pollFirst());
			
			tempData = new byte[tempHeader.getDataByteCount()];
			for(int d = 0; d < tempData.length; ++d)
				tempData[d] = _buffer.pollFirst();
				
			_packets.add(new UsbPacket(tempHeader, new UsbData(tempData)));
		}
		_newPacketsListener.newPacketCallback(null);
	}
}
