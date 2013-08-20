package teambot.common.usb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import teambot.common.interfaces.IPacketListener;
import teambot.common.utils.SimpleEndlessThread;

/**
 * 
 * This class is basically a interface between the class UsbConnectionParser and
 * and the other system components. They can register here for a certain kind of
 * packet via specifying the header when registering. This is not directly
 * implemented into UsbConnectionParser to avoid making it too slow to catch up
 * with the packet stream from the USB connection.
 * 
 * 
 * @see UsbConnectionParser
 * @author Alex
 * 
 */
public class PacketDistribution extends SimpleEndlessThread implements IPacketListener
{
	protected UsbConnectionParser _packetSupplier;
	protected Map<UsbHeader, LinkedList<IPacketListener>> _listeners = new HashMap<UsbHeader, LinkedList<IPacketListener>>(
			UsbHeader.values().length);

	public PacketDistribution(UsbConnectionParser packetSupplier)
	{
		super();
		initializeListenerMap();
		_packetSupplier = packetSupplier;
	}

	private void initializeListenerMap()
	{
		for (UsbHeader header : UsbHeader.values())
			_listeners.put(header, new LinkedList<IPacketListener>());
	}

	public synchronized void register(IPacketListener listener, UsbHeader packetType)
	{
		_listeners.get(packetType).add(listener);
	}

	public synchronized void unRegister(IPacketListener listener, UsbHeader packetType)
	{
		_listeners.get(packetType).remove(listener);
	}

	@Override
	protected void doInThreadLoop()
	{
		if(_packetSupplier._packets.size() == 0)
		{
			try
			{
				synchronized (_thread)
				{
					_thread.wait();
				}				
			} catch (InterruptedException e)
			{
			}
		}
		
		while(!_packetSupplier._packets.isEmpty())
		{
			UsbPacket packet = _packetSupplier._packets.poll();
			for(IPacketListener listener : _listeners.get(packet._header))
				listener.newPacketCallback(packet);
		}
	}

	@Override
	public synchronized void newPacketCallback(UsbPacket packet)
	{
		synchronized (_thread)
		{
			_thread.notify();
		}			
	}
}
