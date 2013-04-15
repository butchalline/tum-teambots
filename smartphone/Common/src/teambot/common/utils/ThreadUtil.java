package teambot.common.utils;

public class ThreadUtil {

	static public void sleepSecs(float secs) {
		try {
			Thread.sleep((long)(secs * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
