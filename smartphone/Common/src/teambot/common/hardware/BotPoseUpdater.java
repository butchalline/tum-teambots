package teambot.common.hardware;

import teambot.common.BotLayoutConstants;
import teambot.common.PoseSupplier;
import teambot.common.data.Pose;
import teambot.common.usb.UsbHeader;
import teambot.common.usb.UsbPacket;
import teambot.common.utils.Constants;

public class BotPoseUpdater 
{
	protected int _lastTimeStamp = -1;
	
    /**
     * d = (dR + dL) / 2
     * angle = (dR - dL) / wheel distance
     *
     * 
     * x = x + d * cos(angle)
     * y = y + d * sin(angle)
     * angle = angle + angle
     */
	public Pose updatePose(Pose oldPose, UsbPacket packetWithWheelChanges)
	{
		if(_lastTimeStamp == -1)
		{
			_lastTimeStamp = packetWithWheelChanges.getHeader().getTimestamp();
			return oldPose;
		}
		
		float deltaT = packetWithWheelChanges.getHeader().getTimestamp() - _lastTimeStamp;
		byte[] value = packetWithWheelChanges.getData().asByteArray();
		float leftChange = ((value[0] << 8) | value[1]) / deltaT;
		float rightChange = ((value[2] << 8) | value[3]) / deltaT;

		float d = (rightChange + leftChange) * 0.5f;
		float angle = (rightChange - leftChange) / BotLayoutConstants.DistanceBeweenWheelCenters_mm;
		
		Pose newPose = new Pose(oldPose);
		newPose.addToX(d * (float)Math.cos(angle));
		newPose.addToY(d * (float)Math.sin(angle));
		newPose.addToAngleInRadian_normalized(angle);
		
		return newPose;
	}
}
