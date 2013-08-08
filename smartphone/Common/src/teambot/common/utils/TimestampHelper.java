package teambot.common.utils;

public class TimestampHelper {

	public static short frameTimestampNow() {
		long timestampUnix = System.currentTimeMillis() / 10;
		
		return (short)(timestampUnix & 0xFFFF);
	}
	
	public static final float NanoSecsToMSecs = 1.0f / 1000000.0f;
}
