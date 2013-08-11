package teambot.common.interfaces;

import teambot.common.data.Pose;

public interface IPoseListener {
	public void callback_PoseChanged(Pose newPose);
}
