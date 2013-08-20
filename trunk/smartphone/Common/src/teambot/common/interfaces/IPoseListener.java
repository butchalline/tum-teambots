package teambot.common.interfaces;

import teambot.common.data.Pose;

public interface IPoseListener {
	public void poseUpdateCallback(Pose newPose);
}
