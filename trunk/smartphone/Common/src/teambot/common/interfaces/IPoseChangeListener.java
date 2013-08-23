package teambot.common.interfaces;

import teambot.common.data.Pose;

public interface IPoseChangeListener
{
	public void poseChangeCallback(Pose poseChange, boolean changeIsRelativeToTheLastPosition);
}
