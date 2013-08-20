package teambot.common.utils;

public class TimestampHelper {

	public static short frameTimestampNow() {
		long timestampUnix = System.currentTimeMillis() / 10;
		
		return (short)(timestampUnix & 0xFFFF);
	}	
}
