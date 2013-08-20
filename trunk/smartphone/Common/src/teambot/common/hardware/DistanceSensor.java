package teambot.common.hardware;

import teambot.common.PoseSupplier;
import teambot.common.interfaces.IPacketListener;
import teambot.common.interfaces.ISensorListener;
import teambot.common.usb.UsbHeader;
import teambot.common.usb.UsbPacket;


/**
 * Class representing the distance sensor of the bot and also the bumpers
 * 
 * @author user
 *
 */
public class DistanceSensor extends BotSensor implements IPacketListener
{

	public DistanceSensor(PoseSupplier poseSupplier)
	{
		super(poseSupplier);
	}

	@Override
	public void newPacketCallback(UsbPacket packet)
	{
		if(packet.getHeader().compareTo(UsbHeader.TB_DATA_INFRARED) != 0)
		{
			throw new IllegalArgumentException();
		}
		
		//TODO
		
		SensorValue newValue = new SensorValue(packet.getData().asByteArray()[0], packet.getHeader().getTimestamp());
		
		setValue(newValue);
		
		for(ISensorListener listener : _listeners)
			listener.newSensorValueCallback(newValue);
	}

	/**
	 * Register method for the sensor values. The values for the distance sensor are in mm.
	 */
	public synchronized void registerForSensorValues_mm(ISensorListener listener)
	{
		_listeners.add(listener);
	}
}
