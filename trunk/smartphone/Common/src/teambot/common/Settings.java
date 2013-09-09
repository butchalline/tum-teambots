package teambot.common;

public class Settings
{
	public static final String botPort = "5600";
	public static final String remoteControlPort = "5700";
	public static final String streamingPort = "5800";
	public static final boolean debugIceConnections = false; //true is very demanding
	public static final int sleepTimeBetweenBotLookUps_ms = 10000;
	public static final int displayInfoRefreshRate_hz = 10;
	public static final int mapOffsetY = 10000;

	public static int timoutOnSingleBotLookUp_ms = 50;
	
	public static final int numberOfParticles = 100;
	public static final int soundBufferSizeMultiplier = 2;
}
